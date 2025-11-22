package id.fs.dia_padcv.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "villages")
data class Village(
    @PrimaryKey(autoGenerate = true)
    val id_village: Int? = null,

    @SerializedName("name_village")
    val nom_village: String?
)
