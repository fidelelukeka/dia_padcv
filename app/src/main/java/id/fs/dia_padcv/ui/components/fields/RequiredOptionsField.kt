package id.fs.dia_padcv.ui.components.fields

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.fs.dia_padcv.ui.components.OuiNonCheckBox

@Composable
fun RequiredOptionsField(
    optionsMap: Map<String, Pair<Boolean, Int>>,
    onOptionChanged: (label: String, hasValue: Boolean, kgValue: Int) -> Unit
) {
    var isErrorGlobal by remember { mutableStateOf(false) }

    Column {
        optionsMap.forEach { (label, values) ->
            val hasValue = values.first
            val kgValue = values.second
            var isErrorQty by remember { mutableStateOf(false) }

            OuiNonCheckBox(
                label = label,
                checked = hasValue,
                onCheckedChange = { newHas ->
                    onOptionChanged(label, newHas, kgValue)
                    if (!newHas) isErrorQty = false
                }
            )

            if (hasValue) {
                OutlinedTextField(
                    value = if (kgValue == 0) "" else kgValue.toString(),
                    onValueChange = { qty ->
                        val kg = qty.toIntOrNull() ?: 0
                        onOptionChanged(label, true, kg)
                        isErrorQty = kg <= 0
                    },
                    label = { Text("Kilogrammes de $label") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = isErrorQty,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isErrorQty) {
                    Text(
                        text = "Le champ Kilogrammes de $label doit être rempli avec une valeur valide",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Vérification globale : au moins un champ doit être coché
        val anySelected = optionsMap.any { it.value.first }
        isErrorGlobal = !anySelected

        if (isErrorGlobal) {
            Text(
                text = "Au moins une option doit être sélectionnée",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
