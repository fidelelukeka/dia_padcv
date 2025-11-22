package id.fs.dia_padcv.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import id.fs.dia_padcv.data.local.entities.Beneficiary
import id.fs.dia_padcv.data.local.entities.Village

data class VillageWithBeneficiaries(
    @Embedded val village: Village,
    @Relation(
        parentColumn = "id_village",
        entityColumn = "id_village_f"
    )
    val beneficiaries: List<Beneficiary>
)
