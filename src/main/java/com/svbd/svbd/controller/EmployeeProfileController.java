package com.svbd.svbd.controller;

import com.svbd.svbd.controller.customfield.NumberField;
import com.svbd.svbd.dto.employee.EmployeeBO;
import com.svbd.svbd.dto.salary.SalaryBO;
import com.svbd.svbd.enums.Exceptions;
import com.svbd.svbd.enums.Pages;
import com.svbd.svbd.exception.OverlapingDateException;
import com.svbd.svbd.exception.StartDateAfterEndDateException;
import com.svbd.svbd.service.EmployeeManagementService;
import com.svbd.svbd.util.DataHolder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.converter.LongStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.svbd.svbd.enums.Exceptions.START_DATE_AFTER_EXCEPTION;
import static com.svbd.svbd.util.AlertUtil.showAlert;
import static com.svbd.svbd.util.DateTimeUtil.parseLocalDate;
import static com.svbd.svbd.util.StageUtil.changeStage;
import static java.util.Objects.nonNull;

public class EmployeeProfileController implements Initializable {

    private final EmployeeManagementService employeeManagementService = new EmployeeManagementService();

    @FXML
    private AnchorPane anchorPane;

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
        var employeeId = (Long) DataHolder.getInstance().getData();
        try {
            var employee = employeeManagementService.getEmployee(employeeId);
            employeeIdField.setText(employee.getId().toString());
            phoneNumber.setText(employee.getPhoneNumber());
            name.setText(employee.getName());
            prepareTable(employee.getSalaries());
            editTable();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "CRITICAL ERROR", "ERROR");
        }
    }

    @FXML
    void saveEmployee() throws IOException {
        try {
            employeeManagementService.updateEmployee(prepareEmployeeBO());
            changeStage((Stage) save.getScene().getWindow(), Pages.TABLE_EMPLOYEE);
        } catch (OverlapingDateException e) {
            showAlert(Exceptions.DATE_OVERLAPPING_EXCEPTION);
        }
    }

    private void prepareTable(Collection<SalaryBO> salaries) {
        var sortedSalaries = salaries.stream()
                .sorted(Comparator.comparing(SalaryBO::getStartDate))
                .toList();
        salaryTable.getItems().addAll(sortedSalaries);
        salaryTable.getItems().add(new SalaryBO());
        id.setCellValueFactory(new PropertyValueFactory<>("salaryId"));
        perHour.setCellValueFactory(new PropertyValueFactory<>("anHour"));
        startDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
    }

    private void editTable() {
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
                            event.getTablePosition().getRow());try {
                      parseLocalDate(event.getNewValue());
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
                        parseLocalDate(event.getNewValue());
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
            var startDate = parseLocalDate(salary.getStartDate());
            var endDate = parseLocalDate(salary.getEndDate());
            if (nonNull(endDate) && startDate.isAfter(endDate)) {
                showAlert(START_DATE_AFTER_EXCEPTION);
                throw new StartDateAfterEndDateException();
            }
        }
    }
}