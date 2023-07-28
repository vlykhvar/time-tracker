package com.svbd.svbd.controller;

import com.svbd.svbd.dto.shift.row.ShiftRowBO;
import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.service.ShiftManagementService;
import com.svbd.svbd.service.ShiftService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.LocalDateTimeStringConverter;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.ResourceBundle;

import static com.svbd.svbd.enums.Pages.TABLE_EMPLOYEE;
import static com.svbd.svbd.util.StageUtil.showStage;
import static java.util.Objects.isNull;

public class MainPageController implements Initializable {

    private final ShiftService service = new ShiftService();
    private final ShiftManagementService shiftManagementService = new ShiftManagementService();

    @FXML
    private MenuItem about;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private MenuItem menuEmployee;

    @FXML
    private MenuItem menuQuit;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TableColumn<ShiftRowBO, String> employeeName;

    @FXML
    private TableColumn<ShiftRowBO, LocalDateTime> endEmployeeShift;

    @FXML
    private Button saveButton;

    @FXML
    private TableView<ShiftRowBO> shitEmployeeData;

    @FXML
    private TableColumn<ShiftRowBO, Long> employeeId;

    @FXML
    private TableColumn<ShiftRowBO, LocalDateTime> startEmployeeShift;

    @FXML
    private TableColumn<ShiftRowBO, Long> shiftRowId;


    @FXML
    void exit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void openDialog(ActionEvent event) {

    }

    @FXML
    void showEmployeeScene(ActionEvent event) throws IOException {
        showStage(TABLE_EMPLOYEE);
    }

    @FXML
    void saveShift(ActionEvent event) {
        var shift = new Shift();
        shift.setShiftDate(datePicker.valueProperty().get());
        shiftManagementService.creatOrUpdate(shift);
        prepareData(shitEmployeeData);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());
        prepareData(shitEmployeeData);
        datePicker.setOnAction((event) -> {
            prepareData(shitEmployeeData);
        });
        editTable();
    }

    private void editTable() {
        employeeName.setCellFactory(TextFieldTableCell.<ShiftRowBO>forTableColumn());
        employeeName.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<ShiftRowBO, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<ShiftRowBO, String> t) {
                        ((ShiftRowBO) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setEmployeeName(t.getNewValue());
                    }
                }
        );
        startEmployeeShift.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateTimeStringConverter()));
        startEmployeeShift.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<ShiftRowBO, LocalDateTime>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<ShiftRowBO, LocalDateTime> t) {
                        ((ShiftRowBO) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setStartShift(t.getNewValue());
                    }
                }
        );
        endEmployeeShift.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateTimeStringConverter()));
        endEmployeeShift.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<ShiftRowBO, LocalDateTime>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<ShiftRowBO, LocalDateTime> t) {
                        ((ShiftRowBO) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setEndShift(t.getNewValue());
                       var shiftRowBO = ((ShiftRowBO) t.getTableView().getItems().get(
                                t.getTablePosition().getRow()));


                    }
                }
        );
    }

    private void prepareData(TableView<ShiftRowBO> shitEmployeeData) {
        var shift = shiftManagementService.getShiftByDate(datePicker.getValue());
        if (isNull(shift) || shift.getRows().isEmpty()) {
            shitEmployeeData.getItems().clear();
            shitEmployeeData.getItems().add(new ShiftRowBO());
        } else {
            prepareShitRowTable(shift.getRows());
        }
    }

    private void prepareShitRowTable(Collection<ShiftRowBO> rowBOs) {
        shitEmployeeData.getItems().clear();
        employeeName.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        employeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        startEmployeeShift.setCellValueFactory(new PropertyValueFactory<>("startShift"));
        endEmployeeShift.setCellValueFactory(new PropertyValueFactory<>("endShift"));
        shiftRowId.setCellValueFactory(new PropertyValueFactory<>("shiftRowId"));
        shitEmployeeData.getItems().addAll(rowBOs);
        shitEmployeeData.getItems().add(new ShiftRowBO());
    }
}
