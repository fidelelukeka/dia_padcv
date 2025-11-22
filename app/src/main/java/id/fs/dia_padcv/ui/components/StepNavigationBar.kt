package id.fs.dia_padcv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StepNavigationBar(
    currentStep: Int,
    totalSteps: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(WindowInsets.navigationBars.asPaddingValues()), // ⚡ ajoute un espace au-dessus des boutons système
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = onPrevious, enabled = currentStep > 1) {
            Text("Retour")
        }

        Text("Étape $currentStep / $totalSteps")

        Button(onClick = onNext) {
            Text(if (currentStep == totalSteps) "Terminer" else "Suivant")
        }
    }
}
