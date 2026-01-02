package id.fs.dia_padcv.ui.distribution.steps

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import java.io.File
import androidx.core.net.toUri

@Composable
fun StepResume(
    viewModel: AppViewModel,
    onSuccess: (() -> Unit)? = null // ✅ callback déclenché après succès
) {
    val distribution by viewModel.currentDistribution.collectAsState()
    val context = LocalContext.current

    val qrData = """
    {
      "id": ${distribution.idDistribution},
      "Nom Complet": "${distribution.nomComplet}",
      "Sexe": "${distribution.sexe}",
      "Phone": "${distribution.phone ?: ""}",
      "Riz : ${distribution.hasRiz} - ${distribution.kgRiz} kg",
      "Maïs : ${distribution.hasMais} - ${distribution.kgMais} kg",
      "Manioc : ${distribution.hasManioc} - ${distribution.kgManioc} kg",
      "Soja : ${distribution.hasSoja} - ${distribution.kgSoja} kg",
      "DAP : ${distribution.hasDap} - ${distribution.kgDap} kg",
      "KCL : ${distribution.hasKcl} - ${distribution.kgKcl} kg",
      "Urée : ${distribution.hasUree} - ${distribution.kgUree} kg",
      "NPK : ${distribution.hasNpk} - ${distribution.kgNpk} kg",
      "Superficie cultivée (ha) : ${distribution.superficie}",
      "Commentaire : ${distribution.suggestion}"
    }
    """.trimIndent()

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

        // --- QR Code ---
        StepTitle("QR Code")
        QrCodeView(data = qrData, context, distribution)

        // --- Identité ---
        StepTitle("Identité")
        SummaryItem("Nom complet", distribution.nomComplet)
        SummaryItem("Sexe", distribution.sexe)
        SummaryItem("Téléphone", distribution.phone ?: "")
        SummaryItem("Taille ménage", distribution.tailleMenage)

        // --- Localisation GPS ---
        StepTitle("Localisation")
        SummaryItem("Latitude", distribution.latitude ?: "")
        SummaryItem("Longitude", distribution.longitude ?: "")
        SummaryItem("Altitude", distribution.altitude ?: "")
        SummaryItem("Précision", distribution.precision ?: "")

        // --- Photo enregistrée ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ){
            if (!distribution.image.isNullOrEmpty()) {
                StepTitle("Photo")

                val imageBitmap = remember(distribution.image) {
                    try {
                        val file = File(distribution.image!!.toUri().path ?: "")
                        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }

                imageBitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "Photo de la distribution",
                        modifier = Modifier
                            .fillMaxWidth(1f)   // ✅ largeur = 90%
                            .fillMaxHeight(0.5f)  // ✅ hauteur = 50%
                            .clip(RoundedCornerShape(16.dp))
                            .padding(top = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                }
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

        // --- Bouton soumission ---
        Button(
            onClick = {
                viewModel.saveDistributionLocal { idLocal ->
                    viewModel.setUiMessage("✅ Distribution enregistrée dans la base de données locale")
                    viewModel.setUiMessage("⏳ Envoi en cours... à l'API")
                    if (idLocal != null) {
                        onSuccess?.invoke()
                    } else {
                        viewModel.setUiMessage("❌ Erreur lors de la sauvegarde locale")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Soumettre la distribution")
        }
    }
}