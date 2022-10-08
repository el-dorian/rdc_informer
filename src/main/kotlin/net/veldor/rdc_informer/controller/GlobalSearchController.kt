package net.veldor.rdc_informer.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.stage.Stage
import net.veldor.rdc_informer.model.selection.Examination

class GlobalSearchController() : Controller {


    lateinit var mOwner: Stage

    @FXML
    lateinit var executionsCountStateView: Label

    @FXML
    lateinit var progressBar: ProgressBar

    @FXML
    lateinit var searchParamsResetButton: Button

    @FXML
    lateinit var doSearchButton: Button

    @FXML
    lateinit var centerChoiceList: ComboBox<Any>

    @FXML
    lateinit var executionAreaChoiceList: ComboBox<Any>

    @FXML
    lateinit var centerContainer: VBox

    @FXML
    lateinit var areasBoxContainer: VBox

    @FXML
    lateinit var doctorChoiceList: ComboBox<Any>

    @FXML
    lateinit var textSearchInput: TextField

    @FXML
    lateinit var executionDateFinishInput: DatePicker

    @FXML
    lateinit var executionDateInput: DatePicker

    @FXML
    lateinit var patientBirthdateInput: DatePicker

    @FXML
    lateinit var patientPersonalsInput: TextField

    @FXML
    lateinit var executionIdInput: TextField

    @FXML
    lateinit var executionsListTable: TableView<Examination>

    @FXML
    lateinit var executionNumberColumn: TableColumn<Examination, String>

    @FXML
    lateinit var executionTypeColumn: TableColumn<Examination, String>

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

    override fun init(owner: Stage) {
        mOwner = owner
    }

    override fun requestFocus() {
        mOwner.requestFocus()
    }

    fun checkEnterForSearch(keyEvent: KeyEvent) {

    }

    fun doSearch(actionEvent: ActionEvent) {

    }

    fun dropSearchOptions(mouseEvent: MouseEvent) {

    }
}