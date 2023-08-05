package com.svbd.svbd.util;

import com.svbd.svbd.enums.Exceptions;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import static com.svbd.svbd.util.ConstantUtil.EMPTY;


public final class AlertUtil {

    private AlertUtil() {
    }

    public static void showAlert(Exceptions exception) {
        Alert alert = new Alert(exception.getAlertType());
        alert.setTitle(exception.getTitle());
        alert.setHeaderText(exception.getText());
        alert.setContentText(EMPTY);
        alert.showAndWait();
    }

    public static void showAlert(Alert.AlertType type, String title, String text) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.setContentText(EMPTY);
        alert.showAndWait();
    }

    public static Boolean showAlertWithButtonYesAndNo(Alert.AlertType type, String title, String text) {
        var yes = new ButtonType("YES", ButtonBar.ButtonData.YES);
        var no = new ButtonType("NO", ButtonBar.ButtonData.NO);
        Alert alert = new Alert(type, text, yes, no);
        alert.setTitle(title);
        alert.setHeaderText(null);
        var result = alert.showAndWait();
        return result.orElse(no) == yes;
    }
}
