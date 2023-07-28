package com.svbd.svbd.controller;

import com.svbd.svbd.Pages;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.service.EmployeeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeTableController implements Initializable {

    private EmployeeService employeeService = new EmployeeService();

    @FXML
    private Button buttonId;

    @FXML
    private TableView<Employee> emploeeTable;

    @FXML
    private TableColumn<Employee, String> fullnameCollumn;

    @FXML
    private TableColumn<Employee, Long> idCollumn;

    @FXML
    private TableColumn<Employee, BigDecimal> perHourColumn;

    @FXML
    private TableColumn<Employee, String> phoneNumberColumn;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        fullnameCollumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCollumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        perHourColumn.setCellValueFactory(new PropertyValueFactory<>("perHour"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emploeeTable.setItems(getEmployees());
    }


    @FXML
    void createEmployee(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Pages.CREATING_EMPLOYEE.getPagePath()));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ObservableList<Employee> getEmployees() {
        ObservableList<Employee> characters = FXCollections.observableArrayList();
        characters.addAll(employeeService.getAllEmployee());
        return characters;
    }
}
