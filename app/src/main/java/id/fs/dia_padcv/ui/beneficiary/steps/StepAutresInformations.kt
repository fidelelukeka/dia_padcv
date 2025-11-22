package id.fs.dia_padcv.ui.beneficiary.steps

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
import id.fs.dia_padcv.ui.utils.QrCodeView
import id.fs.dia_padcv.ui.utils.generateQrCode
import id.fs.dia_padcv.ui.utils.saveBitmapToLocal
import id.fs.dia_padcv.ui.utils.saveGalleryImageToLocal
import id.fs.dia_padcv.ui.utils.saveQrToGallery
import id.fs.dia_padcv.ui.utils.useLocation

@Composable
fun StepAutresInformations(viewModel: AppViewModel) {
    val beneficiary by viewModel.currentBeneficiary.collectAsState()

//    // Sérialisation simple des données du bénéficiaire
//    val qrData = """
//            {
//              "numCarteBen": "${beneficiary.numCarteBen}",
//              "idBen": ${beneficiary.idBen},
//              "nom": ${beneficiary.nom},
//              "postNom": "${beneficiary.postnom}",
//              "prenom": "${beneficiary.prenom}",
//              "sexe": "${beneficiary.sexe}",
//              }
//        """.trimIndent()
//
//    val context = LocalContext.current
//    // Re-génère le QR si les données changent
//    val qrBitmap = remember(qrData) { generateQrCode(qrData) }
//
//    val villages by viewModel.villages.collectAsState(initial = emptyList())
//    val selectedVillage by viewModel.selectedVillage
//
//    val (locationState, fetchLocation, isLoadingLocation) = useLocation()
//
//    val imageUris = remember { mutableStateListOf<Uri>() }
//
//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicturePreview()
//    ) { bitmap ->
//        bitmap?.let {
//            runCatching {
//                saveBitmapToLocal(context, it, prefix = "beneficiary")
//            }.onSuccess { savedUri ->
//                imageUris.add(savedUri)
//                Toast.makeText(context, "Photo enregistrée", Toast.LENGTH_SHORT).show()
//            }.onFailure {
//                Toast.makeText(context, "Échec d'enregistrement de la photo", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    val galleryLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri ->
//        uri?.let {
//            runCatching {
//                saveGalleryImageToLocal(context, it)
//            }.onSuccess { savedUri ->
//                imageUris.add(savedUri)
//                Toast.makeText(context, "Image importée et enregistrée", Toast.LENGTH_SHORT).show()
//            }.onFailure {
//                Toast.makeText(context, "Échec d'importation de l'image", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    // Charger les villages au démarrage
//    LaunchedEffect(Unit) {
//        viewModel.fetchVillages(context)
//    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Autres informations",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
//
//        // Recherche et sélection de village (composant existant)
//        SearchableDropdownField(
//            label = "Rechercher un village",
//            items = villages,
//            selectedItem = selectedVillage,
//            itemLabel = { it.name_village },
//            onItemSelected = { village -> viewModel.selectVillage(village) },
//            onClearSelection = { viewModel.clearSelectedVillage() }
//        )
//
//        // Localisation
//        Button(onClick = { fetchLocation() }, modifier = Modifier.fillMaxWidth()) {
//            Text(if (isLoadingLocation) "Récupération..." else "Récupérer la localisation")
//        }
//
//        if (locationState.latitude.isNotEmpty() && locationState.longitude.isNotEmpty()) {
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 8.dp),
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
//            ) {
//                Column(modifier = Modifier.padding(12.dp)) {
//                    Text("Coordonnées GPS récupérées :", style = MaterialTheme.typography.titleMedium)
//                    Text("Latitude : ${locationState.latitude}")
//                    Text("Longitude : ${locationState.longitude}")
//                    Text("Altitude : ${locationState.altitude.ifEmpty { "N/A" }} m")
//                    Text("Précision : ${locationState.precision.ifEmpty { "N/A" }} m")
//                }
//            }
//        }
//
//        // Photos
//        Text("Photos du bénéficiaire", style = MaterialTheme.typography.titleMedium)
//
//        Button(onClick = { cameraLauncher.launch(null) }, modifier = Modifier.fillMaxWidth()) {
//            Text("Prendre la photo du bénéficiaire")
//        }
//
//        Button(onClick = { galleryLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
//            Text("Importer une photo depuis la galerie")
//        }
//
//        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//            items(imageUris.size) { index ->
//                val uri = imageUris[index]
//                Image(
//                    painter = rememberAsyncImagePainter(uri),
//                    contentDescription = "Photo du bénéficiaire #${beneficiary.idBen}",
//                    modifier = Modifier.size(160.dp),
//                    contentScale = ContentScale.Crop
//                )
//            }
//        }
//
//        // QR Code
//        QrCodeView(data = qrData)
//
//        Button(onClick = {
//            val success = runCatching {
//                saveQrToGallery(context, qrBitmap, "beneficiary_qr_${beneficiary.idBen}.png")
//            }.getOrElse { false }
//
//            Toast.makeText(
//                context,
//                if (success) "QR sauvegardé dans la galerie" else "Échec de sauvegarde du QR",
//                Toast.LENGTH_SHORT
//            ).show()
//        }) {
//            Text("Sauvegarder le QR")
//        }
    }
}
