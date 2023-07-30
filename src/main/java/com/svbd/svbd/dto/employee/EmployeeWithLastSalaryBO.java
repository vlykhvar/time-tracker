package com.svbd.svbd.dto.employee;

public class EmployeeWithLastSalaryBO extends EmployeeShortBO {

    private String phoneNumber;
    private Long perHour;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getPerHour() {
        return perHour;
    }

    public void setPerHour(Long perHour) {
        this.perHour = perHour;
    }

}
