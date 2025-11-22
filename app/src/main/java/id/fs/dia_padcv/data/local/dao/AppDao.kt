package id.fs.dia_padcv.data.local.dao

import androidx.room.*
import id.fs.dia_padcv.data.local.entities.*
import id.fs.dia_padcv.data.local.relations.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // ------------------ DISTRIBUTIONS ------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistribution(distribution: Distribution)

    @Query("SELECT * FROM distributions ORDER BY idDistribution DESC")
    fun getAllDistributionsFlow(): Flow<List<Distribution>>

    @Query("SELECT * FROM distributions")
    suspend fun getAllDistributions(): List<Distribution>

    @Delete
    suspend fun deleteDistribution(distribution: Distribution)

    // ------------------ ENTREPOTS ------------------
    @Query("SELECT * FROM entrepots ORDER BY id DESC")
    fun getAllEntrepots(): Flow<List<Entrepot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntrepot(entrepot: Entrepot)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEntrepots(entrepots: List<Entrepot>)

    @Query("SELECT * FROM entrepots WHERE nameWarehouse != ''")
    suspend fun getUnsyncedEntrepots(): List<Entrepot> // (tu peux ajouter un champ isSynced si besoin)

    @Query("DELETE FROM entrepots WHERE id IN (SELECT id FROM entrepots LIMIT 0)")
    suspend fun deleteSyncedEntrepots() // placeholder si tu veux gérer la sync plus tard

    @Delete
    suspend fun deleteEntrepot(entrepot: Entrepot)


    // ------------------ COLIS ------------------
    @Query("SELECT * FROM colis ORDER BY id DESC")
    fun getAllColis(): Flow<List<Colis>>

    @Query("SELECT * FROM colis WHERE status = 'stockage' OR status IS NULL")
    suspend fun getUnsyncedColis(): List<Colis>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColis(colis: Colis)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllColis(colisList: List<Colis>)

    @Query("UPDATE colis SET status = :status WHERE id = :id")
    suspend fun updateColisStatus(id: Long, status: String)

    @Query("DELETE FROM colis WHERE status = 'déployé'")
    suspend fun deleteSyncedColis()

    @Delete
    suspend fun deleteColis(colis: Colis)


    // ------------------ VILLAGES ------------------
    @Query("SELECT * FROM villages ORDER BY id_village ASC")
    fun getAllVillages(): Flow<List<Village>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVillage(village: Village)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVillages(villages: List<Village>)

    @Delete
    suspend fun deleteVillage(village: Village)


    // ------------------ BENEFICIARIES ------------------
    @Query("SELECT * FROM beneficiaries ORDER BY id_ben DESC")
    fun getAllBeneficiaries(): Flow<List<Beneficiary>>

    @Query("SELECT * FROM beneficiaries WHERE is_synced = 0")
    suspend fun getUnsyncedBeneficiaries(): List<Beneficiary>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeneficiary(beneficiary: Beneficiary)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBeneficiaries(beneficiaries: List<Beneficiary>)

    @Query("UPDATE beneficiaries SET is_synced = :isSynced WHERE id_ben = :id")
    suspend fun updateBeneficiarySyncStatus(id: Long, isSynced: Boolean)

    @Query("DELETE FROM beneficiaries WHERE is_synced = 1")
    suspend fun deleteSyncedBeneficiaries()

    @Delete
    suspend fun deleteBeneficiary(beneficiary: Beneficiary)


    // ------------------ USERS ------------------
    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE status != 'synced' OR status IS NULL")
    suspend fun getUnsyncedUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUsers(users: List<User>)

    @Query("UPDATE users SET status = :status WHERE id = :id")
    suspend fun updateUserSyncStatus(id: Int, status: String)

    @Query("DELETE FROM users WHERE status = 'synced'")
    suspend fun deleteSyncedUsers()

    @Delete
    suspend fun deleteUser(user: User)


    // ------------------ RELATIONS ------------------
    @Transaction
    @Query("SELECT * FROM villages")
    fun getVillagesWithBeneficiaries(): Flow<List<VillageWithBeneficiaries>>

    @Transaction
    @Query("SELECT * FROM entrepots")
    fun getEntrepotsWithColis(): Flow<List<EntrepotWithColis>>

    @Transaction
    @Query("SELECT * FROM beneficiaries")
    fun getBeneficiariesWithDistributions(): Flow<List<BeneficiaryWithDistributions>>

    @Transaction
    @Query("SELECT * FROM villages")
    fun getVillagesWithBeneficiariesAndDistributions(): Flow<List<VillageWithBeneficiariesAndDistributions>>

    @Query("SELECT * FROM sites ORDER BY warehouse_id ASC")
    fun getAllSites(): Flow<List<SiteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSite(site: SiteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSites(sites: List<SiteEntity>)

    @Delete
    suspend fun deleteSite(site: SiteEntity)

    @Query("DELETE FROM sites")
    suspend fun clearSites()
}
