package id.fs.dia_padcv.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import id.fs.dia_padcv.data.local.entities.Colis
import id.fs.dia_padcv.data.local.entities.Entrepot

data class EntrepotWithColis(
    @Embedded val entrepot: Entrepot,
    @Relation(
        parentColumn = "id",         // id de l'entrepot
        entityColumn = "warehouse_id_f"  // doit matcher le @ColumnInfo de Colis
    )
    val colisList: List<Colis>
)