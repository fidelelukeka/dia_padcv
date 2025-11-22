package id.fs.dia_padcv.ui.beneficiary.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
fun StepAgriculture(viewModel: AppViewModel) {

    val beneficiary by viewModel.currentBeneficiary.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        Text(
            text = "Agriculture",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

//        // ✅ 1. Campagne agricole précédente (Oui/Non)
//        OuiNonCheckBox(
//            label = "Avez-vous mené une campagne agricole précédente ?",
//            value = beneficiary.campagneAgricolePrecedente,
//            onValueChange = {
//                viewModel.updateBeneficiary(
//                    beneficiary.copy(campagneAgricolePrecedente = it)
//                )
//            }
//        )
//        // ✅ 2. Produits cultivés
//        OutlinedTextField(
//            value = beneficiary.produitsCultives,
//            onValueChange = {
//                viewModel.updateBeneficiary(
//                    beneficiary.copy(produitsCultives = it)
//                )
//            },
//            label = { Text("Produits cultivés") },
//            modifier = Modifier.fillMaxWidth()
//        )
//        // ✅ 3. Nombre de sacs récoltés
//        OutlinedTextField(
//            value = if (beneficiary.nbreSacsRecoltes == 0) "" else beneficiary.nbreSacsRecoltes.toString(),
//            onValueChange = {
//                val number = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(
//                    beneficiary.copy(nbreSacsRecoltes = number)
//                )
//            },
//            label = { Text("Nombre de sacs récoltés") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//        // ✅ 4. Autres informations
//        OutlinedTextField(
//            value = beneficiary.autresInfos,
//            onValueChange = {
//                viewModel.updateBeneficiary(
//                    beneficiary.copy(autresInfos = it)
//                )
//            },
//            label = { Text("Autres informations") },
//            modifier = Modifier.fillMaxWidth()
//        )
//        // ✅ 5. Nombre de champs
//        OutlinedTextField(
//            value = if (beneficiary.nbreChamps == 0) "" else beneficiary.nbreChamps.toString(),
//            onValueChange = {
//                val number = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(
//                    beneficiary.copy(nbreChamps = number)
//                )
//            },
//            label = { Text("Nombre de champs") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//        // ✅ 6. Nombre de champs agricoles
//        OutlinedTextField(
//            value = if (beneficiary.nbreChampsAgricoles == 0) "" else beneficiary.nbreChampsAgricoles.toString(),
//            onValueChange = {
//                val number = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(
//                    beneficiary.copy(nbreChampsAgricoles = number)
//                )
//            },
//            label = { Text("Nombre de champs agricoles") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
    }
}

//@Preview
//@Composable
//fun StepAgriculturePreview() {
//    StepAgriculture()
//}
