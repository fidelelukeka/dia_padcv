package id.fs.dia_padcv.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "distributions")
data class DistributionOld(
    @PrimaryKey(autoGenerate = true) val idDistribution: Long = 0,
    val dateSortie: String,
    val quantiteSortie: Int,
    val statutDistribution: String, // Retardé, En attente, Distribué
    val datePrevueDistribution: String,
    val moyenTransport: String,
    val isSynced: Boolean = false,

    // ✅ clé étrangère vers le bénéficiaire concerné
    val beneficiaryIdF: Long? = null
)