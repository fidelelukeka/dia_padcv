package id.fs.dia_padcv.ui.distribution.steps

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.fs.dia_padcv.ui.AppViewModel
import id.fs.dia_padcv.ui.components.OuiNonCheckBox


@Composable
fun StepDistributionSemence(viewModel: AppViewModel) {
    val distribution by viewModel.currentDistribution.collectAsState()

    // 🔹 Map générique des options : label -> Pair(hasValue, kgValue)
    val optionsMap = mapOf(
        "Riz" to Pair(viewModel.getOptionState("Riz"), distribution.kgRiz),
        "Maïs" to Pair(viewModel.getOptionState("Maïs"), distribution.kgMais),
        "Manioc" to Pair(viewModel.getOptionState("Manioc"), distribution.kgManioc),
        "Soja" to Pair(viewModel.getOptionState("Soja"), distribution.kgSoja)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Distribution des semences",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )

        // ✅ Superficie (champ numérique)
        OutlinedTextField(
            value = if (distribution.superficie.isEmpty() || distribution.superficie == "0") "" else distribution.superficie,
            onValueChange = { viewModel.updateDistribution(distribution.copy(superficie = it)) },
            label = { Text("Superficie cultivée (ha)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // 🔹 Boucle ultra-générique
        optionsMap.forEach { (label, values) ->
            val hasValue = values.first   // Boolean
            val kgValue = values.second   // Int

            OuiNonCheckBox(
                label = label,
                checked = hasValue,
                onCheckedChange = { newHas ->
                    Log.d("DistributionSemence", "onCheckedChange: label=$label, newHas=$newHas, currentKg=$kgValue")
                    viewModel.updateOption(label, newHas, kgValue)
                }
            )

            if (hasValue) {
                OutlinedTextField(
                    value = if (kgValue == 0) "" else kgValue.toString(),
                    onValueChange = { qty ->
                        val kg = qty.toIntOrNull() ?: 0
                        Log.d("DistributionSemence", "onValueChange: label=$label, qtyInput=$qty, parsedKg=$kg")
                        viewModel.updateOption(label, true, kg)
                    },
                    label = { Text("Kilogrammes de $label") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}