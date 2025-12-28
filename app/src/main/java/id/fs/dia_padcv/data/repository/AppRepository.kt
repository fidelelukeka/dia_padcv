package id.fs.dia_padcv.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.protobuf.LazyStringArrayList.emptyList
import id.fs.dia_padcv.data.local.dao.AppDao
import id.fs.dia_padcv.data.local.relations.*
import kotlinx.coroutines.flow.Flow

// Entités locales Room
import id.fs.dia_padcv.data.local.entities.Beneficiary
import id.fs.dia_padcv.data.local.entities.Colis
import id.fs.dia_padcv.data.local.entities.Distribution
import id.fs.dia_padcv.data.local.entities.Entrepot
import id.fs.dia_padcv.data.local.entities.SiteEntity
import id.fs.dia_padcv.data.local.entities.SyncStatus
import id.fs.dia_padcv.data.local.entities.User as LocalUser
import id.fs.dia_padcv.data.local.entities.Village as VillageEntity

// Classes API
import id.fs.dia_padcv.data.remote.api.Village as ApiVillage
import id.fs.dia_padcv.data.remote.api.ApiResponse
import id.fs.dia_padcv.data.remote.api.ApiService
import id.fs.dia_padcv.data.remote.api.BeneficiaryRequest
import id.fs.dia_padcv.data.remote.api.ColisRequest
import id.fs.dia_padcv.data.remote.api.ColisResponse
import id.fs.dia_padcv.data.remote.api.DistributionDeleteRequest
import id.fs.dia_padcv.data.remote.api.DistributionListResponse
import id.fs.dia_padcv.data.remote.api.DistributionRequest
import id.fs.dia_padcv.data.remote.api.WarehouseRequest
import id.fs.dia_padcv.data.remote.api.WarehouseResponse
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

            return if (creds.first == username && creds.second == passwordHash) {
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


    // ------------------ MAPPINGS ------------------
    fun Distribution.toRequest(): DistributionRequest = DistributionRequest(
        fullname = nomComplet,
        gender = sexe,
        phone = phone,
        numberOfChildren = tailleMenage,
        photo = image,
        landArea = superficie,
        seed = "Mais", // ⚡ exemple
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
        usersId = usersId,
        latitudeWrhs = latitude?.toDoubleOrNull(),
        longitudeWrhs = longitude?.toDoubleOrNull(),
        altitudeWrhs = altitude?.toDoubleOrNull(),
        precisionWrhs = precision?.toDoubleOrNull()
    )

    fun Distribution.toApiRequestForCreate(): DistributionRequest = toRequest()
    fun Distribution.toApiRequestForUpdate(): DistributionRequest = toRequest()
    fun Distribution.toApiDeleteRequest(): DistributionDeleteRequest =
        DistributionDeleteRequest(idDistribution = remoteId ?: idDistribution)

    // ------------------ LOCAL SAVE ------------------
    suspend fun saveDistributionLocal(d: Distribution) {
        val stamped = d.copy(syncStatus = SyncStatus.PENDING, lastModified = now())
        dao.insertDistribution(stamped)
        Log.d("DistributionRepo", "💾 Distribution locale enregistrée (PENDING): idLocal=${stamped.idDistribution}")
    }

    // ------------------ CREATE ------------------
    suspend fun createDistributionRemote(d: Distribution): Boolean {
        return try {
            val response = api.createDistribution(d.toApiRequestForCreate())
            val success = response.isSuccessful && response.body()?.status.equals("success", true)

            if (success) {
                val remoteId = (response.body()?.data as? Long)
                dao.markDistributionSynced(d.idDistribution, SyncStatus.SYNCED, now(), remoteId)
                Log.i("DistributionRepo", "✅ Distribution créée sur API et marquée SYNCED")
            } else {
                dao.markDistributionFailed(d.idDistribution, SyncStatus.FAILED, now())
                Log.w("DistributionRepo", "⚠️ Erreur création distribution: ${response.code()}")
            }
            success
        } catch (e: Exception) {
            dao.markDistributionFailed(d.idDistribution, SyncStatus.FAILED, now())
            Log.e("DistributionRepo", "❌ Exception createDistributionRemote", e)
            false
        }
    }

    // ------------------ UPDATE ------------------
    suspend fun updateDistributionRemote(d: Distribution): Boolean {
        return try {
            val response = api.updateDistribution(d.toApiRequestForUpdate())
            val success = response.isSuccessful && response.body()?.status.equals("success", true)

            if (success) {
                dao.markDistributionSynced(d.idDistribution, SyncStatus.SYNCED, now(), d.remoteId)
                Log.i("DistributionRepo", "✅ Distribution mise à jour sur API et marquée SYNCED")
            } else {
                dao.markDistributionFailed(d.idDistribution, SyncStatus.FAILED, now())
                Log.w("DistributionRepo", "⚠️ Erreur update distribution: ${response.code()}")
            }
            success
        } catch (e: Exception) {
            dao.markDistributionFailed(d.idDistribution, SyncStatus.FAILED, now())
            Log.e("DistributionRepo", "❌ Exception updateDistributionRemote", e)
            false
        }
    }

    // ------------------ DELETE ------------------
    suspend fun deleteDistributionRemote(d: Distribution): Boolean {
        return try {
            val response = api.deleteDistribution(d.toApiDeleteRequest())
            val success = response.isSuccessful && response.body()?.status.equals("success", true)

            if (success) {
                dao.deleteDistribution(d)
                Log.i("DistributionRepo", "✅ Distribution supprimée sur API et en local")
            } else {
                Log.w("DistributionRepo", "⚠️ Erreur delete distribution: ${response.code()}")
            }
            success
        } catch (e: Exception) {
            Log.e("DistributionRepo", "❌ Exception deleteDistributionRemote", e)
            false
        }
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

    // ------------------ PUSH UNSYNCED ------------------
    suspend fun pushUnsyncedDistributions(): SyncResult {
        val pending = dao.getUnsyncedDistributions()
        Log.i("Synchronization", "🔎 Found ${pending.size} unsynced distributions")

        var success = 0
        var failed = 0

        for (d in pending) {
            Log.d("Synchronization", "➡️ Processing distribution localId=${d.idDistribution}, remoteId=${d.remoteId}")
            val ok = if (d.remoteId == null) {
                Log.d("Synchronization", "🆕 Creating new distribution on API for localId=${d.idDistribution}")
                createDistributionRemote(d)
            } else {
                Log.d("Synchronization", "♻️ Updating distribution remoteId=${d.remoteId}")
                updateDistributionRemote(d)
            }
            if (ok) {
                success++
                Log.i("Synchronization", "✅ Distribution synced successfully (localId=${d.idDistribution})")
            } else {
                failed++
                Log.w("Synchronization", "❌ Failed to sync distribution (localId=${d.idDistribution})")
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

    // ------------------ FETCH DISTRIBUTIONS ------------------
    suspend fun fetchDistributionsFromApi(): SyncResult {
        Log.i("Synchronization", "🌐 Starting fetch distributions from API")
        return try {
            val response = api.getDistributions()
            if (response.isSuccessful) {
                val body = response.body() as? DistributionListResponse
                val distributions = body?.data ?: emptyList()

                Log.d("Synchronization", "📦 Received ${distributions.size} distributions from API")

                dao.insertAllDistributions(distributions as List<Distribution>)
                Log.i("Synchronization", "✅ Distributions saved to Room: ${distributions.size}")

                SyncResult(
                    step = "Distributions (fetch)",
                    success = distributions.size,
                    errorMessage = if (distributions.isEmpty()) "⚠️ Aucune distribution reçue" else null
                )
            } else {
                Log.w("Synchronization", "⚠️ API error while fetching distributions: code=${response.code()}")
                SyncResult(step = "Distributions (fetch)", errorMessage = "⚠️ API error: code=${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("Synchronization", "❌ Exception during distributions fetch", e)
            SyncResult(step = "Distributions (fetch)", errorMessage = "❌ Exception: ${e.message}")
        }
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