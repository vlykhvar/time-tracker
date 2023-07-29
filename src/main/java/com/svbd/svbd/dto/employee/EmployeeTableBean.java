package com.svbd.svbd.dto.employee;

import javafx.scene.control.ComboBox;

import java.util.HashSet;
import java.util.Set;

public class EmployeeTableBean {

    private Long id;
    private ComboBox<String> names;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ComboBox<String> getNames() {
        return names;
    }

    public void setNames(ComboBox<String> names) {
        this.names = names;
    }
}
