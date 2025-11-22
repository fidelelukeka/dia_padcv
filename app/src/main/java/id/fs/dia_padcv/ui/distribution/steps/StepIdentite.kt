package id.fs.dia_padcv.ui.distribution.steps

import android.os.Build
import androidx.annotation.RequiresApi
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
    val distribution by viewModel.currentDistribution.collectAsState()

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

        // ✅ Nom
        OutlinedTextField(
            value = distribution.nomComplet,
            onValueChange = { viewModel.updateDistribution(distribution.copy(nomComplet = it)) },
            label = { Text("Nom Complet") },
            modifier = Modifier.fillMaxWidth()
        )

        // ✅ Téléphone
        OutlinedTextField(
            value = distribution.phone ?: "",
            onValueChange = { viewModel.updateDistribution(distribution.copy(phone = it)) },
            label = { Text("Téléphone") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        // ✅ Sexe
        Text("Sexe :", style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row {
                RadioButton(
                    selected = distribution.sexe == "Masculin",
                    onClick = {
                        viewModel.updateDistribution(
                            distribution.copy(sexe = "Masculin")
                        )
                    }
                )
                Text("Homme")
            }
            Row {
                RadioButton(
                    selected = distribution.sexe == "Féminin",
                    onClick = {
                        viewModel.updateDistribution(
                            distribution.copy(sexe = "Féminin")
                        )
                    }
                )
                Text("Femme")
            }
        }
        // ✅ Taille du ménage
        OutlinedTextField(
            value = if (distribution.tailleMenage == 0) "" else distribution.tailleMenage.toString(),
            onValueChange = {
                val taille = it.toIntOrNull() ?: 0
                viewModel.updateDistribution(distribution.copy(tailleMenage = taille))
            },
            label = { Text("Nombre d'enfant") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
