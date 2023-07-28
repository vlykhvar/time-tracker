package com.svbd.svbd.controller;

import com.svbd.svbd.enums.Pages;
import com.svbd.svbd.dto.employee.EmployeeWithLastSalaryBO;
import com.svbd.svbd.service.EmployeeManagementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeTableController implements Initializable {

    private EmployeeManagementService employeeService = new EmployeeManagementService();

    @FXML
    private Button buttonId;

    @FXML
    private TableView<EmployeeWithLastSalaryBO> emploeeTable;

    @FXML
    private TableColumn<EmployeeWithLastSalaryBO, String> fullnameCollumn;

    @FXML
    private TableColumn<EmployeeWithLastSalaryBO, Long> idCollumn;

    @FXML
    private TableColumn<EmployeeWithLastSalaryBO, BigDecimal> perHourColumn;

    @FXML
    private TableColumn<EmployeeWithLastSalaryBO, String> phoneNumberColumn;

    @FXML
    private TableColumn<EmployeeWithLastSalaryBO, String> remove;

    @FXML
    private TableColumn<EmployeeWithLastSalaryBO, String> action;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        fullnameCollumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCollumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        perHourColumn.setCellValueFactory(new PropertyValueFactory<>("perHour"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        Callback<TableColumn<EmployeeWithLastSalaryBO, String>, TableCell<EmployeeWithLastSalaryBO, String>> cellFactory
                = new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<EmployeeWithLastSalaryBO, String> param) {
                        return new TableCell<EmployeeWithLastSalaryBO, String>() {
                            final Button btn = new Button("Змінити");
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        EmployeeWithLastSalaryBO employee = getTableView().getItems().get(getIndex());
                                        System.out.println(employee.getId()
                                                + "   " + employee.getPhoneNumber());
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                    }
                };

        Callback<TableColumn<EmployeeWithLastSalaryBO, String>, TableCell<EmployeeWithLastSalaryBO, String>> removeFactory
                = new Callback<>() {
            @Override
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
                                EmployeeWithLastSalaryBO employee = getTableView().getItems().get(getIndex());
                                employeeService.removeById(employee.getId());
                                initialize(null, null);
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
            }
        };
        action.setCellFactory(cellFactory);
        remove.setCellFactory(removeFactory);
        emploeeTable.setItems(getEmployees());
    }


    @FXML
    void createEmployee(ActionEvent event) {
        try {
            var fxmlLoader = new FXMLLoader(getClass().getResource(Pages.CREATING_EMPLOYEE.getPagePath()));
            Parent root = fxmlLoader.load();
            var stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            var currentStage = (Stage) buttonId.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ObservableList<EmployeeWithLastSalaryBO> getEmployees() {
        ObservableList<EmployeeWithLastSalaryBO> characters = FXCollections.observableArrayList();
        characters.addAll(employeeService.getEmployeesWithLastSalaryBO());
        return characters;
    }
}
