package net.veldor.rdc_informer

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import net.veldor.rdc_informer.controller.MainController
import kotlin.system.exitProcess

class RdcInformerApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(RdcInformerApplication::class.java.getResource("/main.fxml"))
        val scene = Scene(fxmlLoader.load(), 1200.0, 500.0)
        stage.title = "Socket server"
        stage.scene = scene
        stage.show()

        val controller: MainController = fxmlLoader.getController()

        controller.init(stage)

        stage.icons.add(Image(this::class.java.getResourceAsStream("/icon.jpg")))

        stage.setOnHidden {
            println("closed, exit app")
            exitProcess(0)
        }
    }
}

fun main() {
    Application.launch(RdcInformerApplication::class.java)
}