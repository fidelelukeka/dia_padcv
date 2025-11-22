package id.fs.dia_padcv.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun QrCodeSaver(data: String) {
    val context = LocalContext.current
    val qrBitmap = remember { generateQrCode(data) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        QrCodeView(data) // affiche le QR

        Button(onClick = {
            val success = saveQrToGallery(context, qrBitmap, "menage_qr.png")
            if (success) {
                // Ici tu peux afficher un Toast ou Snackbar
                println("QR sauvegardé dans la galerie ✅")
            }
        }) {
            Text("💾 Sauvegarder le QR dans la galerie")
        }
    }
}
