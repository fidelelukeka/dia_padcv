package id.fs.dia_padcv.ui.distribution.steps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.fs.dia_padcv.ui.AppViewModel

@Composable
fun StepCommentairesSuggestion(viewModel: AppViewModel) {
    val distribution by viewModel.currentDistribution.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Commentaires et suggestion",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )

        // ✅ Commentaires et suggestions
        OutlinedTextField(
            value = distribution.suggestion,
            onValueChange = { viewModel.updateDistribution(distribution.copy(suggestion = it)) },
            label = { Text("Commentaires et suggestions") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
