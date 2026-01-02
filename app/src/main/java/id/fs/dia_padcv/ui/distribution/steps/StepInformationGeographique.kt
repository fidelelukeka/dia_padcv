package id.fs.dia_padcv.ui.distribution.steps

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.fs.dia_padcv.ui.AppViewModel
import id.fs.dia_padcv.ui.components.SearchableDropdownField
import id.fs.dia_padcv.ui.components.fields.RequiredLocationField
import id.fs.dia_padcv.ui.components.fields.RequiredPhotoField
import id.fs.dia_padcv.ui.utils.saveBitmapToLocal
import id.fs.dia_padcv.ui.utils.saveGalleryImageToLocal
import id.fs.dia_padcv.ui.utils.useLocation
import androidx.core.net.toUri

@Composable
fun StepInformationGeographique(viewModel: AppViewModel) {

    val distribution by viewModel.currentDistribution.collectAsState()
    val context = LocalContext.current

    val sites by viewModel.sites.collectAsState()
    val selectedSite by viewModel.selectedSite

    val (locationState, fetchLocation, isLoadingLocation) = useLocation()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            runCatching {
                saveBitmapToLocal(context, it, prefix = "distribution")
            }.onSuccess { savedUri ->
                viewModel.updateDistribution(
                    distribution.copy(image = savedUri.toString()) // ✅ stocke en String
                )
                Toast.makeText(context, "Photo enregistrée", Toast.LENGTH_SHORT).show()
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
                viewModel.updateDistribution(
                    distribution.copy(image = savedUri.toString()) // ✅ stocke en String
                )
                Toast.makeText(context, "Image importée et enregistrée", Toast.LENGTH_SHORT).show()
            }
        }
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

        // ✅ Champ site obligatoire
        SearchableDropdownField(
            label = "Rechercher un site",
            items = sites,
            selectedItem = selectedSite,
            itemLabel = { it.nameWarehouse },
            onItemSelected = { site -> viewModel.selectSite(site) },
            onClearSelection = { viewModel.clearSelectedSite() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Champ localisation obligatoire
        RequiredLocationField(
            locationState = locationState,
            isLoading = isLoadingLocation,
            fetchLocation = { fetchLocation() },
            viewModel = viewModel,
            distribution = distribution
        )

        // ✅ Champ image obligatoire (utilise distribution.image)
        RequiredPhotoField(
            imageUri = distribution.image?.toUri(), // ✅ conversion String → Uri
            cameraLauncher = cameraLauncher,
            galleryLauncher = galleryLauncher,
            onClear = {
                viewModel.updateDistribution(distribution.copy(image = null))
            }
        )
    }
}