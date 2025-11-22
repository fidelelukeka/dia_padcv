package id.fs.dia_padcv.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import id.fs.dia_padcv.data.local.entities.Colis
import id.fs.dia_padcv.data.local.entities.Distribution

data class DistributionWithColis(
    @Embedded val distribution: Distribution,
    @Relation(
        parentColumn = "colisIdF",
        entityColumn = "id"
    )
    val colis: Colis?
)
