package com.svbd.svbd.controller;

import com.svbd.svbd.controller.customfield.NumberField;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.dto.employee.EmployeeWithLastSalaryBO;
import com.svbd.svbd.service.EmployeeManagementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static jdk.internal.joptsimple.internal.Strings.EMPTY;

public class EmployeeTableController implements Initializable {

    private EmployeeManagementService employeeManagementService = new EmployeeManagementService();

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

    @FXML
    private NumberField perHour;

    @FXML
    private NumberField phoneNumber;

    @FXML
    private TextField fullName;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        prepareColumn();
    }


    @FXML
    void createEmployee(ActionEvent event) throws SQLException, IOException {
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
        fullName.setText(EMPTY);
        phoneNumber.setText(EMPTY);
        perHour.setText(EMPTY);
        prepareColumn();
    }

    private ObservableList<EmployeeWithLastSalaryBO> getEmployees() {
        ObservableList<EmployeeWithLastSalaryBO> characters = FXCollections.observableArrayList();
        characters.addAll(employeeManagementService.getEmployeesWithLastSalaryBO());
        return characters;
    }

    private void prepareColumn() {
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
                                        + "   " + employee.getPhoneNumber()); //TODO adjust editing employee
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
            }
        };

        var removeFactory
                = new Callback<TableColumn<EmployeeWithLastSalaryBO, String>, TableCell<EmployeeWithLastSalaryBO, String>>() {
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
                                var employee = getTableView().getItems().get(getIndex());
                                employeeManagementService.removeById(employee.getId());
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
}
