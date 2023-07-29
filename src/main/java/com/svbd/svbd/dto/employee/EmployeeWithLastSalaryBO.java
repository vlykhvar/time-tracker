package com.svbd.svbd.dto.employee;

import java.math.BigDecimal;
import java.util.Objects;

public class EmployeeWithLastSalaryBO extends EmployeeShortBO {

    private String phoneNumber;
    private BigDecimal perHour;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getPerHour() {
        return perHour;
    }

    public void setPerHour(BigDecimal perHour) {
        this.perHour = perHour;
    }

}
