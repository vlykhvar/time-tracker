package com.svbd.svbd.controller;

import com.svbd.svbd.controller.customfield.NumberField;
import com.svbd.svbd.dto.shift.ShiftBO;
import com.svbd.svbd.dto.shift.row.ShiftRowBO;
import com.svbd.svbd.exception.IncorrectPasswordException;
import com.svbd.svbd.service.EmployeeManagementService;
import com.svbd.svbd.service.ReportsService;
import com.svbd.svbd.service.SettingsManagementService;
import com.svbd.svbd.service.ShiftManagementService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.svbd.svbd.enums.Pages.REPORTS_PAGE;
import static com.svbd.svbd.enums.Pages.SETTINGS_PAGE;
import static com.svbd.svbd.util.AlertUtil.showAlert;
import static com.svbd.svbd.util.ConstantUtil.TIME_REGEX;
import static com.svbd.svbd.util.DateTimeUtil.prepareWorkTotalTime;
import static com.svbd.svbd.util.StageUtil.showStage;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class MainPageController extends Application implements Initializable {

    private final ShiftManagementService shiftManagementService = new ShiftManagementService();
    private final EmployeeManagementService employeeManagementService = new EmployeeManagementService();
    private final SettingsManagementService settingsManagementService = new SettingsManagementService();
    private final ReportsService reportsService = new ReportsService();

    public Menu management;

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
    private NumberField bonusTime;

    @FXML
    void exit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void openDialog(ActionEvent event) {

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
                .collect(Collectors.toSet());;
        isCorrectTime(rows);
            shiftManagementService.creatOrUpdate(shift, rows);
        prepareData();
    }

    @FXML
    void printOut() throws Exception {
        saveShift();
        var patch = reportsService.generateDailyReport(datePicker.getValue());
        File excelFile = new File(patch);
        getHostServices().showDocument(excelFile.toURI().toURL().toExternalForm());
    }

    @FXML
    void showReportScene() throws IOException {
        showStage(REPORTS_PAGE);
    }

    @FXML
    void showSettings() throws IOException {
        showStage(SETTINGS_PAGE);
    };

    @FXML
    void validateUser() throws IncorrectPasswordException {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Необхідна автентифікація");
        dialog.setHeaderText("Для входу в розділ управління необхідно введення пароля.");
        dialog.setGraphic(new Circle(15, Color.RED)); // Custom graphic
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField pwd = new PasswordField();
        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(10);
        content.getChildren().addAll(new Label("Введіть пароль для продовження:"), pwd);
        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return pwd.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        var password = result.orElseThrow(IncorrectPasswordException::new);
        var requiredPassword = settingsManagementService.getCompanySettings().getCompanyPassword();
        if (!requiredPassword.equals(password)) {
            showAlert(Alert.AlertType.ERROR, "Не вірний пароль", "Не вірний пароль");
            throw new IncorrectPasswordException();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setConverter(new StringConverter<LocalDate>()
        {
            private DateTimeFormatter dateTimeFormatter= DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate localDate)
            {
                if(localDate==null)
                    return "";
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString)
            {
                if(dateString==null || dateString.trim().isEmpty())
                {
                    return null;
                }
                return LocalDate.parse(dateString,dateTimeFormatter);
            }
        });
        initDateOnFirstOpen();
        datePicker.setOnAction((event) -> {
            prepareData();
        });
        prepareData();
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
        taxi.setText(adjustLong(shiftBO.getTaxi()));
        totalCash.setText(adjustLong(shiftBO.getTotalCash()));
        cashOnMorning.setText(adjustLong(shiftBO.getCashOnMorning()));
        cashOnEvening.setText(adjustLong(shiftBO.getCashOnEvening()));
        cashKeyOnEvening.setText(adjustLong(shiftBO.getCashKeyOnEvening()));
        cashKeyTotal.setText(adjustLong(shiftBO.getCashKeyTotal()));
        cashKeyOnMorning.setText(adjustLong(shiftBO.getCashKeyOnMorning()));
        comments.setText(shiftBO.getComments());
        bonusTime.setText(adjustLong(shiftBO.getBonusTime()));
    }

    private void prepareShitRowTableWithoutData() {
        var shifts = employeeManagementService.getAllShortEmployeesData().stream()
                .map(ShiftRowBO::new)
                .collect(Collectors.toSet());
        shitEmployeeData.getItems().clear();
        shitEmployeeData.getItems().addAll(shifts.stream().sorted(Comparator.comparing(ShiftRowBO::getEmployeeName)).toList());

    }

    private void prepareShitRowTableWithData(Collection<ShiftRowBO> rowBOs) {
        var excludeIds = rowBOs.stream().map(ShiftRowBO::getEmployeeId).collect(Collectors.toSet());
        var employees = employeeManagementService.getAllShortEmployeesDataExcludeIds(excludeIds);
        var emptyRowsWithEmployee = employees.stream()
                .map(ShiftRowBO::new)
                .collect(Collectors.toSet());
        rowBOs.addAll(emptyRowsWithEmployee);
        rowBOs = rowBOs.stream()
                .sorted(Comparator.comparing(ShiftRowBO::getEmployeeName))
                .toList();
        shitEmployeeData.getItems().clear();
        shitEmployeeData.getItems().addAll(rowBOs);
    }

    private void initDateOnFirstOpen() {
        LocalDate initDate;
        if (LocalDateTime.now().getHour() < 7) {
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

    private String adjustLong(Long number) {
        return isNull(number) ? Long.valueOf(0).toString() : number.toString();
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
            return 0;
    }

    private void isCorrectTime(Collection<ShiftRowBO> rowBOs) throws Exception {
        for (var row : rowBOs) {
            if (prepareTotalTime(row.getStartShift(), row.getEndShift()) >= 0) {
                continue;
            }
            showAlert(Alert.AlertType.ERROR, "Невірвно вказаний час",
                    String.format("Співробітник %s має не вірний час зміни", row.getEmployeeName()));
            throw new Exception();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
