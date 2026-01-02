package id.fs.dia_padcv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.fs.dia_padcv.ui.AppViewModel.UserInfo
@Composable
fun AvatarViewScreen(
    userInfo: State<UserInfo>,
    boxSize: Dp = 200.dp
) {
    val username = userInfo.value.username.orEmpty()

    // Générer les initiales de manière sûre
    val initials = username
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .take(2)

    // Calculer la taille du texte : 65% de la taille du Box
    val textSize = (boxSize.value * 0.65).sp

    Box(
        modifier = Modifier
            .size(boxSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        val displayText = initials.ifBlank { "-" }

        Text(
            text = displayText,
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
    }
}