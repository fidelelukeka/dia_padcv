package id.fs.dia_padcv.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.fs.dia_padcv.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import androidx.core.net.toUri

// Entités locales Room
import id.fs.dia_padcv.data.local.entities.Beneficiary
import id.fs.dia_padcv.data.local.entities.Colis
import id.fs.dia_padcv.data.local.entities.Distribution
import id.fs.dia_padcv.data.local.entities.Entrepot
import id.fs.dia_padcv.data.local.entities.SiteEntity

// Classes API
import id.fs.dia_padcv.data.remote.api.Village as ApiVillage
import id.fs.dia_padcv.data.remote.api.BeneficiaryRequest
import id.fs.dia_padcv.data.remote.api.ColisRequest
import id.fs.dia_padcv.data.remote.api.DistributionRequest
import id.fs.dia_padcv.data.remote.api.WarehouseRequest
import id.fs.dia_padcv.data.remote.api.RetrofitClient
import id.fs.dia_padcv.data.remote.api.Site
import id.fs.dia_padcv.data.remote.api.Warehouse
import id.fs.dia_padcv.data.remote.db.insertDistribution
import java.time.LocalDate


class AppViewModel(private val repository: AppRepository) : ViewModel() {

    // ------------------ LOGIN ------------------
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    private val _loginError = mutableStateOf<String?>(null)
    val loginError: State<String?> = _loginError

    fun login(context: Context, username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = null
            try {
                val success = repository.login(username, password)
                if (success) {
                    _isLoggedIn.value = true
                    Toast.makeText(context, "✅ Connexion réussie", Toast.LENGTH_SHORT).show()
                    Log.i("AppViewModel", "✅ Connexion réussie pour $username")
                } else {
                    _loginError.value = "Nom d’utilisateur ou mot de passe incorrect."
                    Toast.makeText(context, "⚠️ Nom d’utilisateur ou mot de passe incorrect", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                _loginError.value = e.message ?: "Erreur inconnue."
                Toast.makeText(context, "❌ Erreur login: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("AppViewModel", "❌ Exception login", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ------------------ DISTRIBUTIONS ------------------
//    val distributions: Flow<List<Distribution>> = repository.distributions
//    fun addDistribution(d: Distribution) = viewModelScope.launch { repository.insertDistribution(d) }
//    fun removeDistribution(d: Distribution) = viewModelScope.launch { repository.deleteDistribution(d) }

    // ------------------ VILLAGES ------------------
    private val _villages = MutableStateFlow<List<ApiVillage>>(emptyList())
    val villages: StateFlow<List<ApiVillage>> = _villages

    private val _selectedVillage = mutableStateOf<ApiVillage?>(null)
    val selectedVillage: State<ApiVillage?> = _selectedVillage

    fun selectVillage(village: ApiVillage) {
        _selectedVillage.value = village
        Log.i("AppViewModel", "🏡 Village sélectionné : ${village.name_village}")
    }

    fun clearSelectedVillage() {
        _selectedVillage.value = null
    }

    fun fetchVillages(context: Context) {
        viewModelScope.launch {
            try {
                val list = repository.fetchVillages()
                _villages.value = list
                Toast.makeText(context, "✅ ${list.size} villages récupérés", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                _villages.value = emptyList()
                Toast.makeText(context, "❌ Erreur récupération villages", Toast.LENGTH_SHORT).show()
                Log.e("AppViewModel", "❌ Exception fetchVillages", e)
            }
        }
    }

    // ------------------ SEARCH ENTREPOT ------------------
    private val _entrepots = MutableStateFlow<List<Warehouse>>(emptyList())
    val entrepots: StateFlow<List<Warehouse>> = _entrepots

    private val _selectedEntrepot = MutableStateFlow<Warehouse?>(null)
    val selectedEntrepot: StateFlow<Warehouse?> = _selectedEntrepot

    fun selectEntrepot(entrepot: Warehouse) {
        _selectedEntrepot.value = entrepot
        Log.i("AppViewModel", "🏡 Entrepôt sélectionné : ${entrepot.name_warehouse}")
    }

    fun clearSelectedEntrepot() {
        _selectedEntrepot.value = null
    }

    fun fetchEntrepots() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getWarehouse()
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data
                    val json = Gson().toJson(data)
                    val type = object : TypeToken<List<Warehouse>>() {}.type
                    val parsed = Gson().fromJson<List<Warehouse>>(json, type)
                    _entrepots.value = parsed ?: emptyList()
                    Log.i("AppViewModel", "✅ ${parsed.size} entrepôts récupérés depuis l’API")
                } else {
                    _entrepots.value = emptyList()
                    Log.w("AppViewModel", "⚠️ Erreur API entrepôts: ${response.code()}")
                }
            } catch (e: Exception) {
                _entrepots.value = emptyList()
                Log.e("AppViewModel", "❌ Exception fetchEntrepots", e)
            }
        }
    }


    fun addEntrepot(context: Context, entrepot: Entrepot) = viewModelScope.launch {
        try {
            repository.insertEntrepot(entrepot)
            Toast.makeText(context, "✅ Entrepôt ajouté localement", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "❌ Erreur ajout entrepôt", Toast.LENGTH_SHORT).show()
            Log.e("AppViewModel", "❌ Erreur insertEntrepot", e)
        }
    }

    fun createWarehouseFromEntrepot(context: Context, entrepot: Entrepot) {
        val villageId = _selectedVillage.value?.id_village ?: run {
            Toast.makeText(context, "⚠️ Aucun village sélectionné", Toast.LENGTH_SHORT).show()
            return
        }

        val lat = entrepot.latitude ?: 0.0
        val lng = entrepot.longitude ?: 0.0
        val alt = entrepot.altitude ?: 0.0
        val precision = entrepot.precision ?: 0.0

        viewModelScope.launch {
            try {
                val photoBase64 = entrepot.photoPath
                    ?.split(",")
                    ?.firstOrNull()
                    ?.let { path -> loadBitmapFromUri(context, path)?.let { compressBitmap(it) } }

                val request = WarehouseRequest(
                    id_village_f = villageId,
                    latitude_wrhs = lat,
                    longitude_wrhs = lng,
                    altitude_wrhs = alt,
                    precision_wrhs = precision,
                    name_warehouse = entrepot.nameWarehouse,
                    name_responsable = entrepot.nameResponsable,
                    photo = photoBase64,
                    type = entrepot.type
                )

                val success = repository.createWarehouse(request)
                if (success) {
                    Toast.makeText(context, "✅ Entrepôt créé sur API", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "⚠️ Erreur création entrepôt API", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "❌ Exception création entrepôt", Toast.LENGTH_SHORT).show()
                Log.e("AppViewModel", "❌ Exception createWarehouse", e)
            }
        }
    }

    // ------------------ COLIS ------------------
    val packages: Flow<List<Colis>> = repository.colis

    fun addPackage(context: Context, colis: Colis) = viewModelScope.launch {
        try {
            val warehouseId = selectedEntrepot.value?.warehouse_id?.toIntOrNull()
            if (warehouseId == null) {
                Toast.makeText(context, "⚠️ Entrepôt invalide", Toast.LENGTH_SHORT).show()
                return@launch
            }

            if (colis.quality.isNullOrEmpty() || colis.status.isNullOrEmpty()) {
                Toast.makeText(context, "⚠️ Veuillez remplir qualité et statut", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val qrBase64 = colis.qrCode?.let { path ->
                val file = File(path.toUri().path ?: "")
                if (file.exists()) Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
                else ""
            } ?: ""

            val request = ColisRequest(
                qrCode = qrBase64,
                produitType = colis.produitType ?: "",
                produitContenu = colis.produitContenu ?: "",
                quantity = colis.quantity ?: 0,
                unit = colis.unit ?: "",
                quality = colis.quality.lowercase().replaceFirstChar { it.lowercase() },
                warehouseIdF = warehouseId,
                status = colis.status.lowercase()
            )

            val success = repository.createColis(request)
            if (success) {
                Toast.makeText(context, "✅ Colis créé sur API", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "⚠️ Erreur création colis API", Toast.LENGTH_SHORT).show()
            }

            repository.insertColis(colis.copy(warehouseIdF = warehouseId))
            Toast.makeText(context, "✅ Colis enregistré localement", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(context, "❌ Erreur ajout colis", Toast.LENGTH_SHORT).show()
            Log.e("AppViewModel", "❌ Exception addPackage", e)
        }
    }

    fun removePackage(c: Colis) = viewModelScope.launch {
        try {
            repository.deleteColis(c)
            Log.i("AppViewModel", "✅ Colis supprimé localement")
        } catch (e: Exception) {
            Log.e("AppViewModel", "❌ Erreur deleteColis", e)
        }
    }

    // ------------------ UTILITAIRES ------------------
    private fun compressBitmap(bitmap: Bitmap, quality: Int = 50): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    fun loadBitmapFromUri(context: Context, uriString: String): Bitmap? {
        return try {
            val uri = uriString.toUri()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            Log.e("AppViewModel", "❌ Erreur chargement Bitmap: $uriString", e)
            null
        }
    }

    // ------------------ BENEFICIARIES ------------------
    private val _beneficiaries = MutableStateFlow<List<Beneficiary>>(emptyList())
    val beneficiaries: StateFlow<List<Beneficiary>> = _beneficiaries

    private val _selectedBeneficiary = mutableStateOf<Beneficiary?>(null)
    val selectedBeneficiary: State<Beneficiary?> = _selectedBeneficiary

    fun selectBeneficiary(beneficiary: Beneficiary) {
        _selectedBeneficiary.value = beneficiary
        Log.i("BeneficiaryViewModel", "👤 Bénéficiaire sélectionné : ${beneficiary.nom} ${beneficiary.prenom}")
    }

    fun clearSelectedBeneficiary() {
        _selectedBeneficiary.value = null
    }

    fun fetchBeneficiaries() {
        viewModelScope.launch {
            try {
                repository.beneficiaries.collect { list ->
                    _beneficiaries.value = list
                    Log.i("BeneficiaryViewModel", "✅ ${list.size} bénéficiaires récupérés localement")
                }
            } catch (e: Exception) {
                _beneficiaries.value = emptyList()
                Log.e("BeneficiaryViewModel", "❌ Exception fetchBeneficiaries", e)
            }
        }
    }

    fun addBeneficiary(context: Context, beneficiary: Beneficiary) = viewModelScope.launch {
        try {
            if (beneficiary.nom.isNullOrBlank() || beneficiary.prenom.isNullOrBlank()) {
                Toast.makeText(context, "⚠️ Nom et prénom requis", Toast.LENGTH_SHORT).show()
                return@launch
            }

            repository.insertBeneficiary(beneficiary)
            Toast.makeText(context, "✅ Bénéficiaire ajouté localement", Toast.LENGTH_SHORT).show()
            Log.i("BeneficiaryViewModel", "✅ Bénéficiaire ajouté : ${beneficiary.nom} ${beneficiary.prenom}")
        } catch (e: Exception) {
            Toast.makeText(context, "❌ Erreur ajout bénéficiaire", Toast.LENGTH_SHORT).show()
            Log.e("BeneficiaryViewModel", "❌ Exception addBeneficiary", e)
        }
    }

    fun removeBeneficiary(context: Context, beneficiary: Beneficiary) = viewModelScope.launch {
        try {
            repository.deleteBeneficiary(beneficiary)
            Toast.makeText(context, "✅ Bénéficiaire supprimé localement", Toast.LENGTH_SHORT).show()
            Log.i("BeneficiaryViewModel", "✅ Bénéficiaire supprimé : ${beneficiary.nom} ${beneficiary.prenom}")
        } catch (e: Exception) {
            Toast.makeText(context, "❌ Erreur suppression bénéficiaire", Toast.LENGTH_SHORT).show()
            Log.e("BeneficiaryViewModel", "❌ Exception removeBeneficiary", e)
        }
    }

    fun updateBeneficiarySyncStatus(id: Long, synced: Boolean) = viewModelScope.launch {
        try {
            repository.updateBeneficiarySyncStatus(id, synced)
            Log.i("BeneficiaryViewModel", "✅" +
                    " Sync status mis à jour pour bénéficiaire $id")
        } catch (e: Exception) {
            Log.e("BeneficiaryViewModel", "❌ Exception updateBeneficiarySyncStatus", e)
        }
    }

    private val _currentBeneficiary = MutableStateFlow(
        Beneficiary(
            idBen = 0L,
            villageIdF = 1,
            codeVillageF = "RAD",
            idSprojF = 100,
            benStatus = "Enable",
            sequences = "RAS",
            profileStatusId = 1,
            maritalStatusId = 1,
            handicap = "RAS",
            humanStatusId = 1,
            dateEnregistrement = "",
            codeBenef = generateCode(),
            phone = "",
            prenom = "",
            nom = "",
            postnom = "",
            age = 21,
            sexe = "Masculin",
            numCarteBen = "",
            chefMenage = "OUI",
            enfantChefMenage = "NON",
            repondantPresent = "OUI",
            lienRepondant = "FRERE",
            repondantNoms = "",
            activityStatus = "DEVELOPPEUR",
            tailleMenage = 1,
            enfants6_17Ecole = 0,
            handicapeDansMenage = "NON",
            personneAgeeDansMenage = "NON",
            maladeChroniqueDansMenage = "NON",
            enfantMalnutriDansMenage = "NON",
            scoreVulnerabiliteMembreMenage = 0,
            sourceRevenusPrincipale = "TRAVAIL",
            autresSourceRevenuDansMenage = "RAS",
            habitationMenage = "OUI",
            toit = "TOLE",
            mur = "BANQUE",
            sol = "TERRE",
            nbrePieces = 1,
            nbrePrsParPiece = 1,
            proprietaireParcelle = "OUI",
            scoreHabitation = 0,
            accesToilettes = "OUI",
            distanceAccesToilettes = 1,
            accesEauPotable = "OUI",
            scoreWASH = 0,
            campagneAgricolePrecedente = "OUI",
            produitsCultives = "MAIS",
            nbreSacsRecoltes = 0,
            autresInfos = "RAS",
            nbreChamps = 0,
            nbreChampsAgricoles = 0,
            nbreMaisons = 0,
            nbreCases = 0,
            nbreHoues = 0,
            nbreCharettes = 0,
            nbreMotos = 0,
            nbreVelos = 0,
            nbreBovins = 0,
            nbreOvins = 0,
            nbreCaprins = 0,
            nbreVolails = 0,
            grosElevage = "NON",
            scoreBiensMenage = 0,
            nbreRepasParJour = 2,
            nbreConsommationAlimentsNonPreferes7jrs = 0,
            aidePourManger7jrs = "NON",
            empruntsPourManger7jrs = 0,
            diminuerQuantiteRepas7jrs = 0,
            limiterConsommationAdultes7jrs = 0,
            diminuerNbreRepas7jrs = 0,
            nbreUtiliserEnfantPourManger = 0,
            autresInfosAlimentation = "RAS",
            scoreAlimentation = 0,
            commentairesSuggestions = "RAS",
            numTicket = "",
            geopointBen = "",
            remarkBen = "RAS",
            photo = null,
            auteur = "FIDELE",
            qrCode = null,
            scoreStatutMatrim = 0,
            scoreDemographique = 0,
            scoreFinal = 0,
            isSynced = false,
            latitude = "0.0",
            longitude = "0.0",
            altitude = "0.0",
            precision = "0.0"
        )
    )


//    private val _currentBeneficiary = MutableStateFlow(
//        Beneficiary(
//            idBen = 0L,
//            villageIdF = 0,
//            codeVillageF = "",
//            idSprojF = 0,
//            benStatus = "",
//            sequences = "",
//            profileStatusId = 0,
//            maritalStatusId = 0,
//            handicap = "OUI",
//            humanStatusId = 0,
//            dateEnregistrement = "",
//            codeBenef = "",
//            phone = "",
//            prenom = "",
//            nom = "",
//            postnom = "",
//            age = 0,
//            sexe = "",
//            numCarteBen = "",
//            chefMenage = "NON",
//            enfantChefMenage = "NON",
//            repondantPresent = "NON",
//            lienRepondant = "",
//            repondantNoms = "",
//            activityStatus = "",
//            tailleMenage = 0,
//            enfants6_17Ecole = 0,
//            handicapeDansMenage = "NON",
//            personneAgeeDansMenage = "NON",
//            maladeChroniqueDansMenage = "NON",
//            enfantMalnutriDansMenage = "NON",
//            scoreVulnerabiliteMembreMenage = 0,
//            sourceRevenusPrincipale = "",
//            autresSourceRevenuDansMenage = "",
//            habitationMenage = "NON",
//            toit = "",
//            mur = "NON",
//            sol = "NON",
//            nbrePieces = 0,
//            nbrePrsParPiece = 0,
//            proprietaireParcelle = "NON",
//            scoreHabitation = 0,
//            accesToilettes = "NON",
//            distanceAccesToilettes = 0,
//            accesEauPotable = "NON",
//            scoreWASH = 0,
//            campagneAgricolePrecedente = "NON",
//            produitsCultives = "",
//            nbreSacsRecoltes = 0,
//            autresInfos = "",
//            nbreChamps = 0,
//            nbreChampsAgricoles = 0,
//            nbreMaisons = 0,
//            nbreCases = 0,
//            nbreHoues = 0,
//            nbreCharettes = 0,
//            nbreMotos = 0,
//            nbreVelos = 0,
//            nbreBovins = 0,
//            nbreOvins = 0,
//            nbreCaprins = 0,
//            nbreVolails = 0,
//            grosElevage = "NON",
//            scoreBiensMenage = 0,
//            nbreRepasParJour = 0,
//            nbreConsommationAlimentsNonPreferes7jrs = 0,
//            aidePourManger7jrs = "NON",
//            empruntsPourManger7jrs = 0,
//            diminuerQuantiteRepas7jrs = 0,
//            limiterConsommationAdultes7jrs = 0,
//            diminuerNbreRepas7jrs = 0,
//            nbreUtiliserEnfantPourManger = 0,
//            autresInfosAlimentation = "",
//            scoreAlimentation = 0,
//            commentairesSuggestions = "",
//            numTicket = "",
//            geopointBen = "",
//            remarkBen = "",
//            photo = null,
//            auteur = "",
//            qrCode = null,
//            scoreStatutMatrim = 0,
//            scoreDemographique = 0,
//            scoreFinal = 0,
//            isSynced = false,
//            latitude = "0",
//            longitude = "0",
//            altitude = "0.0",
//            precision = "0.0"
//        )
//    )

    val currentBeneficiary: StateFlow<Beneficiary> = _currentBeneficiary

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateBeneficiary(update: Beneficiary) {
        _currentBeneficiary.value = update
    }


    // ------------------ API ------------------
    @RequiresApi(Build.VERSION_CODES.O)
    fun createBeneficiary(context: Context, beneficiary: Beneficiary) = viewModelScope.launch {
        try {
            // 🔒 Validation minimale
            if (beneficiary.nom.isNullOrBlank() || beneficiary.prenom.isNullOrBlank()) {
                Toast.makeText(context, "⚠️ Nom et prénom requis", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // 📷 Encodage photo et QR
            val photoBase64 = beneficiary.photo?.let { path ->
                val file = File(path.toUri().path ?: "")
                if (file.exists()) Base64.encodeToString(file.readBytes(), Base64.NO_WRAP) else null
            }

            val qrBase64 = beneficiary.qrCode?.let { path ->
                val file = File(path.toUri().path ?: "")
                if (file.exists()) Base64.encodeToString(file.readBytes(), Base64.NO_WRAP) else null
            }

            // 🧠 Construction du payload API
            val request = BeneficiaryRequest(
                villageIdF = beneficiary.villageIdF,
                codeVillageF = beneficiary.codeVillageF,
                idSprojF = beneficiary.idSprojF,
                idProfileStatusF = beneficiary.profileStatusId.takeIf { it!! > 0 },
                idMaritalStatusF = beneficiary.maritalStatusId.takeIf { it!! > 0 },
                handicap = beneficiary.handicap.takeIf { it?.isNotBlank() == true && it?.uppercase() != "RAS" },
                idHumanStatusF = beneficiary.humanStatusId.takeIf { it!! > 0 },
                dateEnregistrement = beneficiary.dateEnregistrement.ifBlank { isoNow() },
                codeBenef = beneficiary.codeBenef.ifBlank { generateCode() },
                phone = beneficiary.phone.orEmpty(),
                prenom = beneficiary.prenom.orEmpty(),
                nom = beneficiary.nom.orEmpty(),
                postnom = beneficiary.postnom.orEmpty(),
                age = beneficiary.age.toString(), // SQL = varchar
                sexe = beneficiary.sexe.orEmpty(),
                numCarteBen = beneficiary.numCarteBen.orEmpty(),
                chefMenage = beneficiary.chefMenage.uppercase(),
                enfantChefMenage = beneficiary.enfantChefMenage.uppercase(),
                repondantPresent = beneficiary.repondantPresent?.uppercase(),
                lienRepondant = sanitizeText(beneficiary.lienRepondant),
                repondantNoms = beneficiary.repondantNoms,
                activityStatus = beneficiary.activityStatus,
                tailleMenage = beneficiary.tailleMenage,
                enfants6_17Ecole = beneficiary.enfants6_17Ecole.toString(),
                handicapDansMenage = beneficiary.handicapeDansMenage.uppercase(),
                personneAgeeDansMenage = beneficiary.personneAgeeDansMenage.uppercase(),
                maladeChroniqueDansMenage = beneficiary.maladeChroniqueDansMenage.uppercase(),
                enfantMalnutriDansMenage = beneficiary.enfantMalnutriDansMenage.uppercase(),
                scoreMembreMenage = beneficiary.scoreVulnerabiliteMembreMenage,
                sourceRevenusPrincipale = beneficiary.sourceRevenusPrincipale,
                autresSourceRevenuDansMenage = beneficiary.autresSourceRevenuDansMenage?.toIntOrNull()
                    ?: 0,
                habitationMenage = beneficiary.habitationMenage.uppercase(),
                toit = beneficiary.toit,
                mur = beneficiary.mur?.uppercase(),
                sol = beneficiary.sol?.uppercase(),
                nbrePieces = beneficiary.nbrePieces.toString(),
                nbrePrsParPiece = beneficiary.nbrePrsParPiece.toString(),
                proprietaireParcelle = beneficiary.proprietaireParcelle.uppercase(),
                scoreHabitation = beneficiary.scoreHabitation,
                accesToilettes = beneficiary.accesToilettes.uppercase(),
                distanceAccesToilettes = beneficiary.distanceAccesToilettes.toString(),
                accesEauPotable = beneficiary.accesEauPotable.uppercase(),
                scoreWash = beneficiary.scoreWASH,
                campagneAgricolePrecedente = beneficiary.campagneAgricolePrecedente.uppercase(),
                produitsCultives = beneficiary.produitsCultives,
                nbreSacsRecoltes = beneficiary.nbreSacsRecoltes.toString(),
                autresInfos = sanitizeText(beneficiary.autresInfos),
                nbreChamps = beneficiary.nbreChamps,
                nbreChampsAgricoles = beneficiary.nbreChampsAgricoles,
                nbreMaisons = beneficiary.nbreMaisons,
                nbreCases = beneficiary.nbreCases,
                nbreHoues = beneficiary.nbreHoues,
                nbreCharrettes = beneficiary.nbreCharettes,
                nbreMotos = beneficiary.nbreMotos,
                nbreVelos = beneficiary.nbreVelos,
                nbreBovins = beneficiary.nbreBovins,
                nbreOvins = beneficiary.nbreOvins,
                nbreCaprins = beneficiary.nbreCaprins,
                nbreVolails = beneficiary.nbreVolails,
                grosElevage = beneficiary.grosElevage.uppercase(),
                scoreBiensMenage = beneficiary.scoreBiensMenage,
                nbreRepasParJour = beneficiary.nbreRepasParJour.toString(),
                nbreConsommationAlimentsNonPreferes7jrs = beneficiary.nbreConsommationAlimentsNonPreferes7jrs,
                aidePourManger7jrs = beneficiary.aidePourManger7jrs.uppercase(),
                empruntsPourManger7jrs = beneficiary.empruntsPourManger7jrs,
                diminuerQuantiteRepas7jrs = beneficiary.diminuerQuantiteRepas7jrs,
                limiterConsommationAdultes7jrs = beneficiary.limiterConsommationAdultes7jrs,
                diminuerNbreRepas7jrs = beneficiary.diminuerNbreRepas7jrs,
                nbreUtiliserEnfantPourManger = beneficiary.nbreUtiliserEnfantPourManger,
                autresInfosAlimentation = sanitizeText(beneficiary.autresInfosAlimentation),
                scoreAlimentation = beneficiary.scoreAlimentation,
                commentairesSuggestions = sanitizeText(beneficiary.commentairesSuggestions),
                numTicket = beneficiary.numTicket,
                geopointBen = beneficiary.geopointBen,
                latitude = beneficiary.latitude,
                longitude = beneficiary.longitude,
                altitude = beneficiary.altitude,
                precisionBen = beneficiary.precision,
                sequences = beneficiary.sequences,
                benStatus = beneficiary.benStatus.ifBlank { "Enable" },
                remarkBen = sanitizeText(beneficiary.remarkBen),
                photo = photoBase64,
                auteur = beneficiary.auteur,
                qrCode = qrBase64,
                scoreStatutMatrim = beneficiary.scoreStatutMatrim,
                scoreDemographique = beneficiary.scoreDemographique,
                scoreFinal = beneficiary.scoreFinal
            )

            val json = Gson().toJson(request)
            Log.d("BeneficiaryRequest", "📤 Payload envoyé: $json")

            val response = RetrofitClient.api.createBeneficiary(request)

            if (response.isSuccessful) {
                addBeneficiary(context, beneficiary.copy(isSynced = true))
                updateBeneficiarySyncStatus(beneficiary.idBen, true)
                Toast.makeText(context, "✅ Bénéficiaire créé sur API", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "⚠️ Erreur création bénéficiaire API: ${response.code()}", Toast.LENGTH_SHORT).show()
                Log.w("BeneficiaryViewModel", "⚠️ Erreur API createBeneficiary: ${response.code()}")
            }

        } catch (e: Exception) {
            Toast.makeText(context, "❌ Exception création bénéficiaire", Toast.LENGTH_SHORT).show()
            Log.e("BeneficiaryViewModel", "❌ Exception createBeneficiary", e)
        }
    }

    fun sanitizeText(input: String?): String? {
        return input?.trim()?.takeIf { it.isNotBlank() && !it.equals("RAS", ignoreCase = true) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isoNow(): String = LocalDate.now().toString()

    fun generateCode(): String = "BEN-${System.currentTimeMillis()}"


    @RequiresApi(Build.VERSION_CODES.O)
    fun createBeneficiaryTestPayload(): BeneficiaryRequest {
        return BeneficiaryRequest(
            villageIdF = 1,
            codeVillageF = "RAD",
            idSprojF = 1,
            idProfileStatusF = 1,
            idMaritalStatusF = 1,
            handicap = "RAS", // varchar(100), valeur canonique
            idHumanStatusF = 1,
            dateEnregistrement = "2025-11-16",
            codeBenef = "TEST",
            phone = "827808428",
            prenom = "FIDELE",
            nom = "LUKEKA",
            postnom = "KIBASOMBA",
            age = "21",
            sexe = "Masculin", // correspond à l’exemple SQL
            numCarteBen = "875-421-1",
            chefMenage = "OUI",
            enfantChefMenage = "NON",
            repondantPresent = "OUI",
            lienRepondant = "FRERE",
            repondantNoms = "LUKEKA KIBASOMBA FIDELE",
            activityStatus = "DEVELOPPEUR",
            tailleMenage = 12,
            enfants6_17Ecole = "3",
            handicapDansMenage = "NON",
            personneAgeeDansMenage = "NON",
            maladeChroniqueDansMenage = "NON",
            enfantMalnutriDansMenage = "NON",
            scoreMembreMenage = 23,
            sourceRevenusPrincipale = "TRAVAIL",
            autresSourceRevenuDansMenage = 12,
            habitationMenage = "OUI",
            toit = "TOIT",
            mur = "AUCUN",
            sol = "AUCUN",
            nbrePieces = "3",
            nbrePrsParPiece = "2",
            proprietaireParcelle = "NON",
            scoreHabitation = 45,
            accesToilettes = "ACCES",
            distanceAccesToilettes = "12",
            accesEauPotable = "NON",
            scoreWash = 12,
            campagneAgricolePrecedente = "RAR",
            produitsCultives = "RA",
            nbreSacsRecoltes = "DA",
            autresInfos = "RA",
            nbreChamps = 12,
            nbreChampsAgricoles = 12,
            nbreMaisons = 23,
            nbreCases = 21,
            nbreHoues = 12,
            nbreCharrettes = 15,
            nbreMotos = 14,
            nbreVelos = 12,
            nbreBovins = 13,
            nbreOvins = 24,
            nbreCaprins = 25,
            nbreVolails = 23,
            grosElevage = "NON", // ⚠️ doit être OUI/NON
            scoreBiensMenage = 12,
            nbreRepasParJour = "2", // ⚠️ pas "RAS"
            nbreConsommationAlimentsNonPreferes7jrs = 12,
            aidePourManger7jrs = "NON",
            empruntsPourManger7jrs = 21,
            diminuerQuantiteRepas7jrs = 32,
            limiterConsommationAdultes7jrs = 32,
            diminuerNbreRepas7jrs = 21,
            nbreUtiliserEnfantPourManger = 45,
            autresInfosAlimentation = "RAS",
            scoreAlimentation = 45,
            commentairesSuggestions = "RAS",
            numTicket = "RAS",
            geopointBen = "RAS",
            latitude = "0.0", // ⚠️ numérique
            longitude = "0.0",
            altitude = "0.0",
            precisionBen = "0.0",
            sequences = "RAS",
            benStatus = "Enable",
            remarkBen = "RAS",
            photo = "RAS",
            auteur = "1",
            qrCode = "RAS",
            scoreStatutMatrim = 12,
            scoreDemographique = 12,
            scoreFinal = 12
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun testBeneficiaryAPI(context: Context) = viewModelScope.launch {
        val request = createBeneficiaryTestPayload()
        val json = Gson().toJson(request)
        Log.d("BeneficiaryTestPayload", "📤 Payload: $json")

        val response = RetrofitClient.api.createBeneficiary(request)
        if (response.isSuccessful) {
            Toast.makeText(context, "✅ Test payload accepté par l’API", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "⚠️ Erreur API test: ${response.code()}", Toast.LENGTH_SHORT).show()
        }
    }

    // ------------------ SITES ------------------
//    private val _sites = MutableStateFlow<List<Site>>(emptyList())
//    val sites: StateFlow<List<Site>> = _sites

//    private val _selectedSite = mutableStateOf<Site?>(null)
//    val selectedSite: State<Site?> = _selectedSite
//
//    fun selectSite(site: Site) {
//        _selectedSite.value = site
//        Log.i("AppViewModel", "🏭 Site sélectionné : ${site.nameWarehouse}")
//    }
//
//    fun clearSelectedSite() {
//        _selectedSite.value = null
//    }

//    fun fetchSites(context: Context) {
//        viewModelScope.launch {
//            try {
//                val list = repository.fetchSites()
//                _sites.value = list
//                Toast.makeText(context, "✅ ${list.size} sites récupérés", Toast.LENGTH_SHORT).show()
//            } catch (e: Exception) {
//                _sites.value = emptyList()
//                Toast.makeText(context, "❌ Erreur récupération sites", Toast.LENGTH_SHORT).show()
//                Log.e("AppViewModel", "❌ Exception fetchSites", e)
//            }
//        }
//    }

    // ------------------ DISTRIBUTION ------------------
    private val _currentDistribution = MutableStateFlow(
        Distribution(
            siteId = 40,
            nomComplet = "",
            sexe = "",
            phone = "",
            image = "",
            tailleMenage = 1,
            superficie = "2.5",
            latitude = "-4.3235",
            longitude = "15.2987",
            altitude = "350",
            precision = "5",
            kgMais = 0,
            kgRiz = 0,
            kgManioc = 0,
            kgSoja = 0,
            kgDap = 0,
            kgKcl = 0,
            kgUree = 0,
            kgNpk = 0,
            suggestion = "teste API ok vieux",
            usersId = 2
        )
    )
    val currentDistribution: StateFlow<Distribution> = _currentDistribution

    fun updateDistribution(updated: Distribution) {
        _currentDistribution.value = updated
    }

    private val _distributions = MutableStateFlow<List<Distribution>>(emptyList())
    val distributions: StateFlow<List<Distribution>> = _distributions

    // ------------------ Mapping vers API ------------------
    fun Distribution.toRequest() = DistributionRequest(
        fullname = nomComplet,
        gender = sexe,
        phone = phone,
        numberOfChildren = tailleMenage,
        photo = image,
        landArea = superficie,
        seed = "Mais",
        fertilizer = "dap",
        maizeQty = kgMais,
        riceQty = kgRiz,
        cassavaQty = kgManioc,
        soybeanQty = kgSoja,
        dapQty = kgDap,
        kclQty = kgKcl,
        ureeQty = kgUree,
        npk = kgNpk,
        suggestion = suggestion,
        warehouseId = siteId,
        beneficiarieId = beneficiarieId,
        usersId = usersId,
        latitudeWrhs = latitude?.toDoubleOrNull(),
        longitudeWrhs = longitude?.toDoubleOrNull(),
        altitudeWrhs = altitude?.toDoubleOrNull(),
        precisionWrhs = precision?.toDoubleOrNull()
    )

    // ------------------ Options semences/engrais ------------------
    // ViewModel
    private val _options = mutableStateMapOf(
        "Riz" to false, "Maïs" to false, "Manioc" to false, "Soja" to false,
        "DAP" to false, "KCL" to false, "Urée" to false, "NPK" to false
    )

    fun updateOption(option: String, checked: Boolean, kgValue: Int) {
        Log.d("AppViewModel", "updateOption: option=$option, checked=$checked, kgValue=$kgValue")
        _options[option] = checked

        val current = _currentDistribution.value
        val kg = if (checked) kgValue else 0

        val updated = when (option) {
            "Riz" -> current.copy(kgRiz = kg)
            "Maïs" -> current.copy(kgMais = kg)
            "Manioc" -> current.copy(kgManioc = kg)
            "Soja" -> current.copy(kgSoja = kg)
            "DAP" -> current.copy(kgDap = kg)
            "KCL" -> current.copy(kgKcl = kg)
            "Urée" -> current.copy(kgUree = kg)
            "NPK" -> current.copy(kgNpk = kg)
            else -> current
        }

        _currentDistribution.value = updated
        Log.d("AppViewModel", "currentDistribution updated: $updated")
    }

    fun getOptionState(option: String): Boolean {
        val state = _options[option] ?: false
        Log.v("AppViewModel", "getOptionState: option=$option, state=$state")
        return state
    }


    // ------------------ Persistance Room ------------------
    fun saveDistribution() = viewModelScope.launch {
        repository.saveDistribution(_currentDistribution.value)
        loadDistributions()
    }

    fun loadDistributions() = viewModelScope.launch {
        _distributions.value = repository.getDistributions()
    }

    // ------------------ Image ------------------
    fun updateImage(path: String) {
        val file = File(path.toUri().path ?: "")
        val base64Image = file.takeIf { it.exists() }?.readBytes()?.let {
            Base64.encodeToString(it, Base64.NO_WRAP)
        }
        _currentDistribution.value = _currentDistribution.value.copy(image = base64Image)
    }

    fun clearImage() {
        _currentDistribution.value = _currentDistribution.value.copy(image = null)
    }

    fun sendDistribution(context: Context) {
        viewModelScope.launch {
            try {
                val request = _currentDistribution.value.toRequest()
                val success = repository.createDistribution(context, request)

                val message = if (success) {
                    "✅ Distribution envoyée avec succès"
                } else {
                    "⚠️ Erreur lors de l'envoi de la distribution"
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                Log.i("AppViewModel", message)

                // ⚡ Optionnel : recharger les distributions locales après envoi
                if (success) loadDistributions()

            } catch (e: Exception) {
                Toast.makeText(context, "❌ Exception lors de l'envoi", Toast.LENGTH_SHORT).show()
                Log.e("AppViewModel", "❌ Exception sendDistribution", e)
            }
        }
    }



    fun saveDistributionToDb(distribution: Distribution) {
        insertDistribution(
            maize = distribution.kgMais,
            rice = distribution.kgRiz,
            cassava = distribution.kgManioc,
            soybean = distribution.kgSoja,
            dap = distribution.kgDap,
            kcl = distribution.kgKcl,
            uree = distribution.kgUree,
            npkVal = distribution.kgNpk,
            suggestionVal = distribution.suggestion,
            warehouse = distribution.siteId,
            beneficiary = 1,
            user = 3
        )
    }


    private val _sitesRoom = MutableStateFlow<List<SiteEntity>>(emptyList())
    val sitesRoom: StateFlow<List<SiteEntity>> = _sitesRoom

//    fun loadSitesLocal() {
//        viewModelScope.launch {
//            repository.getSitesLocal().collect {
//                _sitesRoom.value = it
//            }
//        }
//    }

    fun fetchSitesFromApi(context: Context) {
        viewModelScope.launch {
            val list = repository.fetchSitesFromApi()
            _sitesRoom.value = list
            Toast.makeText(context, "✅ ${list.size} sites enregistrés", Toast.LENGTH_SHORT).show()
        }
    }

//    private val _sites = MutableStateFlow<List<SiteEntity>>(emptyList())
//    val sites: StateFlow<List<SiteEntity>> = _sites
//
//    fun fetchSites(context: Context) {
//        viewModelScope.launch {
//            try {
//                val list = repository.fetchSites()
//                _sites.value = list
//                Toast.makeText(context, "✅ ${list.size} sites enregistrés", Toast.LENGTH_SHORT).show()
//            } catch (e: Exception) {
//                _sites.value = emptyList()
//                Toast.makeText(context, "❌ Erreur récupération sites", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    fun loadSitesLocal() {
//        viewModelScope.launch {
//            repository.getSitesLocal().collect {
//                _sites.value = it
//            }
//        }
//    }


    private val _sites = MutableStateFlow<List<SiteEntity>>(emptyList())
    val sites: StateFlow<List<SiteEntity>> = _sites

    private val _selectedSite = mutableStateOf<SiteEntity?>(null)
    val selectedSite: State<SiteEntity?> = _selectedSite

    fun selectSite(site: SiteEntity) {
        _selectedSite.value = site
        _currentDistribution.value = _currentDistribution.value.copy(siteId = site.warehouseId)
    }

    fun clearSelectedSite() {
        _selectedSite.value = null
        _currentDistribution.value = _currentDistribution.value.copy(siteId = 0)
    }

    fun fetchSites(context: Context) {
        viewModelScope.launch {
            val list = repository.fetchSitesFromApi()
            _sites.value = list
            Toast.makeText(context, "✅ ${list.size} sites enregistrés", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadSitesLocal() {
        viewModelScope.launch {
            repository.getSitesLocal().collect {
                _sites.value = it
            }
        }
    }

}