package com.svbd.svbd.dto.employee;

import java.math.BigDecimal;
import java.util.Objects;

public class EmployeeWithLastSalaryBO {

    private Long id;
    private String name;
    private String phoneNumber;
    private BigDecimal perHour;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeWithLastSalaryBO that = (EmployeeWithLastSalaryBO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(perHour, that.perHour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phoneNumber, perHour);
    }
}
