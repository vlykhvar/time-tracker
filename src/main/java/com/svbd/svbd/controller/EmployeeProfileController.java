package com.svbd.svbd.controller;

import com.svbd.svbd.controller.customfield.NumberField;
import com.svbd.svbd.dto.employee.EmployeeBO;
import com.svbd.svbd.dto.salary.SalaryBO;
import com.svbd.svbd.enums.Exceptions;
import com.svbd.svbd.exception.OverlapingDateException;
import com.svbd.svbd.service.EmployeeManagementService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.LongStringConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private TableColumn<SalaryBO, LocalDate> startDate;

    @FXML
    private TableColumn<SalaryBO, LocalDate> endDate;

    @FXML
    private TextField name;

    @FXML
    private NumberField phoneNumber;

    @FXML
    private Button save;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка завантаження", "Не вдалося завантажити дані профілю.");
        }
    }

    @FXML
    void saveEmployee() throws IOException {
        try {
            // Force any active cell to commit its edit before saving
            salaryTable.edit(-1, null);

            EmployeeBO employeeToSave = prepareEmployeeBO();
            employeeManagementService.updateEmployee(employeeToSave);
            Stage stage = (Stage) save.getScene().getWindow();
            stage.close();
        } catch (OverlapingDateException e) {
            showAlert(Exceptions.DATE_OVERLAPPING_EXCEPTION);
        }
    }

    private void populateTable(Collection<SalaryBO> salaries) {
        var sortedSalaries = new ArrayList<>(salaries);
        sortedSalaries.sort(Comparator.comparing(SalaryBO::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())));

        // Use FXCollections.observableArrayList and setItems for best practice
        salaryTable.setItems(FXCollections.observableArrayList(sortedSalaries));
        salaryTable.getItems().add(new SalaryBO()); // Add empty row for new entries
    }

    private void setupTableColumns() {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        perHour.setCellValueFactory(new PropertyValueFactory<>("anHour"));
        startDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
    }

    private void setupTableEditing() {
        // Use the standard TextFieldTableCell for simplicity and reliability
        perHour.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));
        perHour.setOnEditCommit(event -> {
            // Use getRowValue() to directly access the model object for the row
            SalaryBO salary = event.getRowValue();
            salary.setAnHour(event.getNewValue());
        });

        // DatePicker for Start Date
        startDate.setCellFactory(column -> new DatePickerTableCell());
        startDate.setOnEditCommit(event -> {
            SalaryBO salary = event.getRowValue();
            salary.setStartDate(event.getNewValue());
        });

        // DatePicker for End Date
        endDate.setCellFactory(column -> new DatePickerTableCell());
        endDate.setOnEditCommit(event -> {
            SalaryBO salary = event.getRowValue();
            salary.setEndDate(event.getNewValue());
        });
    }

    private EmployeeBO prepareEmployeeBO() {
        var employeeBO = new EmployeeBO();
        employeeBO.setId(Long.valueOf(employeeIdField.getText()));
        employeeBO.setPhoneNumber(phoneNumber.getText());
        employeeBO.setName(name.getText());
        var salaries = salaryTable.getItems().stream()
                .filter(salaryBO -> (nonNull(salaryBO.getAnHour()) && salaryBO.getAnHour() > 0 && nonNull(salaryBO.getStartDate()))
                        || nonNull(salaryBO.getId()))
                .peek(salaryBO -> salaryBO.setEmployeeId(employeeBO.getId()))
                .collect(Collectors.toSet());
        validateStartAndEndDate(salaries);
        employeeBO.getSalaries().addAll(salaries);
        return employeeBO;
    }

    private void validateStartAndEndDate(Collection<SalaryBO> salaryBOs) {
        for (var salary : salaryBOs) {
            if (nonNull(salary.getStartDate()) && nonNull(salary.getEndDate()) && salary.getStartDate().isAfter(salary.getEndDate())) {
                showAlert(START_DATE_AFTER_EXCEPTION);
                throw new IllegalArgumentException("Start date cannot be after end date.");
            }
        }
    }

    // Inner class for DatePicker cell
    private static class DatePickerTableCell extends TableCell<SalaryBO, LocalDate> {
        private final DatePicker datePicker;

        public DatePickerTableCell() {
            this.datePicker = new DatePicker();
            this.datePicker.setConverter(new StringConverter<>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

                @Override
                public String toString(LocalDate date) {
                    return date != null ? formatter.format(date) : "";
                }

                @Override
                public LocalDate fromString(String string) {
                    return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
                }
            });

            // Commit the value when a new date is chosen from the picker
            this.datePicker.setOnAction(event -> {
                if (isEditing()) {
                    commitEdit(datePicker.getValue());
                }
            });
            setAlignment(Pos.CENTER);
        }

        @Override
        protected void updateItem(LocalDate item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    datePicker.setValue(item);
                    setText(null);
                    setGraphic(datePicker);
                } else {
                    setText(datePicker.getConverter().toString(item));
                    setGraphic(null);
                }
            }
        }

        @Override
        public void startEdit() {
            super.startEdit();
            if (!isEmpty()) {
                datePicker.setValue(getItem());
                setText(null);
                setGraphic(datePicker);
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(datePicker.getConverter().toString(getItem()));
            setGraphic(null);
        }
    }
}
