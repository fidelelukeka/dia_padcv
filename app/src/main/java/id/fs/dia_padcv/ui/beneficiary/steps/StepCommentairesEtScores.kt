package id.fs.dia_padcv.ui.beneficiary.steps

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import id.fs.dia_padcv.ui.AppViewModel
import id.fs.dia_padcv.ui.components.SearchableDropdownField
import id.fs.dia_padcv.ui.utils.QrCodeView
import id.fs.dia_padcv.ui.utils.generateQrCode
import id.fs.dia_padcv.ui.utils.saveBitmapToLocal
import id.fs.dia_padcv.ui.utils.saveGalleryImageToLocal
import id.fs.dia_padcv.ui.utils.saveQrToGallery
import id.fs.dia_padcv.ui.utils.useLocation
import kotlin.collections.get
import kotlin.text.ifEmpty

@Composable
fun StepCommentairesEtScores(viewModel: AppViewModel) {
    val beneficiary by viewModel.currentBeneficiary.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Commentaires et scores",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
//
//        // ✅ Commentaires et suggestions
//        OutlinedTextField(
//            value = beneficiary.commentairesSuggestions ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(commentairesSuggestions = it)) },
//            label = { Text("Commentaires et suggestions") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Numéro ticket
//        OutlinedTextField(
//            value = beneficiary.numTicket ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(numTicket = it)) },
//            label = { Text("Numéro du ticket") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Remarques sur le bénéficiaire
//        OutlinedTextField(
//            value = beneficiary.remarkBen ?: "",
//            onValueChange = { viewModel.updateBeneficiary(beneficiary.copy(remarkBen = it)) },
//            label = { Text("Remarques sur le bénéficiaire") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // ✅ Score final
//        OutlinedTextField(
//            value = if (beneficiary.scoreFinal == 0) "" else beneficiary.scoreFinal.toString(),
//            onValueChange = {
//                val score = it.toIntOrNull() ?: 0
//                viewModel.updateBeneficiary(beneficiary.copy(scoreFinal = score))
//            },
//            label = { Text("Score final") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
    }
}
