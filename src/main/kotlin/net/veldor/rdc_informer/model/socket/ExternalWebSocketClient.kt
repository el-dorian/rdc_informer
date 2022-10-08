package net.veldor.rdc_informer.model.socket

import javafx.collections.ObservableList
import javafx.concurrent.Task
import net.veldor.rdc_informer.model.delegate.SocketDelegate
import net.veldor.rdc_informer.model.handler.JsonHandler
import net.veldor.rdc_informer.model.handler.PreferencesHandler
import net.veldor.rdc_informer.model.selection.Examination
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI


class ExternalWebSocketClient private constructor(uri: URI) : WebSocketClient(uri) {

    private lateinit var delegate: SocketDelegate

    init {
        /*        val ks = KeyStore.getInstance("JKS")
                ks.load(Files.newInputStream(Paths.get("C:/cert/rdcnn_server_cert_store")), "Xyiman123".toCharArray())
                val kmf = KeyManagerFactory.getInstance("SunX509")
                kmf.init(ks, "Xyiman123".toCharArray())
                val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
                tmf.init(ks)
                val sslContext: SSLContext = SSLContext.getInstance("TLS")
                sslContext.init(kmf.keyManagers, tmf.trustManagers, null)
                val factory = sslContext.socketFactory
                setSocketFactory(factory)*/
    }

    override fun onOpen(p0: ServerHandshake?) {
        delegate.connectionEstablished()
        authenticate()
    }

    private fun authenticate() {
        val root = JSONObject()
        root.put("command", "authorize")
        root.put("payload", PreferencesHandler.instance.getExternalServerToken())
        send(root.toString())
    }

    override fun onMessage(message: String?) {
        if (message != null) {
            val parsedMessage = JsonHandler().parseSocketMessage(message)
            if (parsedMessage.command == "patient_deleted") {
                val patient = JsonHandler().parsePatient(parsedMessage.payload)
                delegate.patientDeleted(patient)
            }
            if (parsedMessage.command == "patient_archived") {
                val patient = JsonHandler().parsePatient(parsedMessage.payload)
                delegate.patientArchived(patient)
            } else if (parsedMessage.command == "patient_registered") {
                val patient = JsonHandler().parsePatient(parsedMessage.payload)
                delegate.patientRegistered(patient)
            } else if (parsedMessage.command == "dicom_archive_deleted") {
                val dicomArchive = JsonHandler().parseSocketDicomArchive(parsedMessage.payload)
                delegate.dicomDeleted(dicomArchive)
            } else if (parsedMessage.command == "conclusion_deleted") {
                val conclusion = JsonHandler().parseSocketConclusion(parsedMessage.payload)
                delegate.conclusionDeleted(conclusion)
            } else if (parsedMessage.command == "conclusion_updated") {
                val conclusion = JsonHandler().parseSocketConclusion(parsedMessage.payload)
                delegate.conclusionUpdated(conclusion)
            } else if (parsedMessage.command == "conclusion_registered") {
                val conclusion = JsonHandler().parseSocketConclusion(parsedMessage.payload)
                delegate.conclusionRegistered(conclusion)
            } else if (parsedMessage.command == "dicom_archive_registered") {
                val dicomArchive = JsonHandler().parseSocketDicomArchive(parsedMessage.payload)
                delegate.dicomArchiveRegistered(dicomArchive)
            }else if (parsedMessage.command == "dicom_archive_updated") {
                val dicomArchive = JsonHandler().parseSocketDicomArchive(parsedMessage.payload)
                delegate.dicomArchiveUpdated(dicomArchive)
            }
        }
    }

    override fun onClose(p0: Int, p1: String?, p2: Boolean) {
        println("connection closed")
        delegate.connectionClosed()
        reopenConnection()
    }

    private fun reopenConnection() {
        val task = object : Task<Void?>() {
            override fun call(): Void? {
                Thread.sleep(5000)
                instance.reconnectBlocking()
                return null
            }
        }
        Thread(task).start()
    }

    override fun onError(e: Exception?) {
        println("connection error")
    }

    fun establishConnection(delegate: SocketDelegate) {
        this.delegate = delegate
        connect()
    }

    fun requestDeleteExamination(item: Examination) {
        // send command
        val root = JSONObject()
        root.put("command", "delete_patient")
        root.put("token", PreferencesHandler.instance.getExternalServerToken())
        root.put("payload", item.examination.id)
        send(root.toString())
    }

    fun requestDeleteExamination(items: ObservableList<Examination>) {
        items.forEach { requestDeleteExamination(it) }
    }

    companion object {
        val instance = ExternalWebSocketClient(URI("ws://127.0.0.1:27015"))
    }
}