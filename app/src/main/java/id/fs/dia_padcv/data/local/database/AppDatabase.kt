package id.fs.dia_padcv.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import id.fs.dia_padcv.data.local.entities.Colis
import id.fs.dia_padcv.data.local.entities.Distribution
import id.fs.dia_padcv.data.local.entities.Entrepot
import id.fs.dia_padcv.data.local.entities.Village
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.fs.dia_padcv.data.local.dao.AppDao
import id.fs.dia_padcv.data.local.entities.*

@Database(
    entities = [
        Distribution::class,
        Entrepot::class,
        Village::class,
        Colis::class,
        Beneficiary::class,
        User::class,
        SiteEntity::class
    ],
    version = 15,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration(true) // ⚡ sécurité si aucune migration trouvée
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun resetDatabase(context: Context) {
            synchronized(this) {
                context.deleteDatabase("app_database")
                INSTANCE = null
            }
        }
    }
}