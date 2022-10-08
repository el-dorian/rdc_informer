package net.veldor.rdc_informer.model.delegate

import net.veldor.rdc_informer.model.selection.Examination

interface ApiDelegate {
    fun dayInfoReceived(list: ArrayList<Examination>)
    fun dayInfoReceiveError(message: String)
}