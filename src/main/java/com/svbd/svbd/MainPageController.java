package com.svbd.svbd;

import com.svbd.svbd.service.EmployeeService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;

public class MainPageController {

    private EmployeeService employeeService = new EmployeeService();

    @FXML
    private MenuItem about;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private MenuItem menuEmployee;

    @FXML
    private MenuItem menuQuit;

    @FXML
    void exit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void openDialog(ActionEvent event) {

    }

    @FXML
    void showEmployeeScene(ActionEvent event) {
        try {
            employeeService.createEmployee();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
