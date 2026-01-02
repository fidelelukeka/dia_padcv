package id.fs.dia_padcv.ui.distribution.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.fs.dia_padcv.ui.AppViewModel
import id.fs.dia_padcv.ui.components.fields.RequiredNumberField
import id.fs.dia_padcv.ui.components.fields.RequiredSexeField
import id.fs.dia_padcv.ui.components.fields.RequiredTextField

@Composable
fun StepIdentite(viewModel: AppViewModel) {
    val distribution by viewModel.currentDistribution.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Identité", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

        RequiredTextField(
            value = distribution.nomComplet,
            onValueChange = { viewModel.updateDistribution(distribution.copy(nomComplet = it)) },
            label = "Nom Complet"
        )

        RequiredTextField(
            value = distribution.phone ?: "",
            onValueChange = { viewModel.updateDistribution(distribution.copy(phone = it)) },
            label = "Téléphone"
        )

        RequiredSexeField(
            sexe = distribution.sexe,
            onSexeSelected = { viewModel.updateDistribution(distribution.copy(sexe = it)) }
        )

        RequiredNumberField(
            value = distribution.tailleMenage,
            onValueChange = { viewModel.updateDistribution(distribution.copy(tailleMenage = it)) },
            label = "Nombre d'enfant"
        )
    }
}
