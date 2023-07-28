package com.svbd.svbd.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import static com.svbd.svbd.Pages.TABLE_EMPLOYEE;

public class MainPageController {

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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(TABLE_EMPLOYEE.getPagePath()));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.show();
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

}
