package com.svbd.svbd.controller;

import com.svbd.svbd.controller.customfield.NumberField;
import com.svbd.svbd.dto.employee.EmployeeBO;
import com.svbd.svbd.dto.salary.SalaryBO;
import com.svbd.svbd.enums.Exceptions;
import com.svbd.svbd.exception.OverlapingDateException;
import com.svbd.svbd.service.EmployeeManagementService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.LongStringConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.svbd.svbd.enums.Exceptions.START_DATE_AFTER_EXCEPTION;
import static com.svbd.svbd.util.AlertUtil.showAlert;
import static java.util.Objects.nonNull;

@Component
public class EmployeeProfileController implements Initializable {

    private final EmployeeManagementService employeeManagementService;

    public EmployeeProfileController(EmployeeManagementService employeeManagementService) {
        this.employeeManagementService = employeeManagementService;
    }

    @FXML
    private TextField employeeIdField;

    @FXML
    private TableView<SalaryBO> salaryTable;

    @FXML
    private TableColumn<SalaryBO, Long> id;

    @FXML
    private TableColumn<SalaryBO, Long> perHour;

    @FXML
    private TableColumn<SalaryBO, String> startDate;

    @FXML
    private TableColumn<SalaryBO, String> endDate;

    @FXML
    private TextField name;

    @FXML
    private NumberField phoneNumber;

    @FXML
    private Button save;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Настройка колонок и обработчиков редактирования
        // не зависит от конкретного сотрудника и выполняется один раз
        setupTableColumns();
        setupTableEditing();
    }


    public void loadEmployeeData(Long employeeId) {
        try {
            var employee = employeeManagementService.getEmployee(employeeId);
            employeeIdField.setText(employee.getId().toString());
            phoneNumber.setText(employee.getPhoneNumber());
            name.setText(employee.getName());
            populateTable(employee.getSalaries());
        } catch (Exception e) { // Ловим более конкретные исключения
            showAlert(Alert.AlertType.ERROR, "Помилка завантаження", "Не вдалося завантажити дані профілю.");
        }
    }

    @FXML
    void saveEmployee() throws IOException {
        try {
            EmployeeBO employeeToSave = prepareEmployeeBO();
            employeeManagementService.updateEmployee(employeeToSave);

            // Правильный способ закрыть окно изнутри контроллера
            Stage stage = (Stage) save.getScene().getWindow();
            stage.close();
        } catch (OverlapingDateException e) {
            showAlert(Exceptions.DATE_OVERLAPPING_EXCEPTION);
        }
    }

    private void populateTable(Collection<SalaryBO> salaries) {
        var sortedSalaries = salaries.stream()
                .sorted(Comparator.comparing(salary ->
                        LocalDate.parse(salary.getStartDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))))
                .toList();
        salaryTable.getItems().clear();
        salaryTable.getItems().addAll(sortedSalaries);
        salaryTable.getItems().add(new SalaryBO());
    }

    private void setupTableColumns() {
        id.setCellValueFactory(new PropertyValueFactory<>("salaryId"));
        perHour.setCellValueFactory(new PropertyValueFactory<>("anHour"));
        startDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
    }

    private void setupTableEditing() {
        perHour.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));
        perHour.setOnEditCommit(event -> {
                    var salary = (SalaryBO) event.getTableView().getItems().get(
                            event.getTablePosition().getRow());
                    salary.setAnHour(event.getNewValue());
                }
        );

        startDate.setCellFactory(TextFieldTableCell.forTableColumn());
        startDate.setOnEditCommit(event -> {
                    var salary = (SalaryBO) event.getTableView().getItems().get(
                            event.getTablePosition().getRow());
                    try {
                        LocalDate.parse(event.getNewValue(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        salary.setStartDate(event.getNewValue());
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Невірний формат дати",
                                "Дата повина мати форма dd.mm.yyyy");
                    }
                }
        );
        endDate.setCellFactory(TextFieldTableCell.forTableColumn());
        endDate.setOnEditCommit(event -> {
                    var salary = (SalaryBO) event.getTableView().getItems().get(
                            event.getTablePosition().getRow());
                    try {
                        LocalDate.parse(event.getNewValue(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        salary.setEndDate(event.getNewValue());
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Невірний формат дати",
                                "Дата повина мати форма dd.mm.yyyy");
                    }
                }
        );
    }

    private EmployeeBO prepareEmployeeBO() {
        var employeeBO = new EmployeeBO();
        employeeBO.setId(Long.valueOf(employeeIdField.getText()));
        employeeBO.setPhoneNumber(phoneNumber.getText());
        employeeBO.setName(name.getText());
        var salaries = salaryTable.getItems().stream()
                .filter(salaryBO -> (nonNull(salaryBO.getAnHour()) && nonNull(salaryBO.getStartDate()))
                        || nonNull(salaryBO.getId()))
                .peek(salaryBO -> salaryBO.setEmployeeId(employeeBO.getId()))
                .collect(Collectors.toSet());
        validateStartAndEndDate(salaries);
        employeeBO.getSalaries().addAll(salaries);
        return employeeBO;
    }

    private void validateStartAndEndDate(Collection<SalaryBO> salaryBOs) {
        for (var salary : salaryBOs) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate start = LocalDate.parse(salary.getStartDate(), formatter);
            LocalDate end = nonNull(salary.getEndDate()) ? LocalDate.parse(salary.getEndDate(), formatter) : null;

            if (nonNull(end) && start.isAfter(end)) {
                showAlert(START_DATE_AFTER_EXCEPTION);
                throw new IllegalArgumentException("Start date cannot be after end date.");
            }
        }
    }
}