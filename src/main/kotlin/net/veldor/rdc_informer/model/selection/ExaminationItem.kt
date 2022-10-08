package net.veldor.rdc_informer.model.selection

data class ExaminationItem(
    val id: Int,
    val updated_at: Int,
    val created_at: Int,
    val failed_try: Int,
    val last_login_try: Int,
    val username: String,
    val status: Int
)
