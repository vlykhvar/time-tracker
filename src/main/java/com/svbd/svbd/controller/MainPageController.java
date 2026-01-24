package com.svbd.svbd.controller;

import com.svbd.svbd.controller.customfield.NumberField;
import com.svbd.svbd.dto.report.ReportRequest;
import com.svbd.svbd.dto.shift.ShiftBO;
import com.svbd.svbd.dto.shift.ShiftRequestBO;
import com.svbd.svbd.dto.shift.row.ShiftRowBO;
import com.svbd.svbd.dto.shift.row.ShiftRowRequestBO;
import com.svbd.svbd.enums.EReportType;
import com.svbd.svbd.exception.IncorrectPasswordException;
import com.svbd.svbd.service.EmployeeManagementService;
import com.svbd.svbd.service.ReportsService;
import com.svbd.svbd.service.SettingsManagementService;
import com.svbd.svbd.service.ShiftManagementService;
import com.svbd.svbd.util.StageManager;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.svbd.svbd.enums.Pages.*;
import static com.svbd.svbd.util.AlertUtil.showAlert;
import static com.svbd.svbd.util.DateTimeUtil.prepareWorkTotalTime;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class MainPageController implements Initializable {

    private final ShiftManagementService shiftManagementService;
    private final EmployeeManagementService employeeManagementService;
    private final SettingsManagementService settingsManagementService;
    private final ReportsService reportsService;
    private final StageManager stageManager;
    private final HostServices hostServices;

    public MainPageController(ShiftManagementService shiftManagementService,
                              EmployeeManagementService employeeManagementService,
                              SettingsManagementService settingsManagementService,
                              ReportsService reportsService,
                              StageManager stageManager,
                              HostServices hostServices) {
        this.shiftManagementService = shiftManagementService;
        this.employeeManagementService = employeeManagementService;
        this.settingsManagementService = settingsManagementService;
        this.reportsService = reportsService;
        this.stageManager = stageManager;
        this.hostServices = hostServices;
    }

    public Menu management;

    @FXML
    private MenuItem about;

    @FXML
    private DatePicker datePicker;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<ShiftRowBO> shiftEmployeeData;

    @FXML
    private TableColumn<ShiftRowBO, Long> shiftRowId;

    @FXML
    private TableColumn<ShiftRowBO, Long> employeeId;

    @FXML
    private TableColumn<ShiftRowBO, String> employeeName;

    @FXML
    private TableColumn<ShiftRowBO, LocalTime> endEmployeeShift;

    @FXML
    private TableColumn<ShiftRowBO, LocalTime> startEmployeeShift;

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
    private NumberField bonusTime;

    @FXML
    private NumberField dailyRevenue;

    @FXML
    void openAbout(ActionEvent event) {
        try {
            StageManager.FxmlLoadResult<Object> result = stageManager.load(ABOUT);
            Stage stage = stageManager.createModalStage(anchorPane.getScene(), "Про програму");
            stage.setScene(result.scene());
            stage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Exception", e.getMessage());
        }
    }

    @FXML
    void saveShift() {
        try {
            var shift = new ShiftRequestBO();
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
            shift.setDailyRevenue(checkAndChangeStringToLong(dailyRevenue));
            var rows = new HashSet<ShiftRowRequestBO>();
            for (ShiftRowBO row : shiftEmployeeData.getItems()) {
                if (nonNull(row.getStartShift()) || nonNull(row.getShiftRowId())) {
                    var shiftRowRequestBO = toShiftRowRequestBO(row);
                    rows.add(shiftRowRequestBO);
                }
            }
            shift.getShiftRowBOs().addAll(rows);
            shiftManagementService.creatOrUpdate(shift);
            prepareData();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Exception", e.getMessage());
        }
    }

    @FXML
    void printOut() {
        try {
            saveShift();
            var request = new ReportRequest(datePicker.getValue(), datePicker.getValue());
            var patch = reportsService.generateReport(EReportType.DAILY, request);
            File excelFile = new File(patch);
            hostServices.showDocument(excelFile.toURI().toURL().toExternalForm());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Exception", e.getMessage());
        }
    }

    @FXML
    void showReportScene(ActionEvent event) {
        try {
            StageManager.FxmlLoadResult<Object> result = stageManager.load(REPORTS_PAGE);
            Stage stage = stageManager.createModalStage(anchorPane.getScene(), "Звіти");
            stage.setScene(result.scene());
            stage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Exception", e.getMessage());
        }
    }

    @FXML
    void showSettings(ActionEvent event) {
        try {
            StageManager.FxmlLoadResult<Object> result = stageManager.load(SETTINGS_PAGE);
            Stage stage = stageManager.createModalStage(anchorPane.getScene(), "Налаштування");
            stage.setScene(result.scene());
            stage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Exception", e.getMessage());
        }
    }

    @FXML
    void validateUser() throws IncorrectPasswordException {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Необхідна автентифікація");
        dialog.setHeaderText("Для входу в розділ управління необхідно введення пароля.");
        dialog.setGraphic(new Circle(15, Color.RED));
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
        datePicker.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null)
                    return "";
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateString, dateTimeFormatter);
            }
        });
        initDateOnFirstOpen();
        datePicker.setOnAction((event) -> prepareData());
        prepareData();
        editTable();
    }

    private void editTable() {
        List<LocalTime> timeSlots = generateTimeSlots();
        StringConverter<LocalTime> timeConverter = new StringConverter<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            @Override
            public String toString(LocalTime time) {
                return time != null ? formatter.format(time) : "--:--";
            }

            @Override
            public LocalTime fromString(String string) {
                return string != null && !string.equals("--:--") ? LocalTime.parse(string, formatter) : null;
            }
        };

        startEmployeeShift.setCellFactory(ComboBoxTableCell.forTableColumn(timeConverter, FXCollections.observableArrayList(timeSlots)));
        startEmployeeShift.setOnEditCommit(event -> {
            var shift = event.getRowValue();
            shift.setStartShift(event.getNewValue());
            shift.setTotalWorkTime(prepareTotalTime(shift.getStartShift(), shift.getEndShift()));
            shiftEmployeeData.refresh();
        });

        endEmployeeShift.setCellFactory(ComboBoxTableCell.forTableColumn(timeConverter, FXCollections.observableArrayList(timeSlots)));
        endEmployeeShift.setOnEditCommit(event -> {
            var shift = event.getRowValue();
            shift.setEndShift(event.getNewValue());
            shift.setTotalWorkTime(prepareTotalTime(shift.getStartShift(), shift.getEndShift()));
            shiftEmployeeData.refresh();
        });
    }

    private List<LocalTime> generateTimeSlots() {
        List<LocalTime> timeSlots = new ArrayList<>();
        timeSlots.add(null);
        timeSlots.addAll(Stream.iterate(LocalTime.MIN, time -> time.plusMinutes(30))
                .limit(48)
                .collect(Collectors.toList()));
        return timeSlots;
    }

    private void prepareData() {
        var shift = shiftManagementService.getShiftByDate(datePicker.getValue());

        prepareShiftDataToScene(shift);
        if (shift.getRows().isEmpty()) {
            prepareShiftRowTableWithoutData();
        } else {
            prepareShiftRowTableWithData(shift.getRows());
        }
        employeeName.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        employeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        startEmployeeShift.setCellValueFactory(new PropertyValueFactory<>("startShift"));
        endEmployeeShift.setCellValueFactory(new PropertyValueFactory<>("endShift"));
        shiftRowId.setCellValueFactory(new PropertyValueFactory<>("shiftRowId"));
        totalWorkTime.setCellValueFactory(new PropertyValueFactory<>("totalWorkTime"));
        bonusTime.setText(String.valueOf(shift.getBonusTime()));
    }

    private void prepareShiftDataToScene(ShiftBO shiftBO) {
        taxi.setText(adjustLong(shiftBO.getTaxi()));
        totalCash.setText(adjustLong(shiftBO.getTotalCash()));
        cashOnMorning.setText(adjustLong(shiftBO.getCashOnMorning()));
        cashOnEvening.setText(adjustLong(shiftBO.getCashOnEvening()));
        cashKeyOnEvening.setText(adjustLong(shiftBO.getCashKeyOnEvening()));
        cashKeyTotal.setText(adjustLong(shiftBO.getCashKeyTotal()));
        cashKeyOnMorning.setText(adjustLong(shiftBO.getCashKeyOnMorning()));
        comments.setText(shiftBO.getComments());
        bonusTime.setText(adjustLong(shiftBO.getBonusTime()));
        dailyRevenue.setText(adjustLong(shiftBO.getDailyRevenue()));
    }

    private void prepareShiftRowTableWithoutData() {
        var shifts = employeeManagementService.getAllShortEmployeesData().stream()
                .map(ShiftRowBO::new)
                .collect(Collectors.toSet());
        shiftEmployeeData.getItems().clear();
        shiftEmployeeData.getItems().addAll(shifts.stream().sorted(Comparator.comparing(ShiftRowBO::getEmployeeName)).toList());
    }

    private void prepareShiftRowTableWithData(Collection<ShiftRowBO> rowBOs) {
        var excludeIds = rowBOs.stream().map(ShiftRowBO::getEmployeeId).collect(Collectors.toSet());
        var employees = employeeManagementService.getAllShortEmployeesDataExcludeIds(excludeIds);
        var emptyRowsWithEmployee = employees.stream()
                .map(ShiftRowBO::new)
                .collect(Collectors.toSet());
        rowBOs.addAll(emptyRowsWithEmployee);
        rowBOs = rowBOs.stream()
                .sorted(Comparator.comparing(ShiftRowBO::getEmployeeName))
                .toList();
        shiftEmployeeData.getItems().clear();
        shiftEmployeeData.getItems().addAll(rowBOs);
    }

    private void initDateOnFirstOpen() {
        LocalDate initDate = LocalDateTime.now().getHour() < 7 ? LocalDate.now().minusDays(1) : LocalDate.now();
        datePicker.setValue(initDate);
    }

    private Long checkAndChangeStringToLong(TextField textField) {
        if (isNull(textField.getText()) || textField.getText().isBlank()) {
            return 0L;
        }
        try {
            return Long.valueOf(textField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Тільки цифрові значення");
            throw e;
        }
    }

    private String adjustLong(Long number) {
        return isNull(number) ? "0" : number.toString();
    }

    private Integer prepareTotalTime(LocalTime startShift, LocalTime endShift) {
        // Return 0 if either time is null or if they are equal
        if (isNull(startShift) || isNull(endShift) || startShift.equals(endShift)) {
            return 0;
        }

        var currentDate = datePicker.getValue();
        Integer totalTime = prepareWorkTotalTime(currentDate, startShift, endShift);

        // Ensure the result is never null
        return nonNull(totalTime) ? totalTime : 0;
    }

    private ShiftRowRequestBO toShiftRowRequestBO(ShiftRowBO shiftRowBO) throws Exception {
        var shiftRowRequest = new ShiftRowRequestBO();
        Integer totalWorkTime = prepareTotalTime(shiftRowBO.getStartShift(), shiftRowBO.getEndShift());

        if (totalWorkTime >= 0) {
            shiftRowRequest.setEmployeeId(shiftRowBO.getEmployeeId());
            shiftRowRequest.setEmployeeName(shiftRowBO.getEmployeeName());
            shiftRowRequest.setShiftRowId(shiftRowBO.getShiftRowId());
            shiftRowRequest.setShiftDate(datePicker.getValue());

            if (nonNull(shiftRowBO.getStartShift()) && nonNull(shiftRowBO.getEndShift())) {
                var currentDate = datePicker.getValue();
                LocalDateTime startShiftDateTime = LocalDateTime.of(currentDate, shiftRowBO.getStartShift());
                LocalDateTime endShiftDateTime = LocalDateTime.of(currentDate, shiftRowBO.getEndShift());

                if (startShiftDateTime.toLocalTime().isAfter(endShiftDateTime.toLocalTime())) {
                    endShiftDateTime = endShiftDateTime.plusDays(1);
                }

                shiftRowRequest.setStartShift(startShiftDateTime);
                shiftRowRequest.setEndShift(endShiftDateTime);
                shiftRowRequest.setTotalWorkTime(prepareWorkTotalTime(startShiftDateTime, endShiftDateTime));
            } else {
                shiftRowRequest.setStartShift(null);
                shiftRowRequest.setEndShift(null);
                shiftRowRequest.setTotalWorkTime(0);
            }
        }

        if (shiftRowRequest.getTotalWorkTime() < 0) {
            showAlert(Alert.AlertType.ERROR, "Невірно вказаний час",
                    String.format("Співробітник %s має не вірний час зміни", shiftRowRequest.getEmployeeName()));
            throw new Exception();
        }
        return shiftRowRequest;
    }
}
