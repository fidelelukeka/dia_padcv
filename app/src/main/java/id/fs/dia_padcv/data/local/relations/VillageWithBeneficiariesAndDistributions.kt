package id.fs.dia_padcv.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import id.fs.dia_padcv.data.local.entities.Beneficiary
import id.fs.dia_padcv.data.local.entities.Village

data class VillageWithBeneficiariesAndDistributions(
    @Embedded val village: Village,
    @Relation(
        parentColumn = "id_village",
        entityColumn = "id_village_f",
        entity = Beneficiary::class
    )
    val beneficiaries: List<BeneficiaryWithDistributions>
)
