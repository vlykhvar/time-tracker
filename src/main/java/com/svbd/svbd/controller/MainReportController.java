package com.svbd.svbd.controller;

import com.svbd.svbd.dto.report.MainReport;
import com.svbd.svbd.exception.StartDateAfterEndDateException;
import com.svbd.svbd.service.ReportsService;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static com.svbd.svbd.enums.Exceptions.START_DATE_AFTER_EXCEPTION;
import static com.svbd.svbd.util.AlertUtil.showAlert;

public class MainReportController extends Application implements Initializable {

    private final ReportsService reportsService = new ReportsService();

    @FXML
    private DatePicker dateFrom;

    @FXML
    private DatePicker dateTo;

    @FXML
    private Button makeReport;

    @FXML
    void makeReport(ActionEvent event) throws IOException {
        if (dateFrom.getValue().isAfter(dateTo.getValue())) {
            showAlert(START_DATE_AFTER_EXCEPTION);
            throw new StartDateAfterEndDateException();
        }
        var request = new MainReport(dateFrom.getValue(), dateTo.getValue());
        var patch = reportsService.generateMainRepost(request);
        File excelFile = new File(patch);
        getHostServices().showDocument(excelFile.toURI().toURL().toExternalForm());
        var s = (Stage) makeReport.getScene().getWindow();
        s.close();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LocalDate initial = LocalDate.now();
        LocalDate start = initial.withDayOfMonth(1);
        LocalDate end = initial.withDayOfMonth(initial.getMonth().length(initial.isLeapYear()));
        dateFrom.setValue(start);
        dateTo.setValue(end);
    }
}
