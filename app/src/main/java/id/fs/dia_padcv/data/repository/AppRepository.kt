package id.fs.dia_padcv.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.protobuf.LazyStringArrayList.emptyList
import com.google.gson.Gson
import id.fs.dia_padcv.data.local.dao.AppDao
import kotlinx.coroutines.flow.Flow

// Entités locales Room
import id.fs.dia_padcv.data.local.entities.Beneficiary
import id.fs.dia_padcv.data.local.entities.Colis
import id.fs.dia_padcv.data.local.entities.Distribution
import id.fs.dia_padcv.data.local.entities.Entrepot
import id.fs.dia_padcv.data.local.entities.SiteEntity
import id.fs.dia_padcv.data.local.entities.SyncStatus
import id.fs.dia_padcv.data.local.entities.Village as VillageEntity

// Classes API
import id.fs.dia_padcv.data.remote.api.Village as ApiVillage
import id.fs.dia_padcv.data.remote.api.BeneficiaryRequest
import id.fs.dia_padcv.data.remote.api.ColisRequest
import id.fs.dia_padcv.data.remote.api.DistributionDeleteRequest
import id.fs.dia_padcv.data.remote.api.DistributionListResponse
import id.fs.dia_padcv.data.remote.api.DistributionRequest
import id.fs.dia_padcv.data.remote.api.DistributionResponse
import id.fs.dia_padcv.data.remote.api.WarehouseRequest
import id.fs.dia_padcv.data.remote.api.RetrofitClient
import id.fs.dia_padcv.data.remote.api.Site
import id.fs.dia_padcv.data.remote.api.UserData
import id.fs.dia_padcv.ui.SessionPrefs
import id.fs.dia_padcv.ui.utils.LoginResult
import id.fs.dia_padcv.ui.utils.SyncResult
import kotlinx.coroutines.flow.first
import kotlin.collections.emptyList


class AppRepository(private val dao: AppDao) {

    // =========================================================
    // --------------------- DISTRIBUTIONS ---------------------
    // =========================================================
//    val distributions: Flow<List<Distribution>> = dao.getAllDistributions()
//
//    suspend fun insertDistribution(d: Distribution) = dao.insertDistribution(d)
//    suspend fun insertAllDistributions(list: List<Distribution>) = dao.insertAllDistributions(list)
//    suspend fun deleteDistribution(d: Distribution) = dao.deleteDistribution(d)
//
//    suspend fun getUnsyncedDistributions(): List<Distribution> = dao.getUnsyncedDistributions()
//    suspend fun updateDistributionSyncStatus(id: Long, synced: Boolean) =
//        dao.updateDistributionSyncStatus(id, synced)
//    suspend fun deleteSyncedDistributions() = dao.deleteSyncedDistributions()


    // =========================================================
    // ----------------------- ENTREPOTS -----------------------
    // =========================================================
    val entrepots: Flow<List<Entrepot>> = dao.getAllEntrepots()
    suspend fun insertAllEntrepots(list: List<Entrepot>) = dao.insertAllEntrepots(list)


    // =========================================================
    // ------------------------- COLIS -------------------------
    // =========================================================
    val colis: Flow<List<Colis>> = dao.getAllColis()

    suspend fun insertColis(c: Colis) = dao.insertColis(c)
    suspend fun insertAllColis(list: List<Colis>) = dao.insertAllColis(list)
    suspend fun deleteColis(c: Colis) = dao.deleteColis(c)

    suspend fun getUnsyncedColis(): List<Colis> = dao.getUnsyncedColis()
    suspend fun updateColisStatus(id: Long, status: String) = dao.updateColisStatus(id, status)
    suspend fun deleteSyncedColis() = dao.deleteSyncedColis()


    // =========================================================
    // ------------------------ VILLAGES ------------------------
    // =========================================================
    val villages: Flow<List<VillageEntity>> = dao.getAllVillages()
    suspend fun insertAllVillages(list: List<VillageEntity>) = dao.insertAllVillages(list)


    // =========================================================
    // --------------------- BENEFICIARIES ---------------------
    // =========================================================
    val beneficiaries: Flow<List<Beneficiary>> = dao.getAllBeneficiaries()

    suspend fun insertBeneficiary(b: Beneficiary) = dao.insertBeneficiary(b)
    suspend fun insertAllBeneficiaries(list: List<Beneficiary>) = dao.insertAllBeneficiaries(list)
    suspend fun deleteBeneficiary(b: Beneficiary) = dao.deleteBeneficiary(b)

    suspend fun getUnsyncedBeneficiaries(): List<Beneficiary> = dao.getUnsyncedBeneficiaries()
    suspend fun updateBeneficiarySyncStatus(id: Long, synced: Boolean) =
        dao.updateBeneficiarySyncStatus(id, synced)
    suspend fun deleteSyncedBeneficiaries() = dao.deleteSyncedBeneficiaries()

    // =========================================================
    // ------------------------ API PART -----------------------
    // =========================================================
    private val api = RetrofitClient.api

    suspend fun login(context: Context, username: String, passwordHash: String): LoginResult {
        Log.d("Repository", "▶️ Début login pour $username")

        return try {
            Log.d("Repository", "🌐 Tentative API login: username=$username, password_hash=$passwordHash")
            val response = api.login(username, passwordHash)

            Log.d("Repository", "📡 Réponse API: success=${response.isSuccessful}, code=${response.code()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("Repository", "📦 Body reçu: $body")

                if (body != null && body.status.equals("success", ignoreCase = true)) {
                    val user = body.data.firstOrNull()
                    Log.d("Repository", "👤 Utilisateur extrait: $user")

                    if (user != null) {
                        Log.d("Repository", "✅ Login réussi via API: ${user.username} / ${user.role}")
                        // 🔹 Sauvegarde uniquement si API valide
                        SessionPrefs.saveLogin(context, user.username, user.password_hash, user.role)

                        Log.d("Repository", "💾 Sauvegarde préférences: username=${user.username}, password_hash=${user.password_hash}, role=${user.role}")
                        return LoginResult.Success(user)
                    }
                }
                Log.w("Repository", "⚠️ Identifiants invalides côté API")
                LoginResult.InvalidCredentials
            } else {
                Log.e("Repository", "❌ Réponse non successful: ${response.code()}")
                LoginResult.InvalidCredentials
            }
        } catch (e: Exception) {
            Log.e("Repository", "❌ Exception login, tentative offline", e)

            // 🔹 Lecture offline
            val creds = SessionPrefs.getStoredCredentials(context).first()
            Log.d("Repository", "📂 Credentials stockés: username=${creds.first}, passwordHash=${creds.second}")
            Log.d("Repository", "🔎 Comparaison: attendu username=$username, passwordHash=$passwordHash")

            return if (creds.first == username.trim() && creds.second == passwordHash.trim()) {
                Log.d("Repository", "✅ Login offline réussi: $username")
                LoginResult.Success(UserData(username, passwordHash, "offline"))
            } else {
                Log.e("Repository", "❌ Login offline échoué: stockés=(${creds.first}, ${creds.second}) vs attendus=($username, $passwordHash)")
                LoginResult.NetworkError
            }
        }.also {
            Log.d("Repository", "🏁 Fin login pour $username, résultat=$it")
        }
    }


    // 🔹 Création d'entrepôt via API
    suspend fun createWarehouse(request: WarehouseRequest): Boolean {
        return try {
            val response = api.createWarehouse(request)
            response.isSuccessful.also {
                Log.i("Repository", if (it) "✅ Entrepôt créé" else "⚠️ Erreur création entrepôt : ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("Repository", "❌ Erreur API createWarehouse", e)
            false
        }
    }

    // 🔹 Création de colis via API
    suspend fun createColis(request: ColisRequest): Boolean {
        return try {
            val response = api.createColis(request)
            response.isSuccessful.also {
                Log.i("Repository", if (it) "✅ Colis créé" else "⚠️ Erreur création colis : ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("Repository", "❌ Erreur API createColis", e)
            false
        }
    }

    suspend fun createBeneficiary(request: BeneficiaryRequest): Boolean {
        return try {
            val response = api.createBeneficiary(request)
            response.isSuccessful.also {
                Log.i(
                    "Repository",
                    if (it) "✅ Bénéficiaire créé sur API"
                    else "⚠️ Erreur création bénéficiaire: ${response.code()}"
                )
            }
        } catch (e: Exception) {
            Log.e("Repository", "❌ Erreur API createBeneficiary", e)
            false
        }
    }

    private val now: () -> Long = { System.currentTimeMillis() }

    val distributions: Flow<List<Distribution>> = dao.getAllDistributionsFlow()


    // ------------------ MAPPINGS -----------------
    // Helpers pour labels seed/fertilizer si l’API les attend sous forme de chaînes
    private fun Distribution.buildSeedLabel(): String? {
        val list = mutableListOf<String>()
        if (hasMais == "OUI" && kgMais > 0) list.add("maïs")
        if (hasRiz == "OUI" && kgRiz > 0) list.add("riz")
        if (hasManioc == "OUI" && kgManioc > 0) list.add("cassava")
        if (hasSoja == "OUI" && kgSoja > 0) list.add("soybean")
        return list.joinToString(",").ifBlank { null }
    }

    private fun Distribution.buildFertilizerLabel(): String? {
        val list = mutableListOf<String>()
        if (hasDap == "OUI" && kgDap > 0) list.add("DAP")
        if (hasKcl == "OUI" && kgKcl > 0) list.add("KCL")
        if (hasUree == "OUI" && kgUree > 0) list.add("Urée")
        if (hasNpk == "OUI" && kgNpk > 0) list.add("NPK")
        return list.joinToString(",").ifBlank { null }
    }


    // Request -> Entity (pour insert en Room après fetch GET)
    // ===================== MAPPER =====================
    fun Distribution.toRequest(): DistributionRequest {
        return DistributionRequest(
            fullname = nomComplet.ifBlank { throw IllegalArgumentException("fullname obligatoire") },
            gender = sexe.ifBlank { throw IllegalArgumentException("gender obligatoire") },
            phone = phone ?: throw IllegalArgumentException("phone obligatoire"),
            numberOfChildren = tailleMenage.takeIf { it > 0 } ?: throw IllegalArgumentException("numberofchildren obligatoire"),
            photo = image ?: throw IllegalArgumentException("photo obligatoire"),
            landArea = superficie,
            seed = buildSeedLabel() ?: throw IllegalArgumentException("seed obligatoire"),
            fertilizer = buildFertilizerLabel() ?: throw IllegalArgumentException("fertilizer obligatoire"),
            maizeQty = kgMais,
            riceQty = kgRiz,
            cassavaQty = kgManioc,
            soybeanQty = kgSoja,
            dapQty = kgDap,
            kclQty = kgKcl,
            ureeQty = kgUree,
            npk = kgNpk,
            suggestion = suggestion,
            warehouseId = if (siteId > 0) siteId else 40,
            usersId = 12,
            latitudeWrhs = latitude?.toDoubleOrNull() ?: -4.3256,
            longitudeWrhs = longitude?.toDoubleOrNull() ?: 15.3222,
            altitudeWrhs = altitude?.toDoubleOrNull() ?: 312.5,
            precisionWrhs = precision?.toDoubleOrNull() ?: 5.0
        )
    }

    suspend fun saveDistributionLocal(d: Distribution): Long {
        val stamped = d.copy(syncStatus = SyncStatus.PENDING, lastModified = now())
        val idLocal = dao.insertDistribution(stamped) // ⚡ insert renvoie Long
        Log.d("DistributionRepo", "💾 Distribution locale enregistrée (PENDING): idLocal=$idLocal")
        return idLocal
    }
    suspend fun createDistributionRemote(idLocal: Long): Boolean {
        val d = dao.getDistributionById(idLocal)
            ?: return false.also { Log.e("DistributionRepo", "❌ Distribution introuvable en Room pour idLocal=$idLocal") }

        Log.d("DistributionRepo", "💾 Distribution locale (avant envoi): $d")
        return try {
            val request = d.toRequest() // ⚡ Validation ici

            // 🔹 Log exhaustif pour comparer avec le JSON attendu
            Log.d("DistributionRepo", """
        📋 Mapping toRequest():
        {
            fullname=${request.fullname}
            gender=${request.gender}
            phone=${request.phone}
            numberofchildren=${request.numberOfChildren}
            photo=${request.photo}
            landarea=${request.landArea}
            seed=${request.seed}
            fertilizer=${request.fertilizer}
            maize_qty=${request.maizeQty}
            rice_qty=${request.riceQty}
            cassava_qty=${request.cassavaQty}
            soybean_qty=${request.soybeanQty}
            dap_qty=${request.dapQty}
            kcl_qty=${request.kclQty}
            uree_qty=${request.ureeQty}
            npk=${request.npk}
            suggestion=${request.suggestion}
            warehouse_id=${request.warehouseId}
            users_id=${request.usersId}
            latitude_wrhs=${request.latitudeWrhs}
            longitude_wrhs=${request.longitudeWrhs}
            altitude_wrhs=${request.altitudeWrhs}
            precision_wrhs=${request.precisionWrhs}
        }
        """.trimIndent())

            val jsonPayload = Gson().toJson(request)
            Log.d("DistributionRepo", "📤 JSON envoyé au serveur (CREATE): $jsonPayload")

            val response = api.createDistribution(request)

            // 🔹 Log complet de la réponse API
            Log.d("DistributionRepo", "📥 Réponse brute API: ${response.raw()}")
            Log.d("DistributionRepo", "📥 Body API: ${response.body()}")

            val body: DistributionResponse? = response.body()

            // 🔹 Log exhaustif de la réponse décodée
            Log.d("DistributionRepo", """
        📥 Réponse API décodée:
        success=${body?.success}
        message=${body?.message}
        beneficiary_id=${body?.beneficiary_id}
        distribution_id=${body?.distribution_id}
        """.trimIndent())

            val success = response.isSuccessful && body?.success == true

            if (success) {
                val remoteId = body.distribution_id?.toLongOrNull()
                dao.markDistributionSynced(d.idDistribution, SyncStatus.SYNCED, now(), remoteId)
                Log.i("DistributionRepo", "✅ Distribution créée sur API et marquée SYNCED (remoteId=$remoteId)")
            } else {
                dao.markDistributionFailed(d.idDistribution, SyncStatus.FAILED, now())
                Log.w("DistributionRepo", "⚠️ Échec création distribution: success=${body?.success}, message=${body?.message}")
            }
            success
        } catch (e: IllegalArgumentException) {
            Log.e("DistributionRepo", "❌ Champs obligatoires manquants: ${e.message}")
            dao.markDistributionFailed(d.idDistribution, SyncStatus.FAILED, now())
            false
        } catch (e: Exception) {
            Log.e("DistributionRepo", "❌ Exception createDistributionRemote", e)
            dao.markDistributionFailed(d.idDistribution, SyncStatus.FAILED, now())
            false
        }
    }
    // ------------------ PUSH (CREATE/UPDATE) ------------------
    suspend fun pushUnsyncedDistributions(): SyncResult {
        val pending = dao.getUnsyncedDistributions()
        Log.i("Synchronization", "🔎 Found ${pending.size} unsynced distributions")

        var success = 0
        var failed = 0

        for (d in pending) {
            Log.d("Synchronization", "➡️ Processing distribution localId=${d.idDistribution}, remoteId=${d.remoteId}")

            try {
                val request = d.toRequest()
                Log.d("Synchronization", "📤 Payload JSON: ${Gson().toJson(request)}")

                val ok = createDistributionRemote(idLocal = d.idDistribution)

                if (ok) {
                    success++
                    Log.i("Synchronization", "✅ Distribution synced successfully (localId=${d.idDistribution})")
                    dao.markDistributionSynced(d.idDistribution, SyncStatus.SYNCED, now(), d.remoteId)
                } else {
                    failed++
                    Log.w("Synchronization", "❌ Failed to sync distribution (localId=${d.idDistribution})")
                    dao.markDistributionFailed(d.idDistribution, SyncStatus.FAILED, now())
                }
            } catch (e: IllegalArgumentException) {
                failed++
                Log.e("Synchronization", "❌ Invalid distribution (localId=${d.idDistribution}): ${e.message}")
                dao.markDistributionFailed(d.idDistribution, SyncStatus.FAILED, now())
            } catch (e: Exception) {
                failed++
                Log.e("Synchronization", "❌ Unexpected error syncing distribution (localId=${d.idDistribution})", e)
                dao.markDistributionFailed(d.idDistribution, SyncStatus.FAILED, now())
            }
        }

        Log.i("Synchronization", "🚚 SyncToApi Distributions finished: pending=${pending.size}, success=$success, failed=$failed")

        return SyncResult(
            step = "Distributions (push)",
            pending = pending.size,
            success = success,
            failed = failed,
            errorMessage = if (failed > 0) "❌ $failed distributions non synchronisées" else null
        )
    }

    // ------------------ VILLAGES ------------------

    // Lecture locale
    fun getVillagesLocal(): Flow<List<VillageEntity>> {
        Log.d("VillageRepo", "📥 Fetching villages from Room")
        return dao.getAllVillages()
    }
// ------------------ SITES ------------------

    // Lecture locale (toujours utilisée par l’UI)
    fun getSitesLocal(): Flow<List<SiteEntity>> {
        Log.d("SiteRepo", "📥 Fetching sites from Room")
        return dao.getAllSites()
    }

    // ------------------ VILLAGES ------------------
    suspend fun syncVillagesFromApi(): SyncResult {
        Log.i("Synchronization", "🌐 Starting villages sync from API")
        return try {
            val response = api.getVillages()
            if (response.isSuccessful) {
                val body = response.body()
                val villages = body?.data ?: emptyList<ApiVillage>()
                Log.d("Synchronization", "📦 Received ${villages.size} villages from API")

                val entities = villages.map {
                    VillageEntity(
                        id_village = it.id_village,      // ⚡ corriger en camelCase
                        nom_village = it.name_village    // ⚡ corriger en camelCase
                    )
                }
                dao.insertAllVillages(entities)
                Log.i("Synchronization", "✅ Villages saved to Room: ${entities.size}")

                SyncResult(step = "Villages", success = entities.size,
                    errorMessage = if (entities.isEmpty()) "⚠️ Aucun village reçu" else null)
            } else {
                Log.w("Synchronization", "⚠️ API error while fetching villages: code=${response.code()}")
                SyncResult(step = "Villages", errorMessage = "⚠️ API error: code=${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("Synchronization", "❌ Exception during villages sync", e)
            SyncResult(step = "Villages", errorMessage = "❌ Exception: ${e.message}")
        }
    }

    // ------------------ SITES ------------------
    suspend fun syncSitesFromApi(): SyncResult {
        Log.i("Synchronization", "🌐 Starting sites sync from API")
        return try {
            val response = api.getSites()
            if (response.isSuccessful) {
                val body = response.body()
                val sites = body?.data ?: emptyList<Site>()
                Log.d("Synchronization", "📦 Received ${sites.size} sites from API")

                val entities = sites.map {
                    SiteEntity(
                        warehouseId = it.warehouseId,      // ⚡ corriger en camelCase
                        nameWarehouse = it.nameWarehouse   // ⚡ corriger en camelCase
                    )
                }
                dao.insertAllSites(entities)
                Log.i("Synchronization", "✅ Sites saved to Room: ${entities.size}")

                SyncResult(step = "Sites", success = entities.size,
                    errorMessage = if (entities.isEmpty()) "⚠️ Aucun site reçu" else null)
            } else {
                Log.w("Synchronization", "⚠️ API error while fetching sites: code=${response.code()}")
                SyncResult(step = "Sites", errorMessage = "⚠️ API error: code=${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("Synchronization", "❌ Exception during sites sync", e)
            SyncResult(step = "Sites", errorMessage = "❌ Exception: ${e.message}")
        }
    }

    // 🔹 Distributions
    fun getAllDistributionsFlow(): Flow<List<Distribution>> = dao.getAllDistributionsFlow()
}