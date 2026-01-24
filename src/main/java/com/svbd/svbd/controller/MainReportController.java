package com.svbd.svbd.controller;

import com.svbd.svbd.dto.report.ReportRequest;
import com.svbd.svbd.enums.EReportType;
import com.svbd.svbd.exception.StartDateAfterEndDateException;
import com.svbd.svbd.service.ReportsService;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static com.svbd.svbd.enums.Exceptions.START_DATE_AFTER_EXCEPTION;
import static com.svbd.svbd.util.AlertUtil.showAlert;

@Component
public class MainReportController implements Initializable {

    @Autowired
    private ReportsService reportsService;

    @Autowired
    private HostServices hostServices;

    @FXML
    private DatePicker dateFrom;

    @FXML
    private DatePicker dateTo;

    @FXML
    private Button makeReport;

    @FXML
    void makeReport(ActionEvent event) throws Exception {
        if (dateFrom.getValue().isAfter(dateTo.getValue())) {
            showAlert(START_DATE_AFTER_EXCEPTION);
            throw new StartDateAfterEndDateException();
        }
        var request = new ReportRequest(dateFrom.getValue(), dateTo.getValue());
        try {
            var patch = reportsService.generateReport(EReportType.MONTHLY, request);
            File excelFile = new File(patch);
            hostServices.showDocument(excelFile.toURI().toURL().toExternalForm());
            var s = (Stage) makeReport.getScene().getWindow();
            s.close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Exception", e.getMessage());
        }
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
