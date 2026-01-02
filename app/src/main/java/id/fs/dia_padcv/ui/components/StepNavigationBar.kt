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
    onNext: () -> Unit,
    validateStep: (Int) -> Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(WindowInsets.navigationBars.asPaddingValues()),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = onPrevious, enabled = currentStep > 1) {
            Text("Retour")
        }

        Text("Étape $currentStep / $totalSteps")

        Button(onClick = {
            val ok = validateStep(currentStep)
            if (ok) onNext()
        }, enabled = currentStep < totalSteps) {
            Text(if (currentStep == totalSteps) "Fin, revoyer les réponses" else "Suivant")
        }
    }
}