package com.svbd.svbd.util;

import com.svbd.svbd.enums.Exceptions;
import javafx.scene.control.Alert;

import static jdk.internal.joptsimple.internal.Strings.EMPTY;

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
}
