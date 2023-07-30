package com.svbd.svbd.repository.projection;

public record SalaryEmployeeProjection(Long employeeId, Long anHour) {

    public SalaryEmployeeProjection(Long employeeId, Long anHour) {
        this.employeeId = employeeId;
        this.anHour = anHour;
    }
}
