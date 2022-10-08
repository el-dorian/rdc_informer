package net.veldor.rdc_informer.model.view_model

import javafx.concurrent.Task
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Modality
import javafx.stage.Stage
import net.veldor.rdc_informer.RdcInformerApplication
import net.veldor.rdc_informer.controller.Controller
import net.veldor.rdc_informer.controller.GlobalSearchController
import net.veldor.rdc_informer.model.delegate.ApiDelegate
import net.veldor.rdc_informer.model.handler.JsonHandler
import net.veldor.rdc_informer.model.http.ConnectionToWebserver
import java.io.IOException
import java.io.InputStream
import java.util.*

class MainViewModel {
    private var globalSearchController: GlobalSearchController? = null
    private var archiveSearchWindowCreated: Boolean = false

    fun requestDayExaminations(selectedCenter: Int, requestedDate: String, delegate: ApiDelegate) {
        val task = object : Task<Void?>() {
            override fun call(): Void? {
                try {
                    val answer = ConnectionToWebserver().requestDailyConclusions(selectedCenter, requestedDate)
                    println("MainViewModel 15 $answer")
                    val executionsInfo = JsonHandler().parseDayExaminations(answer)
                    delegate.dayInfoReceived(executionsInfo.payload)
                } catch (t: Throwable) {
                    delegate.dayInfoReceiveError("Не удалось получить данные. Попробуйте позднее!")
                }
                return null
            }
        }
        Thread(task).start()
    }

    fun showGlobalSearchWindow(): Controller {
        if (!archiveSearchWindowCreated) {
            try {
                globalSearchController = createNewWindow(
                    "/archive_search.fxml",
                    "Заключения",
                    null,
                    1200.0,
                    800.0
                ) as GlobalSearchController
                globalSearchController!!.mOwner.show()
                globalSearchController!!.mOwner.icons.add(
                    Image(
                        Objects.requireNonNull(
                            RdcInformerApplication::class.java.getResourceAsStream("/application_icon.png")
                        )
                    )
                )
                globalSearchController!!.requestFocus()
                archiveSearchWindowCreated = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            globalSearchController!!.mOwner.show()
            globalSearchController!!.requestFocus()
        }
        return globalSearchController!!
    }

    @Throws(IOException::class)
    fun createNewWindow(view: String, title: String, owner: Stage?, width: Double, height: Double): Controller {
        val stage = Stage()
        val loader = FXMLLoader(javaClass.getResource(view))
        val root = loader.load<Parent>()
        stage.title = title
        stage.scene = Scene(root, width, height)
        if (owner != null) {
            stage.initModality(Modality.WINDOW_MODAL)
            stage.initOwner(owner)
        }
        stage.show()
        val controller: Controller = loader.getController()
        controller.init(stage)
        return controller
    }
}