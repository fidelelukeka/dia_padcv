package id.fs.dia_padcv.data.local.dao

import androidx.room.*
import id.fs.dia_padcv.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistribution(distribution: Distribution): Long

    @Query("SELECT * FROM distributions WHERE idDistribution = :id LIMIT 1")
    suspend fun getDistributionById(id: Long): Distribution?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDistributions(list: List<Distribution>) // ⚡ c’est cette signature qu’on doit confirmer

    @Query("SELECT * FROM distributions ORDER BY idDistribution DESC")
    fun getAllDistributionsFlow(): Flow<List<Distribution>>

    @Query("SELECT * FROM distributions WHERE sync_status != :synced")
    suspend fun getUnsyncedDistributions(synced: SyncStatus = SyncStatus.SYNCED): List<Distribution>

    @Query("UPDATE distributions SET sync_status = :status, last_modified = :lastModified WHERE idDistribution = :id")
    suspend fun markDistributionFailed(
        id: Long,
        status: SyncStatus = SyncStatus.FAILED,
        lastModified: Long = System.currentTimeMillis()
    )

    @Query("UPDATE distributions SET sync_status = :status, last_modified = :lastModified, remote_id = :remoteId WHERE idDistribution = :id")
    suspend fun markDistributionSynced(
        id: Long,
        status: SyncStatus = SyncStatus.SYNCED,
        lastModified: Long = System.currentTimeMillis(),
        remoteId: Long?
    )

    @Query("UPDATE distributions SET sync_status = :status, last_modified = :lastModified WHERE idDistribution = :id")
    suspend fun markDistributionPending(
        id: Long,
        status: SyncStatus = SyncStatus.PENDING,
        lastModified: Long = System.currentTimeMillis()
    )

    @Delete
    suspend fun deleteDistribution(distribution: Distribution)

    // Référentiels (read-only côté app)
    @Query("SELECT * FROM villages ORDER BY id_village ASC")
    fun getAllVillages(): Flow<List<Village>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVillages(villages: List<Village>)

    @Query("SELECT * FROM entrepots ORDER BY id ASC")
    fun getAllEntrepots(): Flow<List<Entrepot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEntrepots(entrepots: List<Entrepot>)

    @Query("SELECT * FROM sites ORDER BY warehouse_id ASC")
    fun getAllSites(): Flow<List<SiteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSites(sites: List<SiteEntity>)

    @Query("DELETE FROM villages") suspend fun clearVillages()
    @Query("DELETE FROM entrepots") suspend fun clearEntrepots()
    @Query("DELETE FROM sites") suspend fun clearSites()

    // Supprime toute modification/suppression sur villages/entrepots/sites (read-only en local)
    // -> Retire @Delete/@Update pour ces tables dans le DAO



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
}
