package id.fs.dia_padcv.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember

@Composable
fun QrCodeView(data: String) {
    val qrBitmap = remember { generateQrCode(data) }
    Image(
        bitmap = qrBitmap.asImageBitmap(),
        contentDescription = "QR Code",
        modifier = Modifier.size(200.dp)
    )
}
