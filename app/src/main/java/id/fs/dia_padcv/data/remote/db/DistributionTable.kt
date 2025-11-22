package id.fs.dia_padcv.data.remote.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Distributions : Table("distributions") {
    val idDis = integer("id_dis").autoIncrement()
    val maizeQty = integer("maize_qty")
    val riceQty = integer("rice_qty")
    val cassavaQty = integer("cassava_qty")
    val soybeanQty = integer("soybean_qty")
    val dapQty = integer("dap_qty")
    val kclQty = integer("kcl_qty")
    val ureeQty = integer("uree_qty")
    val npk = integer("npk")
    val suggestion = varchar("suggestion", 200).nullable()
    val warehouseId = integer("warehouse_id")
    val beneficiarieId = integer("beneficiarie_id")
    val usersId = integer("users_id")
    val distDate = datetime("dist_date")

    override val primaryKey = PrimaryKey(idDis)
}

fun insertDistribution(
    maize: Int,
    rice: Int,
    cassava: Int,
    soybean: Int,
    dap: Int,
    kcl: Int,
    uree: Int,
    npkVal: Int,
    suggestionVal: String?,
    warehouse: Int,
    beneficiary: Int,
    user: Int
) {
    transaction {
        Distributions.insert {
            it[maizeQty] = maize
            it[riceQty] = rice
            it[cassavaQty] = cassava
            it[soybeanQty] = soybean
            it[dapQty] = dap
            it[kclQty] = kcl
            it[ureeQty] = uree
            it[npk] = npkVal
            it[suggestion] = suggestionVal
            it[warehouseId] = warehouse
            it[beneficiarieId] = beneficiary
            it[usersId] = user
            it[distDate] = LocalDateTime.now()
        }
    }
}