package com.svbd.svbd.controller.customfield;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import static com.svbd.svbd.util.ConstantUtil.NUMBER_REGEX;

public class NumberField extends TextField {

    public NumberField() {
        initSpellListener();
    }

    public final void initSpellListener() {
        this.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue.isEmpty() || newValue.matches(NUMBER_REGEX)) {
                this.setText(newValue);
            } else {
                if (newValue.length() == 2 && newValue.startsWith("0") &&
                        newValue.replaceFirst("0", "").matches(NUMBER_REGEX)) {
                    newValue = newValue.replaceFirst("0", "");
                    this.setText(newValue);
                } else {
                    this.setText(oldValue);
                }}});
        }
    }
