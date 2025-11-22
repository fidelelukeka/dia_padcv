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
fun StepBiensMateriels(viewModel: AppViewModel) {
    val beneficiary by viewModel.currentBeneficiary.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Biens matériels",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
//
//        // ✅ Maisons
//        OutlinedTextField(
//            value = if (beneficiary.nbreMaisons == 0) "" else beneficiary.nbreMaisons.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(nbreMaisons = v))
//            },
//            label = { Text("Nombre de maisons") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Cases
//        OutlinedTextField(
//            value = if (beneficiary.nbreCases == 0) "" else beneficiary.nbreCases.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(nbreCases = v))
//            },
//            label = { Text("Nombre de cases") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Houes
//        OutlinedTextField(
//            value = if (beneficiary.nbreHoues == 0) "" else beneficiary.nbreHoues.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(nbreHoues = v))
//            },
//            label = { Text("Nombre de houes") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Charettes
//        OutlinedTextField(
//            value = if (beneficiary.nbreCharettes == 0) "" else beneficiary.nbreCharettes.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(nbreCharettes = v))
//            },
//            label = { Text("Nombre de charettes") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Motos
//        OutlinedTextField(
//            value = if (beneficiary.nbreMotos == 0) "" else beneficiary.nbreMotos.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(nbreMotos = v))
//            },
//            label = { Text("Nombre de motos") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Vélos
//        OutlinedTextField(
//            value = if (beneficiary.nbreVelos == 0) "" else beneficiary.nbreVelos.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(nbreVelos = v))
//            },
//            label = { Text("Nombre de vélos") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Bovins
//        OutlinedTextField(
//            value = if (beneficiary.nbreBovins == 0) "" else beneficiary.nbreBovins.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(nbreBovins = v))
//            },
//            label = { Text("Nombre de bovins") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Ovins
//        OutlinedTextField(
//            value = if (beneficiary.nbreOvins == 0) "" else beneficiary.nbreOvins.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(nbreOvins = v))
//            },
//            label = { Text("Nombre d'ovins") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Caprins
//        OutlinedTextField(
//            value = if (beneficiary.nbreCaprins == 0) "" else beneficiary.nbreCaprins.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(nbreCaprins = v))
//            },
//            label = { Text("Nombre de caprins") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Volails
//        OutlinedTextField(
//            value = if (beneficiary.nbreVolails == 0) "" else beneficiary.nbreVolails.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(nbreVolails = v))
//            },
//            label = { Text("Nombre de volailles") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Gros élevage (Oui/Non)
//        OuiNonCheckBox(
//            label = "Pratiquez-vous le gros élevage ?",
//            value = beneficiary.grosElevage,
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(grosElevage = it)) }
//        )
//
//        // ✅ Score biens ménage
//        OutlinedTextField(
//            value = if (beneficiary.scoreBiensMenage == 0) "" else beneficiary.scoreBiensMenage.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(scoreBiensMenage = v))
//            },
//            label = { Text("Score biens du ménage") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
    }
}
