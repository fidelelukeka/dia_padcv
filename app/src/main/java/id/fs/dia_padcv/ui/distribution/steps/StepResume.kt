package id.fs.dia_padcv.ui.distribution.steps

import android.graphics.BitmapFactory
import android.util.Base64
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import id.fs.dia_padcv.ui.AppViewModel
import id.fs.dia_padcv.ui.components.StepTitle
import id.fs.dia_padcv.ui.components.SummaryItem
import id.fs.dia_padcv.ui.utils.QrCodeView
import id.fs.dia_padcv.ui.utils.generateQrCode
import id.fs.dia_padcv.ui.utils.saveQrToGallery

@Composable
fun StepResume(viewModel: AppViewModel, onSubmit: (() -> Unit)? = null) {
    val distribution by viewModel.currentDistribution.collectAsState()
    val context = LocalContext.current

    // QR Code basé sur Distribution
    val qrData = """
    {
      "idDistribution": ${distribution.idDistribution},
      "nomComplet": "${distribution.nomComplet}",
      "sexe": "${distribution.sexe}",
      "phone": "${distribution.phone ?: ""}",
      "siteId": ${distribution.siteId}
    }
    """.trimIndent()
    val qrBitmap = remember(qrData) { generateQrCode(qrData) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Résumé complet de la distribution",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider()

        // --- Identité ---
        StepTitle("Identité")
        SummaryItem("Nom complet", distribution.nomComplet)
        SummaryItem("Sexe", distribution.sexe)
        SummaryItem("Téléphone", distribution.phone ?: "")
        SummaryItem("Taille ménage", distribution.tailleMenage)

        // --- QR Code ---
        StepTitle("QR Code")
        QrCodeView(data = qrData)
        Button(onClick = {
            saveQrToGallery(context, qrBitmap, "distribution_qr_${distribution.idDistribution}.png")
        }) {
            Text("Sauvegarder le QR")
        }

        // --- Localisation GPS ---
        StepTitle("Localisation")
        SummaryItem("Latitude", distribution.latitude ?: "")
        SummaryItem("Longitude", distribution.longitude ?: "")
        SummaryItem("Altitude", distribution.altitude ?: "")
        SummaryItem("Précision", distribution.precision ?: "")

        // --- Photo enregistrée ---
        if (!distribution.image.isNullOrEmpty()) {
            StepTitle("Photo")
            val imageBitmap = remember(distribution.image) {
                try {
                    val bytes = Base64.decode(distribution.image, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                } catch (e: Exception) {
                    null
                }
            }
            imageBitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = "Photo de la distribution",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(top = 8.dp)
                )
            }
        }

        // --- Semences ---
        StepTitle("Semences")
        SummaryItem("Riz", "${distribution.hasRiz} - ${distribution.kgRiz} kg")
        SummaryItem("Maïs", "${distribution.hasMais} - ${distribution.kgMais} kg")
        SummaryItem("Manioc", "${distribution.hasManioc} - ${distribution.kgManioc} kg")
        SummaryItem("Soja", "${distribution.hasSoja} - ${distribution.kgSoja} kg")

        // --- Engrais ---
        StepTitle("Engrais")
        SummaryItem("DAP", "${distribution.hasDap} - ${distribution.kgDap} kg")
        SummaryItem("KCL", "${distribution.hasKcl} - ${distribution.kgKcl} kg")
        SummaryItem("Urée", "${distribution.hasUree} - ${distribution.kgUree} kg")
        SummaryItem("NPK", "${distribution.hasNpk} - ${distribution.kgNpk} kg")

        // --- Superficie ---
        StepTitle("Superficie")
        SummaryItem("Superficie cultivée (ha)", distribution.superficie)

        // --- Commentaires ---
        StepTitle("Commentaires")
        SummaryItem("Suggestions", distribution.suggestion)

        // Bouton soumission
        Button(
            onClick = {
                // 🔹 Sauvegarde en Room locale
                viewModel.saveDistribution()

                // 🔹 Sauvegarde via Exposed (si tu utilises encore cette logique)
//                viewModel.saveDistributionToDb(distribution)

                // 🔹 Envoi vers l’API
                viewModel.sendDistribution(context)

                // 🔹 Callback éventuel
                onSubmit?.invoke()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Soumettre la distribution")
        }

    }
}
