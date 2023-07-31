package com.svbd.svbd.enums;

import javafx.scene.control.Alert.AlertType;

import static javafx.scene.control.Alert.AlertType.ERROR;

public enum Exceptions {

    DATE_OVERLAPPING_EXCEPTION(ERROR, "Перекриття дат",
            "Дати заробітньої плати перекриваются"),
    START_DATE_AFTER_EXCEPTION(ERROR, "Не вірний проміжок часу",
            "Дата початку дії заробітної плати пілся кінця періоду"),
    NUMBER_VALUE_EXCEPTION(ERROR, "Не корректне число", "Не корректне число");

    private final AlertType alertType;
    private final String title;
    private final String text;

    Exceptions(AlertType alertType, String title, String text) {
        this.alertType = alertType;
        this.title = title;
        this.text = text;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
