package com.svbd.svbd.dto.employee;

import com.svbd.svbd.dto.salary.SalaryBO;

import java.util.HashSet;
import java.util.Set;

public class EmployeeBO extends EmployeeShortBO {

    private String phoneNumber;

    private final Set<SalaryBO> salaries = new HashSet<>();

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<SalaryBO> getSalaries() {
        return salaries;
    }
}
