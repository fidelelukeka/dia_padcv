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
fun StepAlimentation(viewModel: AppViewModel) {
    val beneficiary by viewModel.currentBeneficiary.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Alimentation", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

//        // Nombre de repas par jour (varchar(50)) → String
//        OutlinedTextField(
//            value = beneficiary.nbreRepasParJour?.toString() ?: "",
//            onValueChange = { input ->
//                val digitsOnly = input.filter { it.isDigit() }
//                val intValue = digitsOnly.toIntOrNull()
//                viewModel.updateBeneficiary(
//                    beneficiary.copy(nbreRepasParJour = intValue ?: 0 )
//                )
//            },
//            label = { Text("Nombre de repas par jour") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // Consommation d'aliments non préférés (7 jours) (int)
//        OutlinedTextField(
//            value = if (beneficiary.nbreConsommationAlimentsNonPreferes7jrs == 0) "" else beneficiary.nbreConsommationAlimentsNonPreferes7jrs.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(nbreConsommationAlimentsNonPreferes7jrs = v))
//            },
//            label = { Text("Consommation d'aliments non préférés (7 jours)") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // Aide pour manger (Oui/Non)
//        OuiNonCheckBox(
//            label = "Avez-vous reçu une aide pour manger (7 jours) ?",
//            value = beneficiary.aidePourManger7jrs, // "OUI"/"NON"
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(aidePourManger7jrs = it)) }
//        )
//
//        // Emprunts pour manger (7 jours) (int)
//        OutlinedTextField(
//            value = if (beneficiary.empruntsPourManger7jrs == 0) "" else beneficiary.empruntsPourManger7jrs.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(empruntsPourManger7jrs = v))
//            },
//            label = { Text("Emprunts pour manger (7 jours)") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // Diminuer la quantité des repas (7 jours) (int)
//        OutlinedTextField(
//            value = if (beneficiary.diminuerQuantiteRepas7jrs == 0) "" else beneficiary.diminuerQuantiteRepas7jrs.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(diminuerQuantiteRepas7jrs = v))
//            },
//            label = { Text("Diminuer la quantité des repas (7 jours)") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // Limiter la consommation des adultes (7 jours) (int)
//        OutlinedTextField(
//            value = if (beneficiary.limiterConsommationAdultes7jrs == 0) "" else beneficiary.limiterConsommationAdultes7jrs.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(limiterConsommationAdultes7jrs = v))
//            },
//            label = { Text("Limiter la consommation des adultes (7 jours)") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // Diminuer le nombre de repas (7 jours) (int)
//        OutlinedTextField(
//            value = if (beneficiary.diminuerNbreRepas7jrs == 0) "" else beneficiary.diminuerNbreRepas7jrs.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(diminuerNbreRepas7jrs = v))
//            },
//            label = { Text("Diminuer le nombre de repas (7 jours)") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // Utiliser les enfants pour manger (int)
//        OutlinedTextField(
//            value = if (beneficiary.nbreUtiliserEnfantPourManger == 0) "" else beneficiary.nbreUtiliserEnfantPourManger.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(nbreUtiliserEnfantPourManger = v))
//            },
//            label = { Text("Utiliser les enfants pour manger (nombre)") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // Autres informations alimentation (varchar)
//        OutlinedTextField(
//            value = beneficiary.autresInfosAlimentation ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(autresInfosAlimentation = it)) },
//            label = { Text("Autres informations alimentation") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // Score alimentation (int)
//        OutlinedTextField(
//            value = if (beneficiary.scoreAlimentation == 0) "" else beneficiary.scoreAlimentation.toString(),
//            onValueChange = {
//                val v = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(scoreAlimentation = v))
//            },
//            label = { Text("Score alimentation") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
    }
}