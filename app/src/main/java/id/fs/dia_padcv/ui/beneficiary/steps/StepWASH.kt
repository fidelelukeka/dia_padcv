package id.fs.dia_padcv.ui.beneficiary.steps

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
fun StepWASH(viewModel: AppViewModel) {
    val beneficiary by viewModel.currentBeneficiary.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "WASH",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

//        // ✅ Accès à l’eau potable
//        OuiNonCheckBox(
//            label = "Accès à l’eau potable",
//            value = beneficiary.accesEauPotable,
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(accesEauPotable = it)) }
//        )
//
//        // ✅ Accès aux toilettes
//        OuiNonCheckBox(
//            label = "Accès aux toilettes",
//            value = beneficiary.accesToilettes,
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(accesToilettes = it)) }
//        )
//
//        // ✅ Distance d'accès aux toilettes
//        OutlinedTextField(
//            value = if (beneficiary.distanceAccesToilettes == 0) "" else beneficiary.distanceAccesToilettes.toString(),
//            onValueChange = {
//                val nbre = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(distanceAccesToilettes = nbre))
//            },
//            label = { Text("Distance d'accès aux toilettes") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Score WASH
//        OutlinedTextField(
//            value = if (beneficiary.scoreWASH == 0) "" else beneficiary.scoreWASH.toString(),
//            onValueChange = {
//                val nbre = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(scoreWASH = nbre))
//            },
//            label = { Text("Score WASH") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
    }
}
