package com.svbd.svbd.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;


public class EditEmployeeController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField id;

    @FXML
    private TextField fullName;

    @FXML
    private TextField perHour;

    @FXML
    private TextField phoneNumber;

    @FXML
    private Button saveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void editEmployee(ActionEvent event) {
    }

    private void isSalaryChanges() {
    }
}

