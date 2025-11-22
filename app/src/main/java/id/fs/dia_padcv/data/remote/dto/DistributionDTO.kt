package id.fs.dia_padcv.data.remote.dto

@kotlinx.serialization.Serializable
data class DistributionDTO(
    val idDis: Int? = null,
    val maizeQty: Int,
    val riceQty: Int,
    val cassavaQty: Int,
    val soybeanQty: Int,
    val dapQty: Int,
    val kclQty: Int,
    val ureeQty: Int,
    val npk: Int,
    val suggestion: String? = null,
    val warehouseId: Int,
    val beneficiarieId: Int,
    val usersId: Int,
    val distDate: String // ou LocalDateTime si tu préfères
)
