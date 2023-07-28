package com.svbd.svbd.controller;

import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.service.EmployeeService;
import com.svbd.svbd.service.SalaryService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

public class EmployeeController {

    private EmployeeService employeeService = new EmployeeService();
    private SalaryService salaryService = new SalaryService();

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField fullName;

    @FXML
    private Label invalidDetails;

    @FXML
    private TextField perHour;

    @FXML
    private TextField phoneNumber;

    @FXML
    private Button saveButton;

    @FXML
    void saveEmployee(ActionEvent event) throws SQLException {
        var employee = new Employee();
        employee.setName(fullName.getText());
        employee.setPhoneNumber(phoneNumber.getText());
        var salary = new Salary();
        salary.setStartDate(LocalDate.now());
        try {
            salary.setAnHour(new BigDecimal(perHour.getText()));
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }
        var employeeId = employeeService.createEmployee(employee);
        salary.setEmployee(new Employee(employeeId));
        salaryService.createSalary(salary);
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }
}
