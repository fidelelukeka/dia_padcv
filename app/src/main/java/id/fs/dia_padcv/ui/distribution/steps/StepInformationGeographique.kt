package id.fs.dia_padcv.ui.distribution.steps

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import id.fs.dia_padcv.ui.AppViewModel
import id.fs.dia_padcv.ui.components.SearchableDropdownField
import id.fs.dia_padcv.ui.utils.saveBitmapToLocal
import id.fs.dia_padcv.ui.utils.saveGalleryImageToLocal
import id.fs.dia_padcv.ui.utils.useLocation

@Composable
fun StepInformationGeographique(viewModel: AppViewModel) {

    val distribution by viewModel.currentDistribution.collectAsState()


    val context = LocalContext.current

    val sites by viewModel.sites.collectAsState()
    val selectedSite by viewModel.selectedSite

    val (locationState, fetchLocation, isLoadingLocation) = useLocation()

    val imageUris = remember { mutableStateListOf<Uri>() }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            runCatching {
                saveBitmapToLocal(context, it, prefix = "distribution")
            }.onSuccess { savedUri ->
                imageUris.add(savedUri)
                viewModel.updateImage(savedUri.toString()) // ✅ mise à jour Distribution
                Toast.makeText(context, "Photo enregistrée", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(context, "Échec d'enregistrement de la photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            runCatching {
                saveGalleryImageToLocal(context, it)
            }.onSuccess { savedUri ->
                imageUris.add(savedUri)
                viewModel.updateImage(savedUri.toString()) // ✅ mise à jour Distribution
                Toast.makeText(context, "Image importée et enregistrée", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(context, "Échec d'importation de l'image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Charger les sites au démarrage
    LaunchedEffect(Unit) {
        viewModel.fetchSites(context)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Informations géographiques",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )

        SearchableDropdownField(
            label = "Rechercher un site",
            items = sites,
            selectedItem = selectedSite,
            itemLabel = { it.nameWarehouse },
            onItemSelected = { site -> viewModel.selectSite(site) },
            onClearSelection = { viewModel.clearSelectedSite() }
        )

        // Localisation
        Button(onClick = {
            fetchLocation()
            if (locationState.latitude.isNotEmpty() && locationState.longitude.isNotEmpty()) {
                viewModel.updateDistribution(distribution.copy(
                        latitude = locationState.latitude,
                        longitude = locationState.longitude,
                        altitude = locationState.altitude,
                        precision = locationState.precision
                    )
                )
                Toast.makeText(context, "Coordonnées GPS enregistrées", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(if (isLoadingLocation) "Récupération..." else "Récupérer la localisation")
        }


        if (locationState.latitude.isNotEmpty() && locationState.longitude.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Coordonnées GPS récupérées :", style = MaterialTheme.typography.titleMedium)
                    Text("Latitude : ${locationState.latitude}")
                    Text("Longitude : ${locationState.longitude}")
                    Text("Altitude : ${locationState.altitude.ifEmpty { "N/A" }} m")
                    Text("Précision : ${locationState.precision.ifEmpty { "N/A" }} m")
                }
            }
        }

        // Photos
        Text("Photo de la distribution", style = MaterialTheme.typography.titleMedium)

        Button(onClick = { cameraLauncher.launch(null) }, modifier = Modifier.fillMaxWidth()) {
            Text("Prendre une photo")
        }

        Button(onClick = { galleryLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text("Importer une photo depuis la galerie")
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(imageUris.size) { index ->
                val uri = imageUris[index]
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Photo distribution $index",
                    modifier = Modifier.size(160.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // ✅ Bouton pour supprimer la photo
        Button(onClick = { viewModel.clearImage() }, modifier = Modifier.fillMaxWidth()) {
            Text("Supprimer la photo")
        }
    }
}
