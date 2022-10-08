package net.veldor.rdc_informer.model.utils

import java.awt.*
import java.awt.TrayIcon.MessageType


class TrayNotification {

    @Throws(AWTException::class)
    fun displayTray(title: String, body: String) {
        //Obtain only one instance of the SystemTray object
        val tray = SystemTray.getSystemTray()

        //If the icon is a file
        val image: Image = Toolkit.getDefaultToolkit().createImage("icon.png")
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));
        val trayIcon = TrayIcon(image, "Сообщение от помощника РДЦ")
        //Let the system resize the image if needed
        trayIcon.isImageAutoSize = true
        //Set tooltip text for the tray icon
        trayIcon.toolTip = "Сообщение от помощника РДЦ"
        tray.add(trayIcon)
        trayIcon.displayMessage(title, body, MessageType.INFO)
    }
}