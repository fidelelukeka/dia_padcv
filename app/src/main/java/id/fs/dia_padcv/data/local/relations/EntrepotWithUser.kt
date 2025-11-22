package id.fs.dia_padcv.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import id.fs.dia_padcv.data.local.entities.Entrepot
import id.fs.dia_padcv.data.local.entities.User

data class EntrepotWithUser(
    @Embedded val entrepot: Entrepot,
    @Relation(
        parentColumn = "userIdF",
        entityColumn = "id"
    )
    val user: User?
)
