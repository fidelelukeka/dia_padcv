package id.fs.dia_padcv.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import id.fs.dia_padcv.data.local.dao.AppDao
import id.fs.dia_padcv.data.local.relations.*
import kotlinx.coroutines.flow.Flow

// Entités locales Room
import id.fs.dia_padcv.data.local.entities.Beneficiary
import id.fs.dia_padcv.data.local.entities.Colis
import id.fs.dia_padcv.data.local.entities.Distribution
import id.fs.dia_padcv.data.local.entities.Entrepot
import id.fs.dia_padcv.data.local.entities.SiteEntity
import id.fs.dia_padcv.data.local.entities.User as LocalUser
import id.fs.dia_padcv.data.local.entities.Village as LocalVillage

// Classes API
import id.fs.dia_padcv.data.remote.api.User as ApiUser
import id.fs.dia_padcv.data.remote.api.Village as ApiVillage
import id.fs.dia_padcv.data.remote.api.ApiResponse
import id.fs.dia_padcv.data.remote.api.BeneficiaryRequest
import id.fs.dia_padcv.data.remote.api.ColisRequest
import id.fs.dia_padcv.data.remote.api.ColisResponse
import id.fs.dia_padcv.data.remote.api.DistributionRequest
import id.fs.dia_padcv.data.remote.api.WarehouseRequest
import id.fs.dia_padcv.data.remote.api.WarehouseResponse
import id.fs.dia_padcv.data.remote.api.RetrofitClient
import id.fs.dia_padcv.data.remote.api.Site


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

    suspend fun insertEntrepot(e: Entrepot) = dao.insertEntrepot(e)
    suspend fun insertAllEntrepots(list: List<Entrepot>) = dao.insertAllEntrepots(list)
    suspend fun deleteEntrepot(e: Entrepot) = dao.deleteEntrepot(e)

    suspend fun getUnsyncedEntrepots(): List<Entrepot> = dao.getUnsyncedEntrepots()
    suspend fun deleteSyncedEntrepots() = dao.deleteSyncedEntrepots()


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
    val villages: Flow<List<LocalVillage>> = dao.getAllVillages()

    suspend fun insertVillage(v: LocalVillage) = dao.insertVillage(v)
    suspend fun insertAllVillages(list: List<LocalVillage>) = dao.insertAllVillages(list)
    suspend fun deleteVillage(v: LocalVillage) = dao.deleteVillage(v)


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
    // ------------------------- USERS -------------------------
    // =========================================================
    val users: Flow<List<LocalUser>> = dao.getAllUsers()

    suspend fun insertUser(u: LocalUser) = dao.insertUser(u)
    suspend fun insertAllUsers(list: List<LocalUser>) = dao.insertAllUsers(list)
    suspend fun deleteUser(u: LocalUser) = dao.deleteUser(u)

    suspend fun getUnsyncedUsers(): List<LocalUser> = dao.getUnsyncedUsers()
    suspend fun updateUserSyncStatus(id: Int, status: String) = dao.updateUserSyncStatus(id, status)
    suspend fun deleteSyncedUsers() = dao.deleteSyncedUsers()


    // =========================================================
    // ----------------------- RELATIONS -----------------------
    // =========================================================
    fun getVillagesWithBeneficiaries(): Flow<List<VillageWithBeneficiaries>> =
        dao.getVillagesWithBeneficiaries()

    fun getEntrepotsWithColis(): Flow<List<EntrepotWithColis>> =
        dao.getEntrepotsWithColis()

    fun getBeneficiariesWithDistributions(): Flow<List<BeneficiaryWithDistributions>> =
        dao.getBeneficiariesWithDistributions()

    fun getVillagesWithBeneficiariesAndDistributions(): Flow<List<VillageWithBeneficiariesAndDistributions>> =
        dao.getVillagesWithBeneficiariesAndDistributions()


    // =========================================================
    // ------------------------ API PART -----------------------
    // =========================================================
    private val api = RetrofitClient.api


    // 🔹 Login utilisateur via API
    suspend fun login(username: String, password: String): Boolean {
        return try {
            val response = api.login(username, password)
            if (response.isSuccessful && response.body()?.status.equals("success", ignoreCase = true)) {
                Log.d("Repository", "✅ Login réussi pour $username")
                true
            } else {
                Log.w("Repository", "⚠️ Login échoué : ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("Repository", "❌ Exception login", e)
            false
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

    // 🔹 Récupération des villages depuis l’API
    suspend fun fetchVillages(): List<ApiVillage> {
        return try {
            val response = api.getVillages()
            if (response.isSuccessful) {
                val list = response.body()?.data?.map {
                    ApiVillage(
                        id_village = it.id_village,
                        name_village = it.name_village,
                    )
                } ?: emptyList()
                Log.i("Repository", "✅ Villages téléchargés : ${list.size}")
                list
            } else {
                Log.w("Repository", "⚠️ Erreur HTTP : ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("Repository", "❌ Exception fetchVillages", e)
            emptyList()
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

    // 🔹 Insertion en local
    suspend fun insertSitesLocal(list: List<SiteEntity>) = dao.insertAllSites(list)

    // 🔹 Récupération depuis API et sauvegarde en Room
    suspend fun fetchSitesFromApi(): List<SiteEntity> {
        return try {
            val response = api.getSites()
            if (response.isSuccessful) {
                val sites = response.body()?.data ?: emptyList()
                val entities = sites.map { SiteEntity(it.warehouseId, it.nameWarehouse) }
                dao.insertAllSites(entities)
                Log.i("SiteRepository", "✅ Sites téléchargés et enregistrés : ${entities.size}")
                entities
            } else {
                Log.w("SiteRepository", "⚠️ Erreur HTTP sites: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("SiteRepository", "❌ Exception fetchSitesFromApi", e)
            emptyList()
        }
    }

    suspend fun saveDistribution(distribution: Distribution) {
        dao.insertDistribution(distribution)
    }

    val distributions: Flow<List<Distribution>> = dao.getAllDistributionsFlow()

    suspend fun insertDistribution(d: Distribution) = dao.insertDistribution(d)
    suspend fun deleteDistribution(d: Distribution) = dao.deleteDistribution(d)
    suspend fun getDistributions(): List<Distribution> = dao.getAllDistributions()

    // 🔹 Création Distribution via API
    suspend fun createDistribution(context: Context, request: DistributionRequest): Boolean {
        return try {
            val response = api.createDistribution(request)
            val success = response.isSuccessful

            val message = if (success) {
                "✅ Distribution créée sur API"
            } else {
                "⚠️ Erreur création distribution: ${response.code()}"
            }

            Log.i("Repository", message)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            success
        } catch (e: Exception) {
            Log.e("Repository", "❌ Erreur API createDistribution", e)
            Toast.makeText(context, "❌ Erreur API createDistribution", Toast.LENGTH_SHORT).show()
            false
        }
    }

    suspend fun fetchSites(): List<SiteEntity> {
        return try {
            val response = api.getSites()
            if (response.isSuccessful) {
                val sites = response.body()?.data ?: emptyList()
                val entities = sites.map { SiteEntity(it.warehouseId, it.nameWarehouse) }

                // ⚡ Enregistrer en Room
                dao.clearSites()
                dao.insertAllSites(entities)

                Log.i("Repository", "✅ Sites téléchargés et enregistrés : ${entities.size}")
                entities
            } else {
                Log.w("Repository", "⚠️ Erreur HTTP sites: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("Repository", "❌ Exception fetchSites", e)
            emptyList()
        }
    }

    fun getSitesLocal(): Flow<List<SiteEntity>> = dao.getAllSites()

//    suspend fun fetchDistributions(): List<Distribution> {
//        return try {
//            val response = api.getDistributions()
//            if (response.isSuccessful) {
//                val list = response.body()?.data?.map { dto ->
//                    Distribution(
//                        siteId = dto.warehouseId,
//                        nomComplet = dto.fullname,
//                        sexe = dto.gender,
//                        phone = dto.phone,
//                        image = dto.photo,
//                        tailleMenage = dto.numberOfChildren ?: 0,
//                        superficie = dto.landArea ?: "0",
//                        latitude = dto.latitudeWrhs?.toString(),
//                        longitude = dto.longitudeWrhs?.toString(),
//                        altitude = dto.altitudeWrhs?.toString(),
//                        precision = dto.precisionWrhs?.toString(),
//                        kgRiz = dto.riceQty ?: 0,
//                        kgMais = dto.maizeQty ?: 0,
//                        kgManioc = dto.cassavaQty ?: 0,
//                        kgSoja = dto.soybeanQty ?: 0,
//                        kgDap = dto.dapQty ?: 0,
//                        kgKcl = dto.kclQty ?: 0,
//                        kgUree = dto.ureeQty ?: 0,
//                        kgNpk = dto.npk ?: 0,
//                        suggestion = dto.suggestion ?: "",
//                        beneficiarieId = dto.beneficiarieId,
//                        usersId = dto.usersId
//                    )
//                } ?: emptyList()
//
//                dao.insertAllDistributions(list)
//                Log.i("Repository", "✅ Distributions téléchargées et insérées : ${list.size}")
//                list
//            } else {
//                Log.w("Repository", "⚠️ Erreur HTTP distributions: ${response.code()}")
//                emptyList()
//            }
//        } catch (e: Exception) {
//            Log.e("Repository", "❌ Exception fetchDistributions", e)
//            emptyList()
//        }
//    }


}
