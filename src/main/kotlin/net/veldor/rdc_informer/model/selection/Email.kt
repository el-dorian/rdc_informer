package net.veldor.rdc_informer.model.selection

data class Email(
    val id: Int,
    val address: String,
    val patient_id: Int,
    val mailed_yet: Boolean
)
