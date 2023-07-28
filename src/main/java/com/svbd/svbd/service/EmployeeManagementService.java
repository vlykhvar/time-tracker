package com.svbd.svbd.service;

import com.svbd.svbd.dto.employee.EmployeeWithLastSalaryBO;
import com.svbd.svbd.entity.Employee;

import java.sql.SQLException;
import java.util.EmptyStackException;
import java.util.Set;

import static com.svbd.svbd.converter.EmployeeConverter.toEmployeeWithLastSalaryBOs;

public class EmployeeManagementService {

    private final EmployeeService employeeService = new EmployeeService();
    private final SalaryService salaryService = new SalaryService();

    public void createEmployee(Employee employee) throws SQLException {
        var salary = employee.getSalaries().stream().findFirst().orElseThrow(() -> new EmptyStackException());
        employee.getSalaries().clear();
        var employeeId = employeeService.createEmployee(employee);
        salary.setEmployee(new Employee(employeeId));
        salaryService.createSalary(salary);
    }

    public void removeById(Long employeeId) {
        employeeService.removeById(employeeId);
    }

    public Set<EmployeeWithLastSalaryBO> getEmployeesWithLastSalaryBO() {
        return toEmployeeWithLastSalaryBOs(employeeService.findAllWithLastSalary());
    }
}
