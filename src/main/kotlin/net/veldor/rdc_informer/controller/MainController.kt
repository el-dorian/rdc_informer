package net.veldor.rdc_informer.controller

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.DragEvent
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import javafx.stage.Stage
import net.veldor.rdc_informer.RdcInformerApplication
import net.veldor.rdc_informer.model.delegate.ApiDelegate
import net.veldor.rdc_informer.model.delegate.SocketDelegate
import net.veldor.rdc_informer.model.selection.*
import net.veldor.rdc_informer.model.socket.ExternalWebSocketClient
import net.veldor.rdc_informer.model.utils.TrayNotification
import net.veldor.rdc_informer.model.view_model.MainViewModel
import net.veldor.rdc_informer.view.HaveConclusionCell
import net.veldor.rdc_informer.view.HaveExecutionCell
import net.veldor.rdc_informer.view.TooltippedTableCell
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class MainController() : Controller, SocketDelegate, ApiDelegate {
    companion object {
        const val CENTER_AURORA = 1
        const val CENTER_NV = 2
        const val CENTER_CT = 3
    }


    private lateinit var viewModel: MainViewModel
    private lateinit var mStage: Stage

    private var selectedCenter = CENTER_AURORA

    @FXML
    var mainContainer: VBox? = null

    @FXML
    var serverStateView: Label? = null

    @FXML
    var logArea: TextArea? = null

    @FXML
    var center: ToggleGroup? = null

    @FXML
    var progressBar: ProgressBar? = null

    @FXML
    var conclusionsAutoloadSwitcher: CheckMenuItem? = null

    @FXML
    var executionsAutoloadSwitcher: CheckMenuItem? = null

    @FXML
    lateinit var datePicker: DatePicker

    @FXML
    lateinit var executionsCountStateView: Label

    @FXML
    lateinit var executionsListTable: TableView<Examination>

    @FXML
    lateinit var executionNumberColumn: TableColumn<Examination, String>

    @FXML
    lateinit var personalsColumn: TableColumn<Examination, String>

    @FXML
    lateinit var haveExecutionColumn: TableColumn<Examination, String>

    @FXML
    lateinit var haveConclusionColumn: TableColumn<Examination, String>

    @FXML
    lateinit var examinationAreasColumn: TableColumn<Examination, String>

    @FXML
    lateinit var hasMailColumn: TableColumn<Examination, String>

    @FXML
    lateinit var isMessageSentColumn: TableColumn<Examination, String>

    @FXML
    lateinit var previousExecutionsColumn: TableColumn<Examination, String>

    @FXML
    lateinit var contrastColumn: TableColumn<Examination, String>

    @FXML
    lateinit var doctorColumn: TableColumn<Examination, String>

    // список описаний в работе
    private val executionsList: ObservableList<Examination> = FXCollections.observableArrayList()

    private var requestedDate: String? = null
    private var previousResult: ArrayList<Examination> = ArrayList<Examination>()

    private val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu", Locale.ENGLISH)


    override fun init(owner: Stage) {
        mStage = owner
        viewModel = MainViewModel()
        ExternalWebSocketClient.instance.establishConnection(this)
        // set currentDate
        requestedDate = LocalDate.now().format(dtf)
        setupDatepicker()
        setupCenterSwitch()
        setupTable()
        // получу данные пациентов
        viewModel.requestDayExaminations(selectedCenter, requestedDate!!, this)
    }

    override fun requestFocus() {
        mStage.requestFocus()
    }

    private fun setupTable() {
        //apply css
        val resource = RdcInformerApplication::class.java.getResource("/main.css")
        if (resource != null) {
            mStage.scene.stylesheets.add(resource.toExternalForm())
        }
        // placeholder
        executionsListTable.placeholder = Label("Обследований не найдено!")
        // table handle
        // fit table by height
        executionsListTable.prefHeightProperty().bind(mainContainer!!.heightProperty())

        executionsListTable.selectionModel.selectionMode = SelectionMode.MULTIPLE

        executionsListTable.setOnKeyPressed { event ->
            if (event.code == KeyCode.DELETE) {
                requestDeletePatients()
            }
        }

        // setup cell actions
        executionsListTable.setRowFactory { _: TableView<Examination> ->
            val row: TableRow<Examination> = TableRow<Examination>()
            row.setOnMouseClicked { event: MouseEvent ->
                if (event.clickCount == 2 && !row.isEmpty) {
                    println("MainController 147 init download content...")
                    /* // change cursor on waiter
                     val rowData: Examination = row.item
                     if (!rowData.conclusions.isEmpty()) {
                         val home = System.getProperty("user.home")
                         val base = File("$home/Downloads/")
                         try {
                             val delegate: DownloadController = viewModel.createDownloadProgressWindow(mStage)
                             viewModel.downloadConclusionFile(
                                 rowData.executionId,
                                 rowData.conclusionInfo,
                                 base,
                                 this,
                                 delegate
                             )
                         } catch (e: IOException) {
                             e.printStackTrace()
                         }
                     }*/
                }
            }
            val rowMenu = ContextMenu()
            var openItem = MenuItem("Найти все обследования пациента")
            openItem.onAction = EventHandler { event: ActionEvent? ->
                println("MainController 171 make global search by patient's examinations")
                /*val rowData: Examination = row.getItem()
                if (rowData.patientName != null && !rowData.patientName.equals("--") && rowData.birthDate != null) {
                    // покажу окно глобального поиска, загружу туда данные и выполню поиск
                    try {
                        viewModel.searchPreviousPatientExecutions(this, rowData.patientName, rowData.birthDate)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        viewModel.createInfoWindow("Не указано ФИО или дата рождения пациента", mStage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }*/
            }
            rowMenu.items.addAll(openItem)
            openItem = MenuItem("Просмотреть заключения")
            openItem.onAction = EventHandler { event: ActionEvent? ->
                /*val rowData: ExecutionInfo = row.getItem()
                if (rowData.haveConclusionFile == "Да") {
                    val myFile = DirectoryChooser()
                    val home = System.getProperty("user.home")
                    val base = File("$home/Downloads/")
                    myFile.initialDirectory = base
                    val file = myFile.showDialog(null)
                    try {
                        val delegate: DownloadController = viewModel.createDownloadProgressWindow(mStage)
                        viewModel.downloadConclusionFile(
                            rowData.executionId,
                            rowData.conclusionInfo,
                            file,
                            this,
                            delegate
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        viewModel.createInfoWindow("Файл ещё не загружен", mStage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }*/
            }
            rowMenu.items.addAll(openItem)
            openItem = MenuItem("Скачать архив")
            openItem.onAction = EventHandler { event: ActionEvent? ->
                /*val rowData: ExecutionInfo = row.getItem()
                if (rowData.haveExecutionFile == "Да") {
                    val myFile = FileChooser()
                    myFile.initialFileName = rowData.executionId + ".zip"
                    val home = System.getProperty("user.home")
                    val base = File("$home/Downloads/")
                    myFile.initialDirectory = base
                    val file = myFile.showSaveDialog(null)
                    try {
                        val delegate: DownloadController = viewModel.createDownloadProgressWindow(mStage)
                        viewModel.downloadExecutionFile(rowData.executionId, file, this, delegate)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        viewModel.createInfoWindow("Файл ещё не загружен", mStage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }*/
            }
            rowMenu.items.addAll(openItem)
            openItem = MenuItem("Удалить обследование")
            openItem.onAction = EventHandler { event: ActionEvent? ->
                requestDeletePatients()
                /*val rowData: ExecutionInfo = row.getItem()
                viewModel.deleteExecution(rowData.executionId, this)*/
            }
            rowMenu.items.addAll(openItem)
            openItem = MenuItem("Показать QR для входа")
            openItem.onAction = EventHandler { event: ActionEvent? ->
                /*val rowData: ExecutionInfo = row.getItem()
                viewModel.getQrCode(rowData.executionId, this)*/
            }
            rowMenu.items.addAll(openItem)
            openItem = MenuItem("Удалить все заключения по обследованию")
            openItem.onAction = EventHandler { event: ActionEvent? ->
                /*val rowData: ExecutionInfo = row.getItem()
                viewModel.deleteConclusions(rowData.executionId, this)*/
            }
            rowMenu.items.addAll(openItem)
            openItem = MenuItem("Отправить письмо")
            openItem.onAction = EventHandler { event: ActionEvent? ->
                /*val rowData: ExecutionInfo = row.getItem()
                if (rowData.hasMail == "Да") {
                    mStage.scene.cursor = Cursor.WAIT
                    viewModel.sendEmail(rowData.executionId, this)
                } else {
                    try {
                        viewModel.createInfoWindow("Адрес почты не внесён", mStage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }*/
            }
            rowMenu.items.addAll(openItem)
            openItem = MenuItem("Назначить электронную почту")
            openItem.onAction = EventHandler { event: ActionEvent? ->
                /*val rowData: ExecutionInfo = row.getItem()
                // покажу окно с полем ввода почты
                viewModel.openAddEmailWindow(rowData.executionId)*/
            }
            rowMenu.items.addAll(openItem)
            row.contextMenuProperty().bind(
                Bindings.`when`(Bindings.isNotNull(row.itemProperty()))
                    .then(rowMenu)
                    .otherwise(null as ContextMenu?)
            )
            row
        }
        // устанавливаем тип и значение которое должно хранится в колонке
        executionNumberColumn.cellValueFactory = PropertyValueFactory("examinationId")
        executionNumberColumn.cellFactory = TooltippedTableCell.forTableColumn()
        personalsColumn.cellValueFactory = PropertyValueFactory("patientName")
        personalsColumn.cellFactory = TooltippedTableCell.forTableColumn()
        haveExecutionColumn.cellValueFactory = PropertyValueFactory("dicomLoad")
        haveExecutionColumn.cellFactory = HaveExecutionCell.forTableColumn()
        haveConclusionColumn.cellValueFactory = PropertyValueFactory("haveConclusionFile")
        haveConclusionColumn.cellFactory = HaveExecutionCell.forTableColumn()
        examinationAreasColumn.cellValueFactory = PropertyValueFactory("examinationAreas")
        examinationAreasColumn.cellFactory = HaveConclusionCell.forTableColumn()
        contrastColumn.cellValueFactory = PropertyValueFactory("contrastAgentInfo")
        contrastColumn.cellFactory = TooltippedTableCell.forTableColumn()
        doctorColumn.cellValueFactory = PropertyValueFactory("diagnostician")
        doctorColumn.cellFactory = TooltippedTableCell.forTableColumn()
        hasMailColumn.cellValueFactory = PropertyValueFactory("hasMail")
        hasMailColumn.cellFactory = HaveConclusionCell.forTableColumn()
        isMessageSentColumn.cellValueFactory = PropertyValueFactory("mailSent")
        isMessageSentColumn.cellFactory = HaveConclusionCell.forTableColumn()
        previousExecutionsColumn.cellValueFactory = PropertyValueFactory("previousExecutionsCount")
        previousExecutionsColumn.cellFactory = HaveConclusionCell.forTableColumn()
        executionsListTable.items = executionsList
    }

    private fun requestDeletePatients() {
        val selectedItems = executionsListTable.selectionModel.selectedItems
        if (selectedItems.size > 0) {
            if (selectedItems.size == 1) {
                if (acceptAction(
                        "Удаление обследования",
                        "Удалить обследование ${selectedItems[0].examinationId} целиком без возможности восстановления?"
                    )
                ) {
                    ExternalWebSocketClient.instance.requestDeleteExamination(selectedItems[0])
                }
            } else {
                val stringBuilder = StringBuilder()
                selectedItems.forEach {
                    stringBuilder.append("${it.examinationId},")
                }
                if (acceptAction(
                        "Удаление обследований",
                        "Удалить обследования (${
                            stringBuilder.toString().dropLast(1)
                        }) целиком без возможности восстановления?"
                    )
                ) {
                    ExternalWebSocketClient.instance.requestDeleteExamination(selectedItems)
                }
            }
        }
    }

    private fun acceptAction(promise: String, body: String): Boolean {

        val alert = Alert(AlertType.CONFIRMATION)
        alert.isResizable = true
        alert.title = "Подтвердите действие"
        alert.headerText = promise
        alert.contentText = body

        val result = alert.showAndWait()
        return result.get() == ButtonType.OK
    }

    private fun setupCenterSwitch() {
        center!!.selectedToggleProperty()
            .addListener { _: ObservableValue<out Toggle?>?, _: Toggle?, _: Toggle? ->
                val selectedRadioButton = center!!.selectedToggle as RadioButton
                val text = selectedRadioButton.text
                selectedCenter = when (text) {
                    "Аврора" -> {
                        CENTER_AURORA
                    }

                    "КТ" -> {
                        CENTER_CT
                    }

                    else -> {
                        CENTER_NV
                    }
                }
                viewModel.requestDayExaminations(selectedCenter, requestedDate!!, this)
                progressBar!!.progress = ProgressBar.INDETERMINATE_PROGRESS
            }
    }

    private fun setupDatepicker() {
        datePicker.value = LocalDate.now()
        datePicker.onAction = EventHandler { _: ActionEvent? ->
            val currentDate = datePicker.value
            requestedDate = currentDate.format(dtf)
            viewModel.requestDayExaminations(selectedCenter, requestedDate!!, this)
            Platform.runLater {
                progressBar!!.progress = ProgressBar.INDETERMINATE_PROGRESS
            }
        }
    }

    fun searchInArchive() {

        // открою новое окно для поиска данных по архиву
        viewModel.showGlobalSearchWindow()
    }

    fun refreshExecutions() {
        viewModel.requestDayExaminations(selectedCenter, requestedDate!!, this)
        progressBar!!.progress = ProgressBar.INDETERMINATE_PROGRESS
    }

    fun handleDragDropped(dragEvent: DragEvent) {

    }

    fun handleDragOver(dragEvent: DragEvent) {

    }

    override fun connectionEstablished() {
        Platform.runLater {
            serverStateView?.text = "Соединение установлено"
            serverStateView?.textFill = Paint.valueOf("#00FF00")
        }
    }

    override fun connectionClosed() {
        Platform.runLater {
            serverStateView?.text = "Нет соединения!"
            serverStateView?.textFill = Paint.valueOf("#FF0000")
        }
    }

    override fun patientDeleted(patient: Patient) {
        Platform.runLater {
            val forDelete = ArrayList<Examination>()
            executionsList.forEach {
                if (it.examination.username == patient.examinationId) {
                    forDelete.add(it)
                }
            }
            if (forDelete.isNotEmpty()) {
                forDelete.forEach {
                    executionsList.remove(it)
                    TrayNotification().displayTray(
                        "Обследование удалено",
                        "Удалено обследование ${it.examinationId}"
                    )
                }
            }
            executionsCountStateView.text = "Обследований: " + executionsList.size
        }
    }

    override fun patientArchived(patient: Patient) {
        Platform.runLater {
            val forDelete = ArrayList<Examination>()
            executionsList.forEach {
                if (it.examination.username == patient.examinationId) {
                    forDelete.add(it)
                }
            }
            if (forDelete.isNotEmpty()) {
                forDelete.forEach {
                    executionsList.remove(it)
                    TrayNotification().displayTray(
                        "Обследование архивировано",
                        "Архивировано обследование ${it.examinationId}"
                    )
                }
            }
            executionsCountStateView.text = "Обследований: " + executionsList.size
        }
    }

    override fun patientRegistered(patient: Patient) {
        Platform.runLater {
            if (requestedDate == LocalDate.now().format(dtf) && executionInSameCenter(patient.examinationId)) {
                val time: Int = (System.currentTimeMillis() / 1000).toInt()
                executionsList.add(
                    0,
                    Examination(
                        previousExecutionsCount = "0",
                        arrayListOf(),
                        ExaminationItem(
                            patient.patientId.toInt(), time, time, 0, 0, patient.examinationId, 1
                        ),
                        arrayListOf(),
                        null
                    )
                )
                TrayNotification().displayTray(
                    "Добавлено обследование",
                    "Добавлено обследование ${patient.examinationId}, пароль : ${patient.password}"
                )
            }
            executionsCountStateView.text = "Обследований: " + executionsList.size
        }
    }

    override fun conclusionRegistered(conclusion: SocketConclusionItem) {
        Platform.runLater {
            if (executionInSameCenter(conclusion.executionNumber)) {
                // search item for update
                executionsList.forEach {
                    if (it.examinationId == conclusion.executionNumber) {
                        it.conclusions.add(conclusion.toConclusionItem())
                        TrayNotification().displayTray(
                            "Добавлено заключение",
                            "Добавлено заключение для  ${conclusion.executionNumber} (Пациент ${conclusion.patientName}): ${conclusion.executionArea}"
                        )
                    }
                }
            }
        }
    }

    override fun dicomArchiveRegistered(dicomArchive: SocketDicomArchive) {
        Platform.runLater {
            if (executionInSameCenter(dicomArchive.examinationId)) {
                // search item for update
                executionsList.forEach {
                    if (it.examination.id == dicomArchive.examinationId.toInt()) {
                        it.dicom = DicomItem(dicomArchive.examinationId.toInt(), it.examination.username)
                        TrayNotification().displayTray(
                            "Добавлен архив обследования",
                            "Добавлен архив обследования для  ${it.examinationId}"
                        )
                    }
                }
            }
        }
    }

    override fun dicomDeleted(dicomArchive: SocketDicomArchive) {
        Platform.runLater {
            if (executionInSameCenter(dicomArchive.examinationId)) {
                // search item for update
                executionsList.forEach {
                    if (it.examination.id == dicomArchive.examinationId.toInt()) {
                        it.dicom = null
                        TrayNotification().displayTray(
                            "Удалён архив обследования",
                            "Удалён архив обследования для  ${it.examinationId}"
                        )
                    }
                }
            }
        }
    }

    override fun conclusionDeleted(conclusion: SocketConclusionItem) {
        Platform.runLater {
            if (executionInSameCenter(conclusion.executionNumber)) {
                // search item for update
                executionsList.forEach { executions ->
                    if (executions.examinationId == conclusion.executionNumber) {
                        executions.conclusions.removeIf {
                            if (it.execution_area == conclusion.executionArea) {
                                TrayNotification().displayTray(
                                    "Удалено заключение",
                                    "Удалено заключение по ${conclusion.executionArea} для ${conclusion.executionNumber}"
                                )
                            }
                            return@removeIf it.execution_area == conclusion.executionArea
                        }
                    }
                }
            }
        }
    }

    override fun conclusionUpdated(conclusion: SocketConclusionItem) {
        Platform.runLater {
            if (executionInSameCenter(conclusion.executionNumber)) {
                // search item for update
                executionsList.forEach { executions ->
                    if (executions.examinationId == conclusion.executionNumber) {
                        var forReplaceId: Int = -1
                        executions.conclusions.forEach {
                            if (it.execution_area == conclusion.executionArea) {
                                forReplaceId = executions.conclusions.indexOf(it)
                            }
                        }
                        if (forReplaceId >= 0) {
                            executions.conclusions[forReplaceId] = conclusion.toConclusionItem()
                            TrayNotification().displayTray(
                                "Обновлено заключение",
                                "Обновлено заключение по ${conclusion.executionArea} для ${conclusion.executionNumber}"
                            )
                        }
                    }
                }
            }
        }
    }

    override fun dicomArchiveUpdated(dicomArchive: SocketDicomArchive) {
        Platform.runLater {
            if (executionInSameCenter(dicomArchive.examinationId)) {
                // search item for update
                executionsList.forEach {
                    if (it.examination.id == dicomArchive.examinationId.toInt()) {
                        TrayNotification().displayTray(
                            "Изменён архив обследования",
                            "Изменён архив обследования для  ${it.examinationId}"
                        )
                    }
                }
            }
        }
    }

    private fun executionInSameCenter(examinationId: String): Boolean {
        if (examinationId.startsWith("A") && selectedCenter == CENTER_AURORA) {
            return true
        } else if (examinationId.startsWith("T") && selectedCenter == CENTER_CT) {
            return true
        } else if (Character.isDigit(examinationId.toCharArray(0, 1)[0]) && selectedCenter == CENTER_NV) {
            return true
        }
        return false
    }

    override fun dayInfoReceived(list: ArrayList<Examination>) {
        Platform.runLater {
            progressBar!!.progress = 0.0
            // refresh info about executions
            var sortColumn: TableColumn<*, *>? = null
            var st: TableColumn.SortType? = null
            if (executionsListTable.sortOrder.size > 0) {
                sortColumn = executionsListTable.sortOrder[0]
                st = sortColumn.sortType
            }
            executionsList.setAll(list)
            if (sortColumn != null) {
                executionsListTable.sortOrder.add(sortColumn as TableColumn<Examination, *>?)
                sortColumn.sortType = st
                sortColumn.isSortable = true // This performs a sort
            }
            executionsCountStateView.text = "Обследований: " + list.size
            previousResult = list
        }
    }

    override fun dayInfoReceiveError(message: String) {
        Platform.runLater {
            TrayNotification().displayTray(
                "Не удалось получить список обследований",
                "Попробуйте ещё раз позднее."
            )
        }
    }
}