package com.svbd.svbd.controller;

import com.svbd.svbd.Application;
import com.svbd.svbd.controller.customfield.NumberField;
import com.svbd.svbd.dto.employee.EmployeeWithLastSalaryBO;
import com.svbd.svbd.dto.settings.DinnerSettingBO;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.exception.OverlapingDateException;
import com.svbd.svbd.service.EmployeeManagementService;
import com.svbd.svbd.service.SettingsManagementService;
import com.svbd.svbd.service.ShiftManagementService;
import com.svbd.svbd.util.DataHolder;
import com.svbd.svbd.util.DateTimeUtil;
import com.svbd.svbd.util.StageUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.LongStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.svbd.svbd.enums.Exceptions.NUMBER_VALUE_EXCEPTION;
import static com.svbd.svbd.enums.Pages.EMPLOYEE_PROFILE;
import static com.svbd.svbd.util.AlertUtil.showAlert;
import static com.svbd.svbd.util.AlertUtil.showAlertWithButtonYesAndNo;
import static com.svbd.svbd.util.ConstantUtil.EMPTY;
import static com.svbd.svbd.util.DateTimeUtil.parseLocalDate;
import static java.util.Objects.isNull;

public class SettingsController extends Application implements Initializable {

    private final EmployeeManagementService employeeManagementService = new EmployeeManagementService();
    private final SettingsManagementService settingsManagementService = new SettingsManagementService();
    private final ShiftManagementService shiftManagementService = new ShiftManagementService();

    @FXML
    private Button buttonId;

    @FXML
    private TableView<EmployeeWithLastSalaryBO> employeeTable;

    @FXML
    private TableColumn<EmployeeWithLastSalaryBO, String> fullNameColumn;

    @FXML
    private TableColumn<EmployeeWithLastSalaryBO, Long> employeeIdColumn;

    @FXML
    private TableColumn<EmployeeWithLastSalaryBO, String> phoneNumberColumn;

    @FXML
    private TableColumn<EmployeeWithLastSalaryBO, String> remove;

    @FXML
    private NumberField perHour;

    @FXML
    private NumberField phoneNumber;

    @FXML
    private TextField fullName;

    @FXML
    private TextField companyNameField;

    @FXML
    private TableColumn<DinnerSettingBO, String> dinnerEndDateColumn;

    @FXML
    private TableColumn<DinnerSettingBO, String> dinnerStartDateColumn;

    @FXML
    private TableColumn<DinnerSettingBO, Long> dinnerPrice;

    @FXML
    private TableColumn<DinnerSettingBO, Long> dinnerIdColumn;

    @FXML
    private TableColumn<DinnerSettingBO, String> dinnerRemove;

    @FXML
    private DatePicker dinnerStartDate;

    @FXML
    private TableView<DinnerSettingBO> dinnerTable;

    @FXML
    private PasswordField doubleCheckField;

    @FXML
    private Button saveCompanySettings;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private NumberField price;

    @FXML
    private Button saveDinnerSettings;

    @FXML
    private Button savePassword;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareEmployeeColumn();
        editDinnerSettingsTable();
        prepareDinnerColumn();
        prepareCompanySettings();
        setEmployeeColumnListener();
    }

    @FXML
    void createEmployee(ActionEvent event) {
        var employee = new Employee();
        employee.setName(fullName.getText());
        employee.setPhoneNumber(phoneNumber.getText());
        var salary = new Salary();
        try {
            salary.setAnHour(Long.valueOf(perHour.getText()));
        } catch (NumberFormatException e) {
            showAlert(NUMBER_VALUE_EXCEPTION);
            throw new NumberFormatException();
        }
        employee.getSalaries().add(salary);
        employeeManagementService.createEmployee(employee);
        fullName.setText(EMPTY);
        phoneNumber.setText(EMPTY);
        perHour.setText(EMPTY);
        prepareEmployeeColumn();
    }

    @FXML
    public void savePassword(ActionEvent event) {
        var companySettings = settingsManagementService.getCompanySettings();
        if (!companySettings.getCompanyPassword().equals(oldPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Невірно вказаний старий пароль",
                    "Невірно вказаний старий пароль");
            oldPasswordField.setStyle("-fx-border-color:red;");
            return;
        } else {
            oldPasswordField.setStyle(null);
        }
        var newPassword = newPasswordField.getText();
        if (isNull(newPassword) || !newPassword.matches("\\S+") || newPassword.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Невірно вказаний новий пароль",
                    "Невірно новий вказаний пароль");
            newPasswordField.setStyle("-fx-border-color:red;");
            return;
        } else {
            newPasswordField.setStyle(null);
        }
        if (!newPassword.equals(doubleCheckField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Паролі не співпадають",
                    "Паролі не співпадають.");
            doubleCheckField.setStyle("-fx-border-color:red;");
            return;
        } else {
            doubleCheckField.setStyle(null);
        }

        settingsManagementService.savePassword(newPassword);
    }

    @FXML
    void saveCompanySettings(ActionEvent event) {
        var companyName = companyNameField.getText();
        if (isNull(companyName) || companyName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Невірна назва компанії", "Назва компанії не може бути пуста");
            return;
        }
        settingsManagementService.saveCompanyName(companyNameField.getText());
    }

    @FXML
    void saveDinnerSetting() {
        var dinnerBO = new DinnerSettingBO();
        dinnerBO.setPrice(Long.valueOf(price.getText()));
        var startDate = dinnerStartDate.getValue();
        dinnerBO.setDateFrom(DateTimeUtil.formatDateForShowing(startDate));
        try {
            settingsManagementService.createDinnerSettings(dinnerBO);
            shiftManagementService.updateShiftsByDinnerSettings(dinnerBO);
        } catch (OverlapingDateException e) {
            showAlert(Alert.AlertType.ERROR, "Періоди накладаются", "Періоди перекриваются");
        }
        prepareDinnerColumn();
    }

    @FXML
    void saveDinnerSettingTable() {
        var dinnerSettingsBO = new ArrayList<>(dinnerTable.getItems());
        try {
            settingsManagementService.updateDinnerSettings(dinnerSettingsBO);
        } catch (OverlapingDateException e) {
            showAlert(Alert.AlertType.ERROR, "Не корректні дати", "Дата кінця зміни не може бути після початку");
        }
        prepareDinnerColumn();
        dinnerTable.getItems().forEach(shiftManagementService::updateShiftsByDinnerSettings);
    }

    /*Private methods*/

    private void editDinnerSettingsTable() {
        dinnerPrice.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));
        dinnerPrice.setOnEditCommit(event -> {
                    var dinnerSettingBO = (DinnerSettingBO) event.getTableView().getItems().get(
                            event.getTablePosition().getRow());
                    dinnerSettingBO.setPrice(event.getNewValue());
                }
        );

        dinnerStartDateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        dinnerStartDateColumn.setOnEditCommit(event -> {
                    var dinnerSettingBO = (DinnerSettingBO) event.getTableView().getItems().get(
                            event.getTablePosition().getRow());
                    try {
                        parseLocalDate(event.getNewValue());
                        dinnerSettingBO.setDateFrom(event.getNewValue());
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Невірний формат дати",
                                "Дата повина мати форма dd.mm.yyyy");
                    }
                }
        );
        dinnerEndDateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        dinnerEndDateColumn.setOnEditCommit(event -> {
                    var dinnerSettingBO = (DinnerSettingBO) event.getTableView().getItems().get(
                            event.getTablePosition().getRow());
                    try {
                        parseLocalDate(event.getNewValue());
                        dinnerSettingBO.setDateTo(event.getNewValue());
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Невірний формат дати",
                                "Дата повина мати форма dd.mm.yyyy");
                    }
                }
        );
    }

    private void prepareDinnerColumn() {
        dinnerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dinnerPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        dinnerStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateFrom"));
        dinnerEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTo"));
        var removeFactory
                = new Callback<TableColumn<DinnerSettingBO, String>,
                TableCell<DinnerSettingBO, String>>() {
            @Override
            @SuppressWarnings("all")
            public TableCell call(final TableColumn<DinnerSettingBO, String> param) {
                return new TableCell<DinnerSettingBO, String>() {
                    final Button btn = new Button("Видалити");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                var dinnerSetting = getTableView().getItems().get(getIndex());
                                var result = showAlertWithButtonYesAndNo(Alert.AlertType.WARNING,
                                        "Видалення",
                                        String.format("Ви бажаєте видалити?"));
                                if (result) {
                                    settingsManagementService.removeDinnerSettingById(dinnerSetting.getId());
                                    initialize(null, null);
                                }
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
            }
        };
        dinnerRemove.setCellFactory(removeFactory);
        dinnerTable.getItems().clear();
        dinnerTable.getItems().addAll(getDinnerSettings());

    }

    private List<DinnerSettingBO> getDinnerSettings() {
        return settingsManagementService.getDinnerSettings();
    }

    private ObservableList<EmployeeWithLastSalaryBO> getEmployees() {
        ObservableList<EmployeeWithLastSalaryBO> characters = FXCollections.observableArrayList();
        characters.addAll(employeeManagementService.getEmployeesWithLastSalaryBO());
        return characters;
    }

    private void prepareEmployeeColumn() {
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        var removeFactory = new Callback<TableColumn<EmployeeWithLastSalaryBO, String>,
                TableCell<EmployeeWithLastSalaryBO, String>>() {
            @Override
            @SuppressWarnings("all")
            public TableCell call(final TableColumn<EmployeeWithLastSalaryBO, String> param) {
                return new TableCell<EmployeeWithLastSalaryBO, String>() {
                    final Button btn = new Button("Видалити");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                var employee = getTableView().getItems().get(getIndex());
                                var result = showAlertWithButtonYesAndNo(Alert.AlertType.WARNING,
                                        "Видалення співробітника",
                                        String.format("Ви бажаєте видалити співробітника %s?", employee.getName()));
                                if (result) {
                                    employeeManagementService.removeById(employee.getId());
                                    initialize(null, null);
                                }
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
            }
        };
        remove.setCellFactory(removeFactory);
        employeeTable.setItems(getEmployees());
    }

    private void setEmployeeColumnListener() {
        employeeTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                @SuppressWarnings("rawtypes")
                TablePosition pos = employeeTable.getSelectionModel().getSelectedCells().get(0);
                int row = pos.getRow();
                var employee = employeeTable.getItems().get(row);
                try {
                    DataHolder.getInstance().setData(employee.getId());
                    StageUtil.showStage(EMPLOYEE_PROFILE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    private void prepareCompanySettings() {
        var companySettings = settingsManagementService.getCompanySettings();
        companyNameField.setText(companySettings.getCompanyName());
    }
}
