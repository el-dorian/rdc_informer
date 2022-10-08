package net.veldor.rdc_informer.model.handler

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.veldor.rdc_informer.model.selection.*


class JsonHandler {
    fun parseDayExaminations(answer: String): ExaminationsList {
        return gson.fromJson(answer, ExaminationsList::class.java)
    }

    fun parseSocketMessage(message: String): WebsocketMessage {
        return gson.fromJson(message, WebsocketMessage::class.java)
    }

    fun parsePatient(raw: String): Patient {
        return gson.fromJson(raw, Patient::class.java)
    }

    fun parseSocketConclusion(raw: String): SocketConclusionItem {
        return gson.fromJson(raw, SocketConclusionItem::class.java)
    }

    fun parseSocketDicomArchive(raw: String): SocketDicomArchive {
        return gson.fromJson(raw, SocketDicomArchive::class.java)
    }

    private val gsonWithExclusion: Gson
        get() {
            return GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
        }

    private val gson: Gson
        get() {
            return GsonBuilder().create()
        }


}