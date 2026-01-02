package id.fs.dia_padcv.ui.components.fields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

@Composable
fun RequiredSexeField(
    sexe: String?,
    onSexeSelected: (String) -> Unit
) {
    var isError by remember { mutableStateOf(false) }

    Column {
        Text("Sexe :", style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row {
                RadioButton(
                    selected = sexe == "Masculin",
                    onClick = { onSexeSelected("Masculin"); isError = false }
                )
                Text("Homme")
            }
            Row {
                RadioButton(
                    selected = sexe == "Féminin",
                    onClick = { onSexeSelected("Féminin"); isError = false }
                )
                Text("Femme")
            }
        }
        if (sexe.isNullOrBlank() || isError) {
            Text(
                text = "Le champ Sexe doit être rempli",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
