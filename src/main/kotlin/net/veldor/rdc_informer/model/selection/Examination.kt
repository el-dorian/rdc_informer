package net.veldor.rdc_informer.model.selection

import java.lang.StringBuilder

class Examination(
    val previousExecutionsCount: String,
    val mails: ArrayList<Email>,
    val examination: ExaminationItem,
    val conclusions: ArrayList<ConclusionItem>,
    var dicom: DicomItem?
) {
    val examinationId: String
        get() {
            return examination.username
        }

    val patientName: String
        get() {
            if (conclusions.isNotEmpty()) {
                return conclusions[0].patient_name
            }
            return "--"
        }

    val haveConclusionFile: String
        get() {
            if (conclusions.isNotEmpty()) {
                return "Да"
            }
            return "Нет"
        }
    val hasMail: String
        get() {
            if (mails.isNotEmpty()) {
                return "Да"
            }
            return "Нет"
        }

    val mailSent: String
        get() {
            if (mails.isNotEmpty()) {
                mails.forEach {
                    if (it.mailed_yet) {
                        return "Да"
                    }
                }
            }
            return "Нет"
        }

    val examinationAreas: String
        get() {
            if (conclusions.isNotEmpty()) {
                val sb = StringBuilder()
                conclusions.forEach {
                    sb.append("${it.execution_area}\n")
                }
                return sb.toString().dropLast(1)
            }
            return "--"
        }

    val diagnostician: String
        get() {
            if (conclusions.isNotEmpty()) {
                return conclusions[0].diagnostician
            }
            return "--"
        }

    val contrastAgentInfo: String
        get() {
            if (conclusions.isNotEmpty()) {
                conclusions.forEach {
                    if (it.contrast_info.isNotEmpty()) {
                        return it.contrast_info
                    }
                }
            }
            return "--"
        }

    val dicomLoad: String
        get() {
            if (dicom != null) {
                return "Да"
            }
            return "Нет"
        }
}