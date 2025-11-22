package id.fs.dia_padcv.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import id.fs.dia_padcv.data.local.entities.Beneficiary
import id.fs.dia_padcv.data.local.entities.Distribution

data class BeneficiaryWithDistributions(
    @Embedded val beneficiary: Beneficiary,
    @Relation(
        parentColumn = "id_ben",
        entityColumn = "idDistribution" // 🔸 à ajouter dans Distribution
    )
    val distributions: List<Distribution>
)
