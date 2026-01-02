package id.fs.dia_padcv.ui.components

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
 * 🔍 Composable hybride obligatoire :
 * - champ de recherche filtrant
 * - dropdown select au clic
 * - fallback sur récents ou 10 items par défaut
 * - affiche une erreur si aucun élément n'est sélectionné
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableDropdownField(
    label: String,
    items: List<T>,
    selectedItem: T?,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit,
    onClearSelection: () -> Unit,
    recentItems: List<T> = emptyList()
) {
    var searchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val filteredItems = remember(searchText, items, recentItems) {
        when {
            searchText.isBlank() -> {
                recentItems.ifEmpty { items.take(10) }
            }
            else -> items.filter { itemLabel(it).contains(searchText, ignoreCase = true) }
        }
    }

    Column {
        OutlinedTextField(
            value = selectedItem?.let { itemLabel(it) } ?: searchText,
            onValueChange = {
                searchText = it
                if (selectedItem != null) {
                    onClearSelection()
                    isError = true
                }
                expanded = true // ouvre le menu dès qu’on tape
            },
            label = { Text(label) },
            isError = isError || selectedItem == null,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }, // ouvre aussi au clic
            trailingIcon = {
                if (searchText.isNotEmpty() || selectedItem != null) {
                    Text(
                        text = "✖",
                        modifier = Modifier
                            .clickable {
                                searchText = ""
                                onClearSelection()
                                expanded = false
                                isError = true
                            }
                            .padding(horizontal = 8.dp)
                    )
                }
            },
            readOnly = false // 🔑 permet la saisie
        )

        if (selectedItem == null) {
            Text(
                text = "Le champ $label doit être rempli",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        DropdownMenu(
            expanded = expanded && filteredItems.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
        ) {
            filteredItems.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemLabel(item)) },
                    onClick = {
                        onItemSelected(item)
                        searchText = itemLabel(item)
                        expanded = false
                        isError = false
                    }
                )
            }
        }
    }
}

