package id.fs.dia_padcv.ui.utils

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import id.fs.dia_padcv.data.local.entities.Distribution

@Composable
fun QrCodeView(
    data: String,
    context : Context,
    distribution : Distribution
) {
    val qrBitmap = remember { generateQrCode(data) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center // ✅ centre le contenu dans la Box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "QR Code",
                contentScale = ContentScale.Fit
            )
            Button(onClick = {
                saveQrToGallery(context, qrBitmap, "distribution_qr_${distribution.nomComplet}_${distribution.idDistribution}.png")
            }) {
                Text("Sauvegarder le QR")
            }
        }
    }
}
