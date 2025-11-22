package id.fs.dia_padcv.ui.beneficiary.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.fs.dia_padcv.data.local.entities.Beneficiary
import id.fs.dia_padcv.ui.AppViewModel

@Composable
fun StepIdentite(viewModel: AppViewModel) {
    val beneficiary by viewModel.currentBeneficiary.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 🔹 Titre
        Text(
            text = "Identité",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )

//        // ✅ Nom
//        OutlinedTextField(
//            value = beneficiary.nom ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(nom = it)) },
//            label = { Text("Nom") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Postnom
//        OutlinedTextField(
//            value = beneficiary.postnom ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(postnom = it)) },
//            label = { Text("Postnom") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Prénom
//        OutlinedTextField(
//            value = beneficiary.prenom ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(prenom = it)) },
//            label = { Text("Prénom") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Téléphone
//        OutlinedTextField(
//            value = beneficiary.phone ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(phone = it)) },
//            label = { Text("Téléphone") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Âge
//        OutlinedTextField(
//            value = if (beneficiary.age == 0) "" else beneficiary.age.toString(),
//            onValueChange = {
//                val age = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(age = age))
//            },
//            label = { Text("Âge") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        if (beneficiary.age < 18) {
//            Text(
//                text = "Le chef de ménage a moins de 18 ans",
//                color = MaterialTheme.colorScheme.error,
//                style = MaterialTheme.typography.bodyMedium
//            )
//        }
//
//        // ✅ Sexe
//        Text("Sexe :", style = MaterialTheme.typography.bodyMedium)
//        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
//            Row {
//                RadioButton(
//                    selected = beneficiary.sexe == "Masculin",
//                    onClick = {
//                        viewModel.updateBeneficiary(
//                            beneficiary.copy(sexe = "Masculin")
//                        )
//                    }
//                )
//                Text("Homme")
//            }
//            Row {
//                RadioButton(
//                    selected = beneficiary.sexe == "Féminin",
//                    onClick = {
//                        viewModel.updateBeneficiary(
//                            beneficiary.copy(sexe = "Féminin")
//                        )
//                    }
//                )
//                Text("Femme")
//            }
//        }
    }
}
