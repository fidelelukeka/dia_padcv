package id.fs.dia_padcv.ui.utils

data class SyncResult(
    val step: String,
    val pending: Int = 0,
    val success: Int = 0,
    val failed: Int = 0,
    val errorMessage: String? = null
)