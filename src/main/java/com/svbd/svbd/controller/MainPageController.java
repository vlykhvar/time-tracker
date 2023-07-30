package com.svbd.svbd.controller;

import com.svbd.svbd.controller.customfield.NumberField;
import com.svbd.svbd.dto.shift.ShiftBO;
import com.svbd.svbd.dto.shift.row.ShiftRowBO;
import com.svbd.svbd.service.EmployeeManagementService;
import com.svbd.svbd.service.ShiftManagementService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.svbd.svbd.enums.Pages.TABLE_EMPLOYEE;
import static com.svbd.svbd.util.AlertUtil.showAlert;
import static com.svbd.svbd.util.ConstantUtil.TIME_REGEX;
import static com.svbd.svbd.util.DateTimeUtil.prepareWorkTotalTime;
import static com.svbd.svbd.util.StageUtil.showStage;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class MainPageController implements Initializable {

    private final ShiftManagementService shiftManagementService = new ShiftManagementService();
    private final EmployeeManagementService employeeManagementService = new EmployeeManagementService();

    @FXML
    private MenuItem about;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private MenuItem menuEmployee;

    @FXML
    private MenuItem menuQuit;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TableView<ShiftRowBO> shitEmployeeData;

    @FXML
    private TableColumn<ShiftRowBO, Long> shiftRowId;

    @FXML
    private TableColumn<ShiftRowBO, Long> employeeId;

    @FXML
    private TableColumn<ShiftRowBO, String> employeeName;

    @FXML
    private TableColumn<ShiftRowBO, String> endEmployeeShift;

    @FXML
    private TableColumn<ShiftRowBO, String> startEmployeeShift;

    @FXML
    private TableColumn<ShiftRowBO, Integer> totalWorkTime;

    @FXML
    private NumberField taxi;

    @FXML
    private NumberField totalCash;

    @FXML
    private NumberField cashOnEvening;

    @FXML
    private NumberField cashOnMorning;

    @FXML
    private NumberField cashKeyOnEvening;

    @FXML
    private NumberField cashKeyOnMorning;

    @FXML
    private NumberField cashKeyTotal;

    @FXML
    private TextArea comments;

    @FXML
    private Button saveButton;

    @FXML
    private Button printOut;

    @FXML
    private TextField bonusTime;

    @FXML
    void exit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void openDialog(ActionEvent event) {

    }

    @FXML
    void showEmployeeScene(ActionEvent event) throws IOException {
        showStage(TABLE_EMPLOYEE);
    }

    @FXML
    void saveShift() throws Exception {
        var shift = new ShiftBO();
        shift.setDate(datePicker.valueProperty().get());
        shift.setTaxi(checkAndChangeStringToLong(taxi));
        shift.setCashKeyOnEvening(checkAndChangeStringToLong(cashKeyOnEvening));
        shift.setComments(comments.getText());
        shift.setCashKeyTotal(checkAndChangeStringToLong(cashKeyTotal));
        shift.setCashKeyOnMorning(checkAndChangeStringToLong(cashKeyOnMorning));
        shift.setCashOnEvening(checkAndChangeStringToLong(cashOnEvening));
        shift.setCashOnMorning(checkAndChangeStringToLong(cashOnMorning));
        shift.setTotalCash(checkAndChangeStringToLong(totalCash));
        shift.setBonusTime(checkAndChangeStringToLong(bonusTime));
        var rows = shitEmployeeData.getItems().stream()
                .filter(row -> nonNull(row.getStartShift()) && !row.getStartShift().isEmpty() || nonNull(row.getShiftRowId()))
                .collect(Collectors.toSet());
        isCorrectTime(rows);
            shiftManagementService.creatOrUpdate(shift, rows);
        prepareData();
    }

    @FXML
    void printOut(){
    }

    @FXML
    void showReportScene() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            saveButton.getScene().getAccelerators().put(
                    new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN), () -> {
                saveButton.fire();
            });
        });
        initDateOnFirstOpen();
        prepareData();
        datePicker.setOnAction((event) -> {
            prepareData();
        });
        editTable();
    }

    private void editTable() {
        startEmployeeShift.setCellFactory(TextFieldTableCell.forTableColumn());
        startEmployeeShift.setOnEditCommit(event -> {
                    var shift = (ShiftRowBO) event.getTableView().getItems().get(
                            event.getTablePosition().getRow());
                    shift.setStartShift(checkIfValueIsTime(event.getOldValue(), event.getNewValue()));
                    shift.setTotalWorkTime(prepareTotalTime(shift.getStartShift(), shift.getEndShift()));
                }
        );
        endEmployeeShift.setCellFactory(TextFieldTableCell.forTableColumn());
        endEmployeeShift.setOnEditCommit(event -> {
                    var shift = (ShiftRowBO) event.getTableView().getItems().get(
                            event.getTablePosition().getRow());
                    shift.setEndShift(checkIfValueIsTime(event.getOldValue(), event.getNewValue()));
                    shift.setTotalWorkTime(prepareTotalTime(shift.getStartShift(), shift.getEndShift()));
                }
        );
    }

    private void prepareData() {
        var shift = shiftManagementService.getShiftByDate(datePicker.getValue());
        prepareShiftDataToScene(shift);
        if (shift.getRows().isEmpty()) {
            prepareShitRowTableWithoutData();
        } else {
            prepareShitRowTableWithData(shift.getRows());
        }
        employeeName.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        employeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        startEmployeeShift.setCellValueFactory(new PropertyValueFactory<>("startShift"));
        endEmployeeShift.setCellValueFactory(new PropertyValueFactory<>("endShift"));
        shiftRowId.setCellValueFactory(new PropertyValueFactory<>("shiftRowId"));
        totalWorkTime.setCellValueFactory(new PropertyValueFactory<>("totalWorkTime"));
        shiftRowId.setCellValueFactory(new PropertyValueFactory<>("shiftRowId"));
        bonusTime.setText(String.valueOf(shift.getBonusTime()));
    }

    private void prepareShiftDataToScene(ShiftBO shiftBO) {;
        taxi.setText(adjustBigDecimal(shiftBO.getTaxi()));
        totalCash.setText(adjustBigDecimal(shiftBO.getTotalCash()));
        cashOnMorning.setText(adjustBigDecimal(shiftBO.getCashOnMorning()));
        cashOnEvening.setText(adjustBigDecimal(shiftBO.getCashOnEvening()));
        cashKeyOnEvening.setText(adjustBigDecimal(shiftBO.getCashKeyOnEvening()));
        cashKeyTotal.setText(adjustBigDecimal(shiftBO.getCashKeyTotal()));
        cashKeyOnMorning.setText(adjustBigDecimal(shiftBO.getCashKeyOnMorning()));
        comments.setText(shiftBO.getComments());
    }

    private void prepareShitRowTableWithoutData() {
        var shifts = employeeManagementService.getAllShortEmployeesData().stream()
                .map(ShiftRowBO::new)
                .collect(Collectors.toSet());
        shitEmployeeData.getItems().clear();
        shitEmployeeData.getItems().addAll(shifts);

    }

    private void prepareShitRowTableWithData(Collection<ShiftRowBO> rowBOs) {
        var excludeIds = rowBOs.stream().map(ShiftRowBO::getEmployeeId).collect(Collectors.toSet());
        var employees = employeeManagementService.getAllShortEmployeesDataExcludeIds(excludeIds);
        var emptyRowsWithEmployee = employees.stream().map(ShiftRowBO::new).collect(Collectors.toSet());
        rowBOs.addAll(emptyRowsWithEmployee);
        rowBOs = rowBOs.stream().sorted(Comparator.comparing(ShiftRowBO::getEmployeeName)).toList();
        shitEmployeeData.getItems().clear();
        shitEmployeeData.getItems().addAll(rowBOs);
    }

    private void initDateOnFirstOpen() {
        LocalDate initDate;
        if (LocalDateTime.now().getHour() < 8) {
            initDate = LocalDate.now().minusDays(1);
        } else {
            initDate = LocalDate.now();
        }
        datePicker.setValue(initDate);
    }

    private Long checkAndChangeStringToLong(TextField textField) {
        if (isNull(textField.getText()) || textField.getText().isBlank() || textField.getText().isEmpty()) {
            return 0L;
        }
        try {
            return Long.valueOf(textField.getText());
        } catch (NumberFormatException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Тільки цифрові значення");
            alert.showAndWait();
            throw e;
        }
    }

    private String adjustBigDecimal(Long decimal) {
        return isNull(decimal) ? Long.valueOf(0).toString() : decimal.toString();
    }

    private String checkIfValueIsTime(String oldValue, String newValue) {
        if (newValue.isEmpty() || newValue.matches(TIME_REGEX)) {
            return newValue;
        } else {
            showAlert(Alert.AlertType.ERROR, "Hе корректний формат", "Формат має бути у вигляді HH:mm");
            return oldValue;
        }
    }

    private Integer prepareTotalTime(String startShift, String endShift) {
            if (nonNull(startShift) && !startShift.isEmpty() && nonNull(endShift) && !endShift.isEmpty() &&
                    startShift.matches(TIME_REGEX) && endShift.matches(TIME_REGEX)) {
                var currentDate = datePicker.getValue();
                var startShiftTime = LocalTime.parse(startShift);
                var endShiftTime = LocalTime.parse(endShift);
                return prepareWorkTotalTime(currentDate, startShiftTime, endShiftTime);

            }
            return null;
    }

    private void isCorrectTime(Collection<ShiftRowBO> rowBOs) throws Exception {
        for (var row : rowBOs) {
            var total = prepareTotalTime(row.getStartShift(), row.getEndShift());
            if (isNull(total) || total > 0) {
                continue;
            }
            showAlert(Alert.AlertType.ERROR, "Невірвно вказаний час",
                    String.format("Співробітник %s має не вірний час зміни", row.getEmployeeName()));
            throw new Exception();
        }
    }
}
