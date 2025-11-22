package id.fs.dia_padcv.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import id.fs.dia_padcv.data.local.entities.Entrepot
import id.fs.dia_padcv.ui.AppViewModel
import id.fs.dia_padcv.ui.components.DropdownSelector
import id.fs.dia_padcv.ui.components.SearchableDropdownField
import id.fs.dia_padcv.ui.utils.saveBitmapToLocal
import id.fs.dia_padcv.ui.utils.saveGalleryImageToLocal
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import id.fs.dia_padcv.data.local.entities.Colis
import id.fs.dia_padcv.ui.components.DropdownSelector
import id.fs.dia_padcv.ui.components.SearchableDropdownField
import id.fs.dia_padcv.ui.utils.saveBitmapToLocal
import id.fs.dia_padcv.ui.utils.saveGalleryImageToLocal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val TAG = "PackageScreen"
    val context = LocalContext.current

    var errorMessage by remember { mutableStateOf<String?>(null) }

    var produitType by remember { mutableStateOf("") }
    var produitContenu by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var quality by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    val entrepots by viewModel.entrepots.collectAsState(initial = emptyList())
    val selectedEntrepot by viewModel.selectedEntrepot.collectAsState(initial = null)

    // ✅ On ne garde qu’un seul QR Code pour cohérence avec l’API
    var qrCodeUri by remember { mutableStateOf<Uri?>(null) }

    // 🔹 Caméra
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            try {
                val savedUri = saveBitmapToLocal(context, it, prefix = "colis")
                qrCodeUri = savedUri
                Log.d(TAG, "📸 QR Code enregistré depuis caméra : $savedUri")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur capture caméra", e)
                errorMessage = "Erreur lors de la capture photo."
            }
        }
    }

    // 🔹 Galerie
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val savedUri = saveGalleryImageToLocal(context, it)
                qrCodeUri = savedUri
                Log.d(TAG, "🖼 QR Code importé depuis galerie : $savedUri")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur sélection galerie", e)
                errorMessage = "Erreur lors de la sélection d'image."
            }
        }
    }

    // 🔹 Charger les entrepôts
    LaunchedEffect(Unit) {
        try {
            viewModel.fetchEntrepots()
        } catch (e: Exception) {
            Log.e(TAG, "Erreur fetch entrepôts", e)
            errorMessage = "Impossible de charger les entrepôts."
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { Text("📦 Enregistrer un colis", style = MaterialTheme.typography.titleLarge) }

        item {
            DropdownSelector(
                label = "Type de produit",
                options = listOf("Sémence", "Engrais"),
                selectedOption = produitType,
                onOptionSelected = { produitType = it }
            )
        }

        item {
            OutlinedTextField(
                value = produitContenu,
                onValueChange = { produitContenu = it },
                label = { Text("Contenu du colis") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantité") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            DropdownSelector(
                label = "Unité",
                options = listOf("kg", "litre", "pièce"),
                selectedOption = unit,
                onOptionSelected = { unit = it }
            )
        }

        item {
            DropdownSelector(
                label = "Qualité du produit",
                options = listOf("Bon", "Mauvais"),
                selectedOption = quality,
                onOptionSelected = { quality = it }
            )
        }

        item {
            DropdownSelector(
                label = "Statut du colis",
                options = listOf("En stock", "Expédié", "Livré", "Endommagé"),
                selectedOption = status,
                onOptionSelected = { status = it }
            )
        }

        item {
            SearchableDropdownField(
                label = "Rechercher un entrepôt",
                items = entrepots,
                selectedItem = selectedEntrepot,
                itemLabel = { it.name_warehouse },
                onItemSelected = { entrepot -> viewModel.selectEntrepot(entrepot) },
                onClearSelection = { viewModel.clearSelectedEntrepot() }
            )
        }

        // 🔹 QR CODE IMAGE
        item {
            Text("QR Code du colis", style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { cameraLauncher.launch(null) }, modifier = Modifier.weight(1f)) {
                    Text("📸 Caméra")
                }
                Button(onClick = { galleryLauncher.launch("image/*") }, modifier = Modifier.weight(1f)) {
                    Text("🖼 Galerie")
                }
            }

            qrCodeUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "QR Code du colis",
                    modifier = Modifier
                        .size(180.dp)
                        .padding(top = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // 🔹 Boutons d’action
        item {
            Button(
                onClick = {
                    try {
                        if (selectedEntrepot == null) {
                            errorMessage = "⚠️ Aucun entrepôt sélectionné"
                            return@Button
                        }

                        val warehouseIdF = selectedEntrepot!!.warehouse_id.toDoubleOrNull()?.toInt()
                            ?: run {
                                errorMessage = "⚠️ ID entrepôt invalide"
                                return@Button
                            }

                        val qty = quantity.toIntOrNull()
                        if (qty == null) {
                            errorMessage = "⚠️ Quantité invalide"
                            return@Button
                        }

                        val colis = Colis(
                            qrCode = qrCodeUri?.toString() ?: "",
                            produitType = produitType,
                            produitContenu = produitContenu,
                            quantity = qty,
                            unit = unit,
                            quality = quality,
                            status = status,
                            warehouseIdF = warehouseIdF
                        )

                        viewModel.addPackage(context, colis)
                        Toast.makeText(context, "✅ Colis enregistré avec succès", Toast.LENGTH_SHORT).show()
                        onBack()

                    } catch (e: Exception) {
                        Log.e(TAG, "Erreur enregistrement colis", e)
                        errorMessage = "Erreur lors de l'enregistrement du colis."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enregistrer le colis")
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

    // 🔹 Dialogue d’erreur
    errorMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            confirmButton = { TextButton(onClick = { errorMessage = null }) { Text("OK") } },
            title = { Text("Erreur") },
            text = { Text(msg) }
        )
    }
}
