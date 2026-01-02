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
import androidx.core.net.toUri
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.fs.dia_padcv.data.local.entities.Beneficiary
import id.fs.dia_padcv.data.local.entities.Colis
import id.fs.dia_padcv.data.local.entities.Distribution
import id.fs.dia_padcv.data.local.entities.Entrepot
import id.fs.dia_padcv.data.local.entities.SiteEntity
import id.fs.dia_padcv.data.local.entities.SyncStatus
import id.fs.dia_padcv.data.local.entities.Village
import id.fs.dia_padcv.data.remote.api.BeneficiaryRequest
import id.fs.dia_padcv.data.remote.api.ColisRequest
import id.fs.dia_padcv.data.remote.api.RetrofitClient
import id.fs.dia_padcv.data.remote.api.Warehouse
import id.fs.dia_padcv.data.remote.api.WarehouseRequest
import id.fs.dia_padcv.data.repository.AppRepository
import id.fs.dia_padcv.ui.utils.LoginResult
import id.fs.dia_padcv.ui.utils.SyncResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate
import id.fs.dia_padcv.data.remote.api.Village as ApiVillage

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    // ------------------ LOGIN ------------------
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    private val _loginError = mutableStateOf<String?>(null)
    val loginError: State<String?> = _loginError

    private val now: () -> Long = { System.currentTimeMillis() }

    fun login(context: Context, username: String, password: String) {
        viewModelScope.launch {
            Log.d("ViewModel", "▶️ Début login: username=$username")
            _isLoading.value = true
            _loginError.value = null

            val passwordHash = password

            when (val result = repository.login(context, username, passwordHash)) {
                is LoginResult.Success -> {
                    Log.d("ViewModel", "✅ Login réussi: ${result.user.username} / ${result.user.role}")
                    _isLoggedIn.value = true
                }
                LoginResult.InvalidCredentials -> {
                    Log.e("ViewModel", "❌ Identifiants invalides pour $username")
                    _loginError.value = "Nom d’utilisateur ou mot de passe incorrect."
                }
                LoginResult.NetworkError -> {
                    Log.e("ViewModel", "❌ Réseau indisponible pour $username")
                    _loginError.value = "Connexion impossible : réseau indisponible."
                }
            }

            _isLoading.value = false
            Log.d("ViewModel", "🏁 Fin login: username=$username, isLoggedIn=${_isLoggedIn.value}")
        }
    }

    // 🔹 Récupération des infos utilisateur
    fun getUserInfo(context: Context): Flow<UserInfo> {
        return context.dataStore.data.map { prefs ->
            UserInfo(
                username = prefs[stringPreferencesKey("username")],
                email = prefs[stringPreferencesKey("email")],
                role = prefs[stringPreferencesKey("role")],
                status = prefs[stringPreferencesKey("status")],
                createdAt = prefs[stringPreferencesKey("created_at")]
            )
        }
    }

    data class UserInfo(
        val username: String?,
        val email: String?,
        val role: String?,
        val status: String?,
        val createdAt: String?
    )
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

    // 🔹 Sites depuis Room
    val sites: StateFlow<List<SiteEntity>> = repository.getSitesLocal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // 🔹 Villages depuis Room
    val villages: StateFlow<List<Village>> = repository.getVillagesLocal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // 🔹 Distributions depuis Room
    val distributions: StateFlow<List<Distribution>> = repository.getAllDistributionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ------------------ VILLAGES ------------------
    private val _selectedVillage = mutableStateOf<ApiVillage?>(null)
    val selectedVillage: State<ApiVillage?> = _selectedVillage

    fun selectVillage(village: ApiVillage) {
        _selectedVillage.value = village
        Log.i("AppViewModel", "🏡 Village sélectionné : ${village.name_village}")
    }

    fun clearSelectedVillage() {
        _selectedVillage.value = null
    }

    private val _currentDistribution = MutableStateFlow(
        Distribution(
            siteId = 0,
            nomComplet = "",
            sexe = "",
            phone = "",
            image = null,
            tailleMenage = 0,
            superficie = 0,
            latitude = "",
            longitude = "",
            altitude = "",
            precision = "",
            kgMais = 0,
            kgRiz = 0,
            kgManioc = 0,
            kgSoja = 0,
            kgDap = 0,
            kgKcl = 0,
            kgUree = 0,
            kgNpk = 0,
            suggestion = "",
            usersId = 0
        )
    )
    val currentDistribution: StateFlow<Distribution> = _currentDistribution


    // Message UI
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage

    // Options semences/engrais
    private val _options = mutableStateMapOf(
        "Riz" to false, "Maïs" to false, "Manioc" to false, "Soja" to false,
        "DAP" to false, "KCL" to false, "Urée" to false, "NPK" to false
    )

    // ------------------ DISTRIBUTIONS ------------------

    fun updateDistribution(updated: Distribution) {
        _currentDistribution.value = updated
    }
    // ===================== SAVE LOCAL =====================
    fun saveDistributionLocal(onResult: (Long?) -> Unit) = viewModelScope.launch {
        try {
            val distribution = _currentDistribution.value

            // 🔹 Sauvegarde locale avec statut PENDING
            val stamped = distribution.copy(syncStatus = SyncStatus.PENDING, lastModified = now())
            val idLocal = repository.saveDistributionLocal(stamped) // ⚡ dao.insert renvoie l'idLocal

            _uiMessage.value = "💾 Distribution sauvegardée en local (idLocal=$idLocal)"

            // ✅ reset formulaire (objet vide)
            _currentDistribution.value = Distribution(
                siteId = 0,
                nomComplet = "",
                sexe = "",
                phone = "",
                image = "",
                tailleMenage = 0,
                superficie = 0,
                latitude = "",
                longitude = "",
                altitude = "",
                precision = "",
                kgMais = 0,
                kgRiz = 0,
                kgManioc = 0,
                kgSoja = 0,
                kgDap = 0,
                kgKcl = 0,
                kgUree = 0,
                kgNpk = 0,
                suggestion = "",
                usersId = 0
            )

            // ⚡ renvoyer l'idLocal inséré
            onResult(idLocal)
        } catch (e: Exception) {
            _uiMessage.value = "❌ Erreur lors de la sauvegarde locale"
            onResult(null)
        }
    }

    // ===================== SEND REMOTE =====================
    fun sendDistribution(idLocal: Long) = viewModelScope.launch {
        try {
            val success = repository.createDistributionRemote(idLocal) // ⚡ relire depuis Room
            _uiMessage.value = if (success) {
                "✅ Distribution envoyée avec succès"
            } else {
                "⚠️ Erreur lors de l'envoi, mais sauvegarde locale OK"
            }
        } catch (e: Exception) {
            _uiMessage.value = "❌ Exception lors de l'envoi, mais sauvegarde locale OK"
        }
    }


    fun clearUiMessage() { _uiMessage.value = null }

    // ------------------ OPTIONS SEMENCES/ENGRAIS ------------------
    fun updateOption(label: String, hasValue: Boolean, qty: Int) {
        val dist = _currentDistribution.value
        val newDist = when (label) {
            "Riz" -> dist.copy(
                hasRiz = if (hasValue) "OUI" else "NON",
                kgRiz = if (hasValue) qty else 0
            )
            "Maïs" -> dist.copy(
                hasMais = if (hasValue) "OUI" else "NON",
                kgMais = if (hasValue) qty else 0
            )
            "Manioc" -> dist.copy(
                hasManioc = if (hasValue) "OUI" else "NON",
                kgManioc = if (hasValue) qty else 0
            )
            "Soja" -> dist.copy(
                hasSoja = if (hasValue) "OUI" else "NON",
                kgSoja = if (hasValue) qty else 0
            )
            "DAP" -> dist.copy(
                hasDap = if (hasValue) "OUI" else "NON",
                kgDap = if (hasValue) qty else 0
            )
            "KCL" -> dist.copy(
                hasKcl = if (hasValue) "OUI" else "NON",
                kgKcl = if (hasValue) qty else 0
            )
            "Urée" -> dist.copy(
                hasUree = if (hasValue) "OUI" else "NON",
                kgUree = if (hasValue) qty else 0
            )
            "NPK" -> dist.copy(
                hasNpk = if (hasValue) "OUI" else "NON",
                kgNpk = if (hasValue) qty else 0
            )
            else -> dist
        }
        _currentDistribution.value = newDist
    }

    fun getOptionState(label: String): Boolean {
        val dist = _currentDistribution.value
        return when (label) {
            "Riz" -> dist.hasRiz == "OUI"
            "Maïs" -> dist.hasMais == "OUI"
            "Manioc" -> dist.hasManioc == "OUI"
            "Soja" -> dist.hasSoja == "OUI"
            "DAP" -> dist.hasDap == "OUI"
            "KCL" -> dist.hasKcl == "OUI"
            "Urée" -> dist.hasUree == "OUI"
            "NPK" -> dist.hasNpk == "OUI"
            else -> false
        }
    }


    // ------------------ IMAGE ------------------

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

    // 🔹 Télécharger sites + villages depuis API → Room
    fun downloadSitesAndVillages() {
        viewModelScope.launch {
            val sites = repository.syncSitesFromApi()
            val villages = repository.syncVillagesFromApi()
            // éventuellement afficher un Toast ou un Snackbar
        }
    }

    // ------------------ SYNCHRONISATION ------------------
    fun syncSitesFromApi() {
        viewModelScope.launch {
            val count = repository.syncSitesFromApi()
            Log.i("SyncViewModel", "🌐 Synced $count sites from API → Room")
        }
    }

    fun syncVillagesFromApi() {
        viewModelScope.launch {
            val count = repository.syncVillagesFromApi()
            Log.i("SyncViewModel", "🌐 Synced $count villages from API → Room")
        }
    }

    fun pushUnsyncedDistributions() = viewModelScope.launch {
        repository.pushUnsyncedDistributions()
    }

    // ------------------ SYNC ALL DATA ------------------
    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _currentStepLabel = MutableStateFlow<String?>(null)
    val currentStepLabel: StateFlow<String?> = _currentStepLabel

    private val _syncResults = MutableStateFlow<List<SyncResult>>(emptyList())
    val syncResults: StateFlow<List<SyncResult>> = _syncResults

    // ✅ Ajout du flag de synchronisation
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    private val _syncEvent = MutableSharedFlow<Unit>()
    val syncEvent: SharedFlow<Unit> = _syncEvent

    private val totalSteps = 4

    fun syncAllData() {
        viewModelScope.launch {
            // 🔹 Démarrage
            _isSyncing.value = true
            _progress.value = 0f
            val results = mutableListOf<SyncResult>()

            try {
                _currentStepLabel.value = "📤 Push distributions"
                results += repository.pushUnsyncedDistributions()
                _progress.value = 1f / totalSteps

                _currentStepLabel.value = "🌐 Sync sites"
                results += repository.syncSitesFromApi()
                _progress.value = 2f / totalSteps

                _currentStepLabel.value = "🌐 Sync villages"
                results += repository.syncVillagesFromApi()
                _progress.value = 3f / totalSteps

                _currentStepLabel.value = "📥 Sync distributions"
                results += repository.pushUnsyncedDistributions()
                _progress.value = 1f

                _syncResults.value = results
                _currentStepLabel.value = null

                val errors = results.filter { it.errorMessage != null }
                _uiMessage.value = if (errors.isEmpty()) {
                    "🎯 Synchronisation terminée avec succès"
                } else {
                    "⚠️ Synchronisation terminée avec erreurs:\n" +
                            errors.joinToString("\n") { it.errorMessage ?: "" }
                }

                // 🔹 Résumé global en log
                val totalSuccess = results.sumOf { it.success }
                val totalFailed = results.sumOf { it.failed }
                val totalPending = results.sumOf { it.pending }

                Log.i(
                    "Synchronization",
                    """
                ================== SYNC SUMMARY ==================
                Steps exécutés : ${results.size}
                Total pending   : $totalPending
                Total success   : $totalSuccess
                Total failed    : $totalFailed
                --------------------------------------------------
                Détails :
                ${results.joinToString("\n") { "🔹 ${it.step}: pending=${it.pending}, success=${it.success}, failed=${it.failed}" }}
                ==================================================
                """.trimIndent()
                )
            } finally {
                // 🔹 Arrêt
                _isSyncing.value = false
                _syncEvent.emit(Unit) // ✅ signal unique
            }
        }
    }
    fun setUiMessage(message: String?) {
        _uiMessage.value = message
    }

    fun validateDistribution(step: Int): Boolean {
        val d = currentDistribution.value

        return when (step) {
            1 -> { // ✅ Étape Information Géographique
                val ok = !d.latitude.isNullOrBlank() && !d.longitude.isNullOrBlank() && d.siteId != 0 && d.image != null
                if (!ok) setUiMessage("Veuillez remplir la localisation, sélectionner un site et prendre un photo")
                ok
            }
            2 -> { // ✅ Étape Identité
                val ok = d.nomComplet.isNotBlank() &&
                        !d.phone.isNullOrBlank() &&
                        d.tailleMenage > 0 &&
                        d.sexe.isNotBlank()
                if (!ok) setUiMessage("Veuillez remplir tous les champs d'identité")
                ok
            }
            3 -> { // ✅ Étape Distribution Semence
                val anySelected = listOf(d.kgRiz, d.kgMais, d.kgManioc, d.kgSoja).any { it > 0 }
                val ok = anySelected && d.superficie > 0
                if (!ok) setUiMessage("Veuillez sélectionner au moins une semence et indiquer la quantité")
                ok
            }
            4 -> { // ✅ Étape Distribution Engrais
                val anySelected = listOf(d.kgUree, d.kgKcl, d.kgDap, d.kgNpk).any { it > 0 }
                val ok = anySelected
                if (!ok) setUiMessage("Veuillez sélectionner au moins un engrais et indiquer la quantité")
                ok
            }
            5 -> { // ✅ Étape Commentaires / Suggestions
                val ok = d.suggestion.isNotBlank()
                if (!ok) setUiMessage("Veuillez saisir une suggestion ou un commentaire")
                ok
            }
            6 -> { // ✅ Étape Résumé
                val ok = d.image != null
                if (!ok) setUiMessage("Veuillez ajouter une photo avant de terminer")
                ok
            }
            else -> true
        }
    }
}