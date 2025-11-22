package id.fs.dia_padcv.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sites")
data class SiteEntity(
    @PrimaryKey @ColumnInfo(name = "warehouse_id") val warehouseId: Int,
    @ColumnInfo(name = "name_warehouse") val nameWarehouse: String
)