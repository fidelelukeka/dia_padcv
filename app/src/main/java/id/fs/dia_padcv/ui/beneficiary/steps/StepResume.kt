package id.fs.dia_padcv.ui.beneficiary.steps

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StepResume(viewModel: AppViewModel, onSubmit: (() -> Unit)? = null) {
    val beneficiary by viewModel.currentBeneficiary.collectAsState()
    val context = LocalContext.current

//    // QR Code
//    val qrData = """
//    {
//      "idBen": ${beneficiary.idBen},
//      "numCarteBen": "${beneficiary.numCarteBen}",
//      "nom": "${beneficiary.nom}",
//      "postnom": "${beneficiary.postnom}",
//      "prenom": "${beneficiary.prenom}",
//      "sexe": "${beneficiary.sexe}",
//      "phone": "${beneficiary.phone}"
//    }
//""".trimIndent()
//    val qrBitmap = remember(qrData) { generateQrCode(qrData) }
//
//    // Photos
//    val photos = beneficiary.photo ?: emptyList<Uri>()

    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Résumé complet du bénéficiaire",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider()

//        // --- Identité ---
//        StepTitle("Identité")
//        SummaryItem("Nom", beneficiary.nom)
//        SummaryItem("Postnom", beneficiary.postnom)
//        SummaryItem("Prénom", beneficiary.prenom)
//        SummaryItem("Téléphone", beneficiary.phone)
//        SummaryItem("Âge", beneficiary.age)
//        SummaryItem("Sexe", beneficiary.sexe)
//
//        // --- QR Code ---
//        StepTitle("QR Code")
//        QrCodeView(data = qrData)
//        Button(onClick = {
//            val success = saveQrToGallery(context, qrBitmap, "beneficiary_qr_${beneficiary.idBen}.png")
//            // feedback utilisateur
//        }) {
//            Text("Sauvegarder le QR")
//        }
//
//        // --- Localisation GPS ---
//        StepTitle("Localisation")
//        SummaryItem("Latitude", beneficiary.latitude)
//        SummaryItem("Longitude", beneficiary.longitude)
//        SummaryItem("Altitude", beneficiary.altitude)
//        SummaryItem("Précision", beneficiary.precision)
//
//        // --- Ménage ---
//        StepTitle("Ménage")
//        SummaryItem("Numéro carte bénéficiaire", beneficiary.numCarteBen)
//        SummaryItem("Chef de ménage", beneficiary.chefMenage)
//        SummaryItem("Enfant chef de ménage", beneficiary.enfantChefMenage)
//        SummaryItem("Répondant présent", beneficiary.repondantPresent)
//        SummaryItem("Lien répondant", beneficiary.lienRepondant)
//        SummaryItem("Noms répondant", beneficiary.repondantNoms)
//        SummaryItem("Statut activité", beneficiary.activityStatus)
//        SummaryItem("Taille ménage", beneficiary.tailleMenage)
//        SummaryItem("Enfants 6-17 école", beneficiary.enfants6_17Ecole)
//        SummaryItem("Handicapé", beneficiary.handicapeDansMenage)
//        SummaryItem("Personne âgée", beneficiary.personneAgeeDansMenage)
//        SummaryItem("Malade chronique", beneficiary.maladeChroniqueDansMenage)
//        SummaryItem("Enfant malnutri", beneficiary.enfantMalnutriDansMenage)
//        SummaryItem("Source revenus principale", beneficiary.sourceRevenusPrincipale)
//        SummaryItem("Autres sources revenus", beneficiary.autresSourceRevenuDansMenage)
//        SummaryItem("Score vulnérabilité", beneficiary.scoreVulnerabiliteMembreMenage)
//
//        // --- Habitation ---
//        StepTitle("Habitation")
//        SummaryItem("Habitation présente", beneficiary.habitationMenage)
//        SummaryItem("Toit", beneficiary.toit)
//        SummaryItem("Mur", beneficiary.mur)
//        SummaryItem("Sol", beneficiary.sol)
//        SummaryItem("Nombre pièces", beneficiary.nbrePieces)
//        SummaryItem("Personnes par pièce", beneficiary.nbrePrsParPiece)
//        SummaryItem("Propriétaire parcelle", beneficiary.proprietaireParcelle)
//        SummaryItem("Score habitation", beneficiary.scoreHabitation)
//
//        // --- WASH ---
//        StepTitle("WASH")
//        SummaryItem("Accès eau potable", beneficiary.accesEauPotable)
//        SummaryItem("Accès toilettes", beneficiary.accesToilettes)
//        SummaryItem("Distance toilettes", beneficiary.distanceAccesToilettes)
//        SummaryItem("Score WASH", beneficiary.scoreWASH)
//
//        // --- Agriculture ---
//        StepTitle("Agriculture")
//        SummaryItem("Campagne précédente", beneficiary.campagneAgricolePrecedente)
//        SummaryItem("Produits cultivés", beneficiary.produitsCultives)
//        SummaryItem("Sacs récoltés", beneficiary.nbreSacsRecoltes)
//        SummaryItem("Nombre champs", beneficiary.nbreChamps)
//        SummaryItem("Nombre champs agricoles", beneficiary.nbreChampsAgricoles)
//        SummaryItem("Autres infos agriculture", beneficiary.autresInfos)
//
//        // --- Biens matériels ---
//        StepTitle("Biens matériels")
//        SummaryItem("Maisons", beneficiary.nbreMaisons)
//        SummaryItem("Cases", beneficiary.nbreCases)
//        SummaryItem("Houes", beneficiary.nbreHoues)
//        SummaryItem("Charettes", beneficiary.nbreCharettes)
//        SummaryItem("Motos", beneficiary.nbreMotos)
//        SummaryItem("Vélos", beneficiary.nbreVelos)
//        SummaryItem("Bovins", beneficiary.nbreBovins)
//        SummaryItem("Ovins", beneficiary.nbreOvins)
//        SummaryItem("Caprins", beneficiary.nbreCaprins)
//        SummaryItem("Volailles", beneficiary.nbreVolails)
//        SummaryItem("Gros élevage", beneficiary.grosElevage)
//        SummaryItem("Score biens", beneficiary.scoreBiensMenage)
//
//        // --- Alimentation ---
//        StepTitle("Alimentation")
//        SummaryItem("Repas par jour", beneficiary.nbreRepasParJour)
//        SummaryItem("Consommation non préférés (7j)", beneficiary.nbreConsommationAlimentsNonPreferes7jrs)
//        SummaryItem("Aide pour manger (7j)", beneficiary.aidePourManger7jrs)
//        SummaryItem("Emprunts pour manger (7j)", beneficiary.empruntsPourManger7jrs)
//        SummaryItem("Diminuer quantité repas (7j)", beneficiary.diminuerQuantiteRepas7jrs)
//        SummaryItem("Limiter adultes (7j)", beneficiary.limiterConsommationAdultes7jrs)
//        SummaryItem("Diminuer nombre repas (7j)", beneficiary.diminuerNbreRepas7jrs)
//        SummaryItem("Utiliser enfants pour manger", beneficiary.nbreUtiliserEnfantPourManger)
//        SummaryItem("Autres infos alimentation", beneficiary.autresInfosAlimentation)
//        SummaryItem("Score alimentation", beneficiary.scoreAlimentation)
//
//        // --- Commentaires et scores ---
//        StepTitle("Commentaires et scores")
//        SummaryItem("Commentaires", beneficiary.commentairesSuggestions)
//        SummaryItem("Numéro ticket", beneficiary.numTicket)
//        SummaryItem("Remarques", beneficiary.remarkBen)
//        SummaryItem("Score matrimonial", beneficiary.scoreStatutMatrim)
//        SummaryItem("Score démographique", beneficiary.scoreDemographique)
//        SummaryItem("Score final", beneficiary.scoreFinal)
//
//
//        // --- Aperçu JSON avant soumission ---
//        StepTitle("Aperçu des données à envoyer")
//
//        val gson = remember { Gson() }
//        val jsonPreview = remember(beneficiary) {
//            gson.toJson(beneficiary)
//        }
//
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        ) {
//            Text(
//                text = "Payload (JSON)",
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.primary
//            )
//
//            Text(
//                text = jsonPreview,
//                style = MaterialTheme.typography.bodySmall,
//                modifier = Modifier.padding(top = 8.dp)
//            )
//        }
//
//        // Bouton soumission
//        Button(
//            onClick = {
//                // Appel direct à la fonction de création
//                viewModel.createBeneficiary(context, beneficiary)
//                // Si tu veux garder le callback externe
//                onSubmit?.invoke()
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Soumettre")
//        }
//
//        Button(
//            onClick = {
//                viewModel.testBeneficiaryAPI(context)
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Tester Payload Minimal")
//        }
   }
}
