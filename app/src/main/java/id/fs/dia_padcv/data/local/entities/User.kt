package id.fs.dia_padcv.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "username")
    val username: String? = null,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    val role: String? = null,
    val active: Int = 0,
    val created_at: String? = null,
    val updated_at: String? = null,
    val status: String? = null
)
