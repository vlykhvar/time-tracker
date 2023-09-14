package com.svbd.svbd.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "salary")
public class Salary {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    @Column(name = "salary_id")
    private Long salaryId;

    @Column(name = "an_hour")
    private Long anHour;

    @Column(name = "date_from", nullable = false)
    private LocalDate dateFrom;

    @Column(name = "date_to")
    private LocalDate dateTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id")
    private Employee employee;

    public Long getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(Long salaryId) {
        this.salaryId = salaryId;
    }

    public Long getAnHour() {
        return anHour;
    }

    public void setAnHour(Long anHour) {
        this.anHour = anHour;
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
                Objects.equals(anHour, salary.anHour);
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(salaryId, anHour, employee);
    }
}
