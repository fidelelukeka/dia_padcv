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
fun StepMenage(viewModel: AppViewModel) {
    val beneficiary by viewModel.currentBeneficiary.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Ménage",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
//
//        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
//
//        // ✅ Numéro carte bénéficiaire
//        OutlinedTextField(
//            value = beneficiary.numCarteBen ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(numCarteBen = it)) },
//            label = { Text("Numéro carte bénéficiaire") },
//            modifier = Modifier.fillMaxWidth()
//        )

//        // ✅ Chef de ménage
//        OuiNonCheckBox(
//            label = "Êtes-vous chef de ménage ?",
//            value = beneficiary.chefMenage,
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(chefMenage = it)) }
//        )
//
//        // ✅ Enfant chef de ménage
//        OuiNonCheckBox(
//            label = "Enfant chef de ménage ?",
//            value = beneficiary.enfantChefMenage,
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(enfantChefMenage = it)) }
//        )
//
//        // ✅ Répondant présent
//        OuiNonCheckBox(
//            label = "Répondant présent",
//            value = beneficiary.repondantPresent.toString(),
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(repondantPresent = it)) }
//        )
//
//        // ✅ Lien répondant
//        OutlinedTextField(
//            value = beneficiary.lienRepondant ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(lienRepondant = it)) },
//            label = { Text("Lien du répondant") },
//            modifier = Modifier.fillMaxWidth()
//        )

//        // ✅ Noms répondant
//        OutlinedTextField(
//            value = beneficiary.repondantNoms ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(repondantNoms = it)) },
//            label = { Text("Nom complet du répondant") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Statut activité
//        OutlinedTextField(
//            value = beneficiary.activityStatus ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(activityStatus = it)) },
//            label = { Text("Statut d'activité") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Taille du ménage
//        OutlinedTextField(
//            value = if (beneficiary.tailleMenage == 0) "" else beneficiary.tailleMenage.toString(),
//            onValueChange = {
//                val taille = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(tailleMenage = taille))
//            },
//            label = { Text("Taille du ménage") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Enfants 6-17 ans à l’école
//        OutlinedTextField(
//            value = if (beneficiary.enfants6_17Ecole == 0) "" else beneficiary.enfants6_17Ecole.toString(),
//            onValueChange = {
//                val taille = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(enfants6_17Ecole = taille))
//            },
//            label = { Text("Enfants 6-17 ans à l’école") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Handicapé dans le ménage
//        OuiNonCheckBox(
//            label = "Présence d'un handicapé dans le ménage ?",
//            value = beneficiary.handicapeDansMenage,
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(handicapeDansMenage = it)) }
//        )
//
//        // ✅ Personne âgée
//        OuiNonCheckBox(
//            label = "Présence d'une personne âgée dans le ménage ?",
//            value = beneficiary.personneAgeeDansMenage,
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(personneAgeeDansMenage = it)) }
//        )
//
//        // ✅ Malade chronique
//        OuiNonCheckBox(
//            label = "Présence d'un malade chronique dans le ménage ?",
//            value = beneficiary.maladeChroniqueDansMenage,
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(maladeChroniqueDansMenage = it)) }
//        )
//
//        // ✅ Enfant malnutri
//        OuiNonCheckBox(
//            label = "Présence d'un enfant malnutri dans le ménage ?",
//            value = beneficiary.enfantMalnutriDansMenage,
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(enfantMalnutriDansMenage = it)) }
//        )
//
//        // ✅ Source principale de revenus
//        OutlinedTextField(
//            value = beneficiary.sourceRevenusPrincipale ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(sourceRevenusPrincipale = it)) },
//            label = { Text("Source principale de revenus") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Autres sources de revenus
//        OutlinedTextField(
//            value = beneficiary.autresSourceRevenuDansMenage ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(autresSourceRevenuDansMenage = it)) },
//            label = { Text("Autres sources de revenus") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Score vulnérabilité
//        OutlinedTextField(
//            value = if (beneficiary.scoreVulnerabiliteMembreMenage == 0) "" else beneficiary.scoreVulnerabiliteMembreMenage.toString(),
//            onValueChange = {
//                val score = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(scoreVulnerabiliteMembreMenage = score))
//            },
//            label = { Text("Score vulnérabilité ménage") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Score statut matrimonial
//        OutlinedTextField(
//            value = if (beneficiary.scoreStatutMatrim == 0) "" else beneficiary.scoreStatutMatrim.toString(),
//            onValueChange = {
//                val score = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(scoreStatutMatrim = score))
//            },
//            label = { Text("Score statut matrimonial") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Score démographique
//        OutlinedTextField(
//            value = if (beneficiary.scoreDemographique == 0) "" else beneficiary.scoreDemographique.toString(),
//            onValueChange = {
//                val score = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(scoreDemographique = score))
//            },
//            label = { Text("Score démographique") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )

    }
}
