package com.svbd.svbd.converter;

import com.svbd.svbd.dto.employee.EmployeeBO;
import com.svbd.svbd.dto.employee.EmployeeShortBO;
import com.svbd.svbd.dto.employee.EmployeeWithLastSalaryBO;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.repository.projection.EmployeeShortProjection;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.svbd.svbd.converter.SalaryConverter.toSalaries;
import static com.svbd.svbd.converter.SalaryConverter.toSalaryBOs;

public final class EmployeeConverter {

    private EmployeeConverter() {
    }

    public static EmployeeWithLastSalaryBO toEmployeeWithLastSalaryBO(Employee employee) {
        var employeeWithLastSalary = new EmployeeWithLastSalaryBO();
        employeeWithLastSalary.setId(employee.getEmployeeId());
        employeeWithLastSalary.setName(employee.getName());
        employeeWithLastSalary.setPhoneNumber(employee.getPhoneNumber());
        return employeeWithLastSalary;
    }

    public static Set<EmployeeWithLastSalaryBO> toEmployeeWithLastSalaryBOs(Collection<Employee> employees) {
        return employees.stream()
                .map(EmployeeConverter::toEmployeeWithLastSalaryBO)
                .collect(Collectors.toSet());
    }

    public static EmployeeShortBO toEmployeeShortBO(EmployeeShortProjection projection) {
        var employeeShortBO = new EmployeeShortBO();
        employeeShortBO.setId(projection.getId());
        employeeShortBO.setName(projection.getName());
        return employeeShortBO;
    }

    public static Set<EmployeeShortBO> toEmployeeShortBO(Collection<EmployeeShortProjection> projections) {
        return projections.stream()
                .map(EmployeeConverter::toEmployeeShortBO)
                .collect(Collectors.toSet());
    }

    public static EmployeeBO toEmployeeBO(Employee employee) {
        var employeeBO = new EmployeeBO();
        employeeBO.setId(employee.getEmployeeId());
        employeeBO.setPhoneNumber(employee.getPhoneNumber());
        employeeBO.setName(employee.getName());
        employeeBO.getSalaries().addAll(toSalaryBOs(employee.getSalaries()));
        return employeeBO;
    }

    public static Employee toEmployee(EmployeeBO employeeBO) {
        var employee = new Employee();
        employee.setEmployeeId(employeeBO.getId());
        employee.setPhoneNumber(employeeBO.getPhoneNumber());
        employee.setName(employeeBO.getName());
        employee.getSalaries().addAll(toSalaries(employeeBO.getSalaries()));
        return employee;
    }
}
