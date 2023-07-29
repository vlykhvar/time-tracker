package com.svbd.svbd.util;

import javafx.scene.control.Alert;

import static jdk.internal.joptsimple.internal.Strings.EMPTY;

public final class AlertUtil {

    private AlertUtil() {
    }

    public static void showAlert(Alert.AlertType type, String title, String text) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.setContentText(EMPTY);
        alert.showAndWait();
    }
}
