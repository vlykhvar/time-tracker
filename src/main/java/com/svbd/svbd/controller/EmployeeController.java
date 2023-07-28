package com.svbd.svbd.controller;

import com.svbd.svbd.enums.Pages;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.service.EmployeeManagementService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import static com.svbd.svbd.util.StageUtil.changeStage;

public class EmployeeController {

    private EmployeeManagementService employeeManagementService = new EmployeeManagementService();

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
    void saveEmployee(ActionEvent event) throws SQLException, IOException {
        var employee = new Employee();
        employee.setName(fullName.getText());
        employee.setPhoneNumber(phoneNumber.getText());
        var salary = new Salary();
        try {
            salary.setAnHour(new BigDecimal(perHour.getText()));
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }
        employee.getSalaries().add(salary);
        employeeManagementService.createEmployee(employee);
        changeStage((Stage) anchorPane.getScene().getWindow(), Pages.TABLE_EMPLOYEE);
    }
}
