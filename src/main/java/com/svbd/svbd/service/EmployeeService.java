package com.svbd.svbd.service;

import com.svbd.svbd.entity.employees.Employee;
import com.svbd.svbd.settings.H2Embedded;

import java.sql.SQLException;
import java.util.List;

import static java.util.Collections.emptyList;

public class EmployeeService {

    private static final String TABLE_NAME = "employees";
    private H2Embedded db = new H2Embedded();

    public void createEmployee() throws SQLException {
        var query = "INSERT INTO " + TABLE_NAME + " (name, phone_number, an_hour) VALUES('test', 'test', 22.3)";
        db.insertQuery(query);
    }

    public List<Employee> getAllEmployee() {
        return emptyList();
    }
}
