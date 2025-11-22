package id.fs.dia_padcv.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entrepots")
data class Entrepot(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nameWarehouse: String,
    val nameResponsable: String,
    val type: String, // "Général" ou "Secondaire"
    val geopoint: String?, // optionnel, format "lat,long"
    val latitude: Double?,
    val longitude: Double?,
    val altitude: Double?,
    val precision: Double?,
    val photoPath: String?, // chemin de la photo
    val isSynced: Boolean = false,
)