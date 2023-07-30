package com.svbd.svbd.dto.salary;

import java.util.Objects;

public class SalaryBO {

    private Long id;
    private Long employeeId;
    private Long anHour;
    private String startDate;
    private String endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnHour() {
        return anHour;
    }

    public void setAnHour(Long anHour) {
        this.anHour = anHour;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalaryBO salaryBO = (SalaryBO) o;
        return Objects.equals(id, salaryBO.id) && Objects.equals(employeeId, salaryBO.employeeId) && Objects.equals(anHour, salaryBO.anHour) && Objects.equals(startDate, salaryBO.startDate) && Objects.equals(endDate, salaryBO.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, employeeId, anHour, startDate, endDate);
    }
}
