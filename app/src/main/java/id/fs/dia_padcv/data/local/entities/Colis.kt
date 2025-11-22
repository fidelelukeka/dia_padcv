package id.fs.dia_padcv.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "colis")
data class Colis(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @SerializedName("QR_code")
    val qrCode: String? = null,

    @SerializedName("produit_type")
    val produitType: String? = null,

    @SerializedName("produit_contenu")
    val produitContenu: String? = null,

    val quantity: Int? = null,
    val unit: String? = null, // ex: "kg"
    val quality: String? = null, // "bon" ou "mauvais"

    @ColumnInfo("warehouse_id_f")
    val warehouseIdF: Int,

    val status: String? = null, // "stockage" ou "déployé"

    val isSynced: Boolean = false
)
