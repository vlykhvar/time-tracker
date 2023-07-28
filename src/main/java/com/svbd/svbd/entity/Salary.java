package com.svbd.svbd.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Salary {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "SALARY_ID")
    private Long salaryId;

    @Column(name = "AN_HOUR")
    private BigDecimal anHour;

    @Column(name = "STARTDATE")
    private LocalDate startDate;

    @Column(name = "ENDDATE")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "EMPLOYEE_ID")
    private Employee employee;

    public Long getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(Long salaryId) {
        this.salaryId = salaryId;
    }

    public BigDecimal getAnHour() {
        return anHour;
    }

    public void setAnHour(BigDecimal anHour) {
        this.anHour = anHour;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employeeId) {
        this.employee = employeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Salary salary = (Salary) o;
        return Objects.equals(salaryId, salary.salaryId) && Objects.equals(employee, salary.employee) &&
                Objects.equals(anHour, salary.anHour) && Objects.equals(startDate, salary.endDate) &&
                Objects.equals(endDate, salary.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(salaryId, anHour, startDate, endDate, employee);
    }
}
