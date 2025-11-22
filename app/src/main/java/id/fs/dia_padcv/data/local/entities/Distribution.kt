package id.fs.dia_padcv.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "distributions")
data class Distribution(
    @PrimaryKey(autoGenerate = true) val idDistribution: Long = 0,

    // 🔗 Référence au site/entrepôt
    @ColumnInfo(name = "site_id") val siteId: Int,

    // 📍 Localisation
    @ColumnInfo(name = "latitude") val latitude: String? = null,
    @ColumnInfo(name = "longitude") val longitude: String? = null,
    @ColumnInfo(name = "altitude") val altitude: String? = null,
    @ColumnInfo(name = "precision") val precision: String? = null,

    // 👤 Infos bénéficiaire
    @ColumnInfo(name = "fullname") val nomComplet: String,
    @ColumnInfo(name = "gender") val sexe: String,
    @ColumnInfo(name = "phone") val phone: String? = null,
    @ColumnInfo(name = "photo") val image: String?,

    // 👨‍👩‍👧‍👦 Taille ménage
    @ColumnInfo(name = "number_of_children") val tailleMenage: Int = 0,

    // 🌾 Semences / Engrais
    @ColumnInfo(name = "has_riz") val hasRiz: String = "NON",
    @ColumnInfo(name = "kg_riz") val kgRiz: Int = 0,

    @ColumnInfo(name = "has_mais") val hasMais: String = "NON",
    @ColumnInfo(name = "kg_mais") val kgMais: Int = 0,

    @ColumnInfo(name = "has_manioc") val hasManioc: String = "NON",
    @ColumnInfo(name = "kg_manioc") val kgManioc: Int = 0,

    @ColumnInfo(name = "has_soja") val hasSoja: String = "NON",
    @ColumnInfo(name = "kg_soja") val kgSoja: Int = 0,

    @ColumnInfo(name = "has_dap") val hasDap: String = "NON",
    @ColumnInfo(name = "kg_dap") val kgDap: Int = 0,

    @ColumnInfo(name = "has_kcl") val hasKcl: String = "NON",
    @ColumnInfo(name = "kg_kcl") val kgKcl: Int = 0,

    @ColumnInfo(name = "has_uree") val hasUree: String = "NON",
    @ColumnInfo(name = "kg_uree") val kgUree: Int = 0,

    @ColumnInfo(name = "has_npk") val hasNpk: String = "NON",
    @ColumnInfo(name = "kg_npk") val kgNpk: Int = 0,

    // 🌍 Superficie cultivée
    @ColumnInfo(name = "landarea") val superficie: String = "0", // ⚡ String pour matcher API

    // 💬 Suggestion
    @ColumnInfo(name = "suggestion") val suggestion: String,

    // 🔗 Liens backend
    @ColumnInfo(name = "beneficiarie_id") val beneficiarieId: Int? = null,
    @ColumnInfo(name = "users_id") val usersId: Int = 0
)
