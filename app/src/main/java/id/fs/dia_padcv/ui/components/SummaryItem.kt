package id.fs.dia_padcv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector

// --- Helper pour formatter les valeurs ---
fun formatSummaryValue(value: Any?): String = when (value) {
    null -> "Non renseigné"
    is String -> if (value.isBlank()) "Non renseigné" else value
    is Int -> if (value == 0) "Non renseigné" else value.toString()
    is Boolean -> if (value) "Oui" else "Non"
    else -> value.toString()
}

// --- Extension pour styling uniforme ---
@Composable
fun Modifier.summaryBackground(): Modifier = this.background(
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
    shape = RoundedCornerShape(8.dp)
)

// --- Composable amélioré ---@Composable
@Composable
fun SummaryItem(
    label: String,
    value: Any?,
    icon: ImageVector? = null
) {
    val valueText = formatSummaryValue(value)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .summaryBackground()
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = valueText,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            softWrap = true // ✅ permet d’afficher correctement les longues valeurs
        )
    }
}
