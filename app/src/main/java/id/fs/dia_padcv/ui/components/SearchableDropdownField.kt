package id.fs.dia_padcv.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 🔍 Composable générique pour recherche et sélection dynamique
 * avec animation fluide de la liste filtrée
 * Utilise uniquement les icônes natives préconfigurées
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableDropdownField(
    label: String,
    items: List<T>,
    selectedItem: T?,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit,
    onClearSelection: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    val filteredItems = remember(searchText, items) {
        if (searchText.isBlank()) emptyList()
        else items.filter { itemLabel(it).contains(searchText, ignoreCase = true) }
    }

    LaunchedEffect(searchText) {
        if (searchText.isBlank()) onClearSelection()
    }

    Column {
        OutlinedTextField(
            value = selectedItem?.let { itemLabel(it) } ?: searchText,
            onValueChange = {
                searchText = it
                if (selectedItem != null) onClearSelection()
            },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (searchText.isNotEmpty() || selectedItem != null) {
                    Text(
                        text = "✖",
                        modifier = Modifier
                            .clickable {
                                searchText = ""
                                onClearSelection()
                            }
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        )

        AnimatedVisibility(
            visible = filteredItems.isNotEmpty() && selectedItem == null,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 150.dp)
            ) {
                filteredItems.forEach { item ->
                    Text(
                        text = itemLabel(item),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .clickable {
                                onItemSelected(item)
                                searchText = itemLabel(item)
                            }
                    )
                }
            }
        }
    }
}
