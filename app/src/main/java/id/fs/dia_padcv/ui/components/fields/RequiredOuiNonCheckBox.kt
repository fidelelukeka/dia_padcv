package id.fs.dia_padcv.ui.components.fields

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RequiredOuiNonCheckBox(
    label: String,
    checked: Boolean?,
    onCheckedChange: (Boolean) -> Unit
) {
    var isError by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Checkbox(
            checked = checked == true,
            onCheckedChange = {
                onCheckedChange(it)
                isError = false
            }
        )
        Text(text = "$label : ${if (checked == true) "OUI" else if (checked == false) "NON" else ""}")
    }

    if (checked == null || isError) {
        Text(
            text = "Le champ $label doit être rempli",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
