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
fun StepHabitation(viewModel: AppViewModel) {
    val beneficiary by viewModel.currentBeneficiary.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Habitation",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

//        // ✅ Habitation présente
//        OuiNonCheckBox(
//            label = "Le ménage dispose-t-il d'une habitation ?",
//            value = beneficiary.habitationMenage,
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(habitationMenage = it)) }
//        )
//
//        // ✅ Type de toit
//        OutlinedTextField(
//            value = beneficiary.toit ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(toit = it)) },
//            label = { Text("Type de toit") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Avez vous un mur
//        OuiNonCheckBox(
//            label = "Le ménage dispose-t-il d'un mur ?",
//            value = beneficiary.mur.toString(),
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(mur = it)) }
//        )
//
//        // ✅ Avez vous un sol
//        OuiNonCheckBox(
//            label = "Le ménage dispose-t-il d'un sol ?",
//            value = beneficiary.sol.toString(),
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(sol = it)) }
//        )
//
//        // ✅ Nombre de pièces
//        OutlinedTextField(
//            value = if (beneficiary.nbrePieces == 0) "" else beneficiary.nbrePieces.toString(),
//            onValueChange = {
//                val nbre = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(nbrePieces = nbre))
//            },
//            label = { Text("Nombre de pièces") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Nombre de personne par pièce
//        OutlinedTextField(
//            value = if (beneficiary.nbrePrsParPiece == 0) "" else beneficiary.nbrePrsParPiece.toString(),
//            onValueChange = {
//                val nbre = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(nbrePrsParPiece = nbre))
//            },
//            label = { Text("Nombre de personne par pièces") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Propriètaire de la parcelle
//        OuiNonCheckBox(
//            label = "Propriètaire d'une parcelle",
//            value = beneficiary.proprietaireParcelle,
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(proprietaireParcelle = it)) }
//        )
//
//        // ✅ Score habitation
//        OutlinedTextField(
//            value = if (beneficiary.scoreHabitation == 0) "" else beneficiary.scoreHabitation.toString(),
//            onValueChange = {
//                val nbre = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(scoreHabitation = nbre))
//            },
//            label = { Text("Score habitation") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
    }
}
