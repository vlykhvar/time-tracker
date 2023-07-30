package com.svbd.svbd.controller;

import com.svbd.svbd.controller.customfield.NumberField;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.dto.employee.EmployeeWithLastSalaryBO;
import com.svbd.svbd.service.EmployeeManagementService;
import com.svbd.svbd.util.DataHolder;
import com.svbd.svbd.util.StageUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static com.svbd.svbd.enums.Pages.EMPLOYEE_PROFILE;
import static jdk.internal.joptsimple.internal.Strings.EMPTY;

public class EmployeeTableController implements Initializable {

    private EmployeeManagementService employeeManagementService = new EmployeeManagementService();

    @FXML
    private Button buttonId;

    @FXML
    private TableView<EmployeeWithLastSalaryBO> employeeTable;

    @FXML
    private TableColumn<EmployeeWithLastSalaryBO, String> fullNameColumn;

    @FXML
    private TableColumn<EmployeeWithLastSalaryBO, Long> employeeIdColumn;

    @FXML
    private TableColumn<EmployeeWithLastSalaryBO, BigDecimal> perHourColumn;

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


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        prepareColumn();
        columnListener();
    }


    @FXML
    void createEmployee(ActionEvent event) throws SQLException, IOException {
        var employee = new Employee();
        employee.setName(fullName.getText());
        employee.setPhoneNumber(phoneNumber.getText());
        var salary = new Salary();
        try {
            salary.setAnHour(Long.valueOf(perHour.getText()));
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
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        perHourColumn.setCellValueFactory(new PropertyValueFactory<>("perHour"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        var removeFactory
                = new Callback<TableColumn<EmployeeWithLastSalaryBO, String>, TableCell<EmployeeWithLastSalaryBO, String>>() {
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
        remove.setCellFactory(removeFactory);
        employeeTable.setItems(getEmployees());
    }

    private void columnListener() {
        employeeTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                @SuppressWarnings("rawtypes")
                TablePosition pos = employeeTable.getSelectionModel().getSelectedCells().get(0);
                int row = pos.getRow();
                var employee = employeeTable.getItems().get(row);
                try {
                    DataHolder.getInstance().setData(employee.getId());
                    StageUtil.changeStage((Stage) buttonId.getScene().getWindow(),EMPLOYEE_PROFILE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
}
