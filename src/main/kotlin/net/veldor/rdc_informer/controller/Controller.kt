package net.veldor.rdc_informer.controller

import javafx.stage.Stage

interface Controller {
    fun init(owner: Stage)
    fun requestFocus()
}