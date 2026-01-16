package com.svbd.svbd.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "employee")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Employee extends CreatedAtRemovedAt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToMany(mappedBy = "employee", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private final List<Salary> salaries = new ArrayList<>();

    @OneToMany(mappedBy = "employee", orphanRemoval = true, cascade = CascadeType.ALL)
    private final List<ShiftRow> shifts = new ArrayList<>();

    public Employee() {
    }

    public Employee(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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

    public List<Salary> getSalaries() {
        return salaries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(employeeId, employee.employeeId) && Objects.equals(name, employee.name) && Objects.equals(phoneNumber, employee.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, name, phoneNumber);
    }
}
