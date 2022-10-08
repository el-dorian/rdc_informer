package net.veldor.rdc_informer.model.selection

data class SocketConclusionItem(
    val conclusionId: Int,
    val executionNumber: String,
    val executionDate: String,
    val diagnostician: String,
    val patientName: String,
    val patientSex: String,
    val patientBirthdate: String,
    val executionArea: String,
    val contrastInfo: String,
    val hash: String
) {
    fun toConclusionItem(): ConclusionItem {
        return ConclusionItem(
            id = conclusionId,
            diagnostician = diagnostician,
            patient_birthdate = patientBirthdate,
            patient_sex = patientSex,
            execution_date = executionDate,
            execution_area = executionArea,
            patient_name = patientName,
            execution_number = executionNumber,
            contrast_info = contrastInfo

        )
    }
}
