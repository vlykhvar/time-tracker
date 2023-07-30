package com.svbd.svbd.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Salary extends CreatedAtRemovedAt {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "SALARY_ID")
    private Long salaryId;

    @Column(name = "AN_HOUR")
    private Long anHour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "EMPLOYEE_ID")
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

    @Override
    public int hashCode() {
        return Objects.hash(salaryId, anHour, employee);
    }
}
