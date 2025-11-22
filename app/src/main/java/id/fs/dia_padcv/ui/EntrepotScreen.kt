package id.fs.dia_padcv.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import id.fs.dia_padcv.data.local.entities.Entrepot
import id.fs.dia_padcv.data.remote.api.Village
import id.fs.dia_padcv.ui.components.DropdownSelector
import id.fs.dia_padcv.ui.components.SearchableDropdownField
import id.fs.dia_padcv.ui.utils.saveBitmapToLocal
import id.fs.dia_padcv.ui.utils.saveGalleryImageToLocal
import id.fs.dia_padcv.ui.utils.useLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrepotScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    var nameWarehouse by remember { mutableStateOf("") }
    var nameResponsable by remember { mutableStateOf("") }
    var typeEntrepot by remember { mutableStateOf("Provincial") }
    var villageSearch by remember { mutableStateOf("") }

    val villages by viewModel.villages.collectAsState(initial = emptyList())
    val selectedVillage by viewModel.selectedVillage
    val filteredVillages = villages.filter { it.name_village.contains(villageSearch, ignoreCase = true) }

    val (locationState, fetchLocation, isLoadingLocation) = useLocation()
    val context = LocalContext.current

    // --- Images ---
    val imageUris = remember { mutableStateListOf<Uri>() }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val savedUri = saveBitmapToLocal(context, it, prefix = "entrepot")
            imageUris.add(savedUri)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val savedUri = saveGalleryImageToLocal(context, it)
            imageUris.add(savedUri)
        }
    }

    // 🔹 Charger les villages au démarrage
    LaunchedEffect(Unit) {
        viewModel.fetchVillages(context)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { Text("Ajouter un entrepôt", style = MaterialTheme.typography.titleLarge) }

        item {
            OutlinedTextField(
                value = nameWarehouse,
                onValueChange = { nameWarehouse = it },
                label = { Text("Nom de l'entrepôt") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = nameResponsable,
                onValueChange = { nameResponsable = it },
                label = { Text("Nom du responsable") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- Champ village ---
        item {
            SearchableDropdownField(
                label = "Rechercher un village",
                items = villages,
                selectedItem = selectedVillage,
                itemLabel = { it.name_village },
                onItemSelected = { village -> viewModel.selectVillage(village) },
                onClearSelection = { viewModel.clearSelectedVillage() }
            )
        }
        item {
            DropdownSelector(
                label = "Type d'entrepôt",
                options = listOf("Provincial", "Communautaire"),
                selectedOption = typeEntrepot,
                onOptionSelected = { typeEntrepot = it },
                placeholder = "Sélectionner le type"
            )
        }
        item {
            Button(onClick = { fetchLocation() }, modifier = Modifier.fillMaxWidth()) {
                Text(if (isLoadingLocation) "⏳ Récupération..." else "📍 Récupérer la localisation")
            }
        }

        item {
            if (locationState.latitude.isNotEmpty() && locationState.longitude.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("📍 Coordonnées GPS récupérées :", style = MaterialTheme.typography.titleMedium)
                        Text("Latitude : ${locationState.latitude}")
                        Text("Longitude : ${locationState.longitude}")
                        Text("Altitude : ${locationState.altitude.ifEmpty { "N/A" }} m")
                        Text("Précision : ${locationState.precision.ifEmpty { "N/A" }} m")
                    }
                }
            }
        }

        item {
            Text("Photos de l'entrepôt", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { cameraLauncher.launch(null) }, modifier = Modifier.fillMaxWidth()) {
                Text("📸 Prendre une photo")
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(imageUris.size) { index ->
                    val uri = imageUris[index]
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Photo entrepôt",
                        modifier = Modifier.size(500.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        item {
            Button(onClick = {
                val entrepot = Entrepot(
                    nameWarehouse = nameWarehouse,
                    nameResponsable = nameResponsable,
                    type = typeEntrepot,
                    latitude = locationState.latitude.toDoubleOrNull(),
                    longitude = locationState.longitude.toDoubleOrNull(),
                    altitude = locationState.altitude.toDoubleOrNull(),
                    precision = locationState.precision.toDoubleOrNull(),
                    geopoint = if (locationState.latitude.isNotEmpty() && locationState.longitude.isNotEmpty())
                        "${locationState.latitude},${locationState.longitude},${locationState.altitude}" else null,
                    photoPath = imageUris.joinToString(",") { it.toString() }
                )

                viewModel.addEntrepot(context, entrepot)
                viewModel.createWarehouseFromEntrepot(context, entrepot) // 🔹 API + compression
                onBack()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Enregistrer")
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onBack() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Annuler", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}
