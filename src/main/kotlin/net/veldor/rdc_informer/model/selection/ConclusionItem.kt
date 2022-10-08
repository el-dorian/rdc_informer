package net.veldor.rdc_informer.model.selection

data class ConclusionItem(
    val id: Int,
    val diagnostician: String,
    val patient_birthdate: String,
    val patient_sex: String,
    val execution_date: String,
    val execution_area: String,
    val patient_name: String,
    val execution_number: String,
    val contrast_info: String
)
