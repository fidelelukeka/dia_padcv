package id.fs.dia_padcv.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OuiNonCheckBox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { isChecked ->
                onCheckedChange(isChecked)
            }
        )
        Text(text = "$label : ${if (checked) "OUI" else "NON"}")
    }
}