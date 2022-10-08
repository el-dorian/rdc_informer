package net.veldor.rdc_informer.model.delegate

import net.veldor.rdc_informer.model.selection.Patient
import net.veldor.rdc_informer.model.selection.SocketConclusionItem
import net.veldor.rdc_informer.model.selection.SocketDicomArchive

interface SocketDelegate {
    fun connectionEstablished()
    fun connectionClosed()
    fun patientDeleted(patient: Patient)
    fun patientRegistered(patient: Patient)
    fun conclusionRegistered(conclusion: SocketConclusionItem)
    fun dicomArchiveRegistered(dicomArchive: SocketDicomArchive)
    fun dicomDeleted(dicomArchive: SocketDicomArchive)
    fun conclusionDeleted(conclusion: SocketConclusionItem)
    fun conclusionUpdated(conclusion: SocketConclusionItem)
    fun dicomArchiveUpdated(dicomArchive: SocketDicomArchive)
    fun patientArchived(patient: Patient)
}