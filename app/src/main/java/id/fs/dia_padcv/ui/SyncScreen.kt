package id.fs.dia_padcv.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.fs.dia_padcv.data.local.entities.Distribution
import id.fs.dia_padcv.data.local.entities.SiteEntity
import id.fs.dia_padcv.data.local.entities.Village
import id.fs.dia_padcv.data.local.entities.SyncStatus
import id.fs.dia_padcv.ui.components.AppScaffold
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val sites by viewModel.sites.collectAsState(initial = emptyList())
    val villages by viewModel.villages.collectAsState(initial = emptyList())
    val distributions by viewModel.distributions.collectAsState(initial = emptyList())
    val uiMessage by viewModel.uiMessage.collectAsState()

    val isSyncing by viewModel.isSyncing.collectAsState()   // ✅ observe depuis ViewModel
    val progress by viewModel.progress.collectAsState()
    val currentStepLabel by viewModel.currentStepLabel.collectAsState()
    val syncResults by viewModel.syncResults.collectAsState()
    val syncEvent = viewModel.syncEvent.collectAsState(initial = null)


    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("All") }
    var showSyncResults by remember { mutableStateOf(false) }


    val snackbarHostState = remember { SnackbarHostState() }

    AppScaffold(
        title = "Synchronisation",
        onBack = onBack,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    viewModel.syncAllData() // ✅ déclenche la synchro
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Synchroniser toutes les données")
            }

            // 🔹 Affichage progress pendant la synchro
            if (isSyncing) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Synchronisation en cours... ${(progress * 100).toInt()}%")
                if (currentStepLabel != null) {
                    Text("Étape: $currentStepLabel")
                }
            }

            LaunchedEffect(syncEvent.value) {
                if (syncEvent.value != null) {
                    showSyncResults = true
                    delay(10_000)
                    showSyncResults = false
                }
            }

            AnimatedVisibility(
                visible = showSyncResults,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    val totalSteps = syncResults.size
                    val totalPending = syncResults.sumOf { it.pending }
                    val totalSuccess = syncResults.sumOf { it.success }
                    val totalFailed = syncResults.sumOf { it.failed }

                    Text(
                        text = " SYNC SUMMARY ",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Steps exécutés : $totalSteps", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Total pending   : $totalPending",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        "Total success   : $totalSuccess",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        "Total failed    : $totalFailed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )

                    Text("--------------------------------------------------", style = MaterialTheme.typography.bodyMedium)
                    Text("Détails :", style = MaterialTheme.typography.headlineSmall)

                    syncResults.forEach { result ->
                        val color = when {
                            result.failed > 0 -> MaterialTheme.colorScheme.error
                            result.success > 0 -> MaterialTheme.colorScheme.secondary
                            result.pending > 0 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                        Text(
                            text = "🔹 ${result.step}: pending=${result.pending}, success=${result.success}, failed=${result.failed}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = color
                        )
                    }

                    Text("==================================================", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Recherche") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("All", "Sites", "Villages", "Distributions").forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val filteredData: List<Any> = when (selectedType) {
                "Sites" -> sites.filter { it.nameWarehouse.contains(searchQuery, ignoreCase = true) }
                "Villages" -> villages.filter { (it.nom_village ?: "").contains(searchQuery, ignoreCase = true) }
                "Distributions" -> distributions.filter { it.nomComplet.contains(searchQuery, ignoreCase = true) }
                "All" -> (sites + villages + distributions).filter { item ->
                    when (item) {
                        is SiteEntity -> item.nameWarehouse.contains(searchQuery, ignoreCase = true)
                        is Village -> (item.nom_village ?: "").contains(searchQuery, ignoreCase = true)
                        is Distribution -> item.nomComplet.contains(searchQuery, ignoreCase = true)
                        else -> false
                    }
                }
                else -> emptyList()
            }

            LazyColumn {
                items(filteredData) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 🔹 Texte principal
                        Text(
                            text = when (item) {
                                is SiteEntity -> item.nameWarehouse
                                is Village -> item.nom_village ?: "N/A"
                                is Distribution -> item.nomComplet
                                else -> "Unknown"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // 🔹 Pill avec couleur et statut
                        when (item) {
                            is SiteEntity -> {
                                AssistChip(
                                    onClick = {},
                                    label = { Text("Site") },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        labelColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text("✅ Ok", color = MaterialTheme.colorScheme.secondary)
                            }
                            is Village -> {
                                AssistChip(
                                    onClick = {},
                                    label = { Text("Village") },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                        labelColor = MaterialTheme.colorScheme.tertiary
                                    )
                                )
                                Text("✅ Ok", color = MaterialTheme.colorScheme.secondary)
                            }
                            is Distribution -> {
                                AssistChip(
                                    onClick = {},
                                    label = { Text("Distribution") },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                                        labelColor = MaterialTheme.colorScheme.error
                                    )
                                )
                                val flag = when (item.syncStatus) {
                                    SyncStatus.SYNCED -> "✅"
                                    SyncStatus.PENDING -> "⏳"
                                    SyncStatus.FAILED -> "❌"
                                }
                                Text(flag)
                            }
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
