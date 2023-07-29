package com.svbd.svbd.service;

import com.svbd.svbd.repository.employee.EmployeeRepository;
import com.svbd.svbd.dto.employee.EmployeeShortBO;
import com.svbd.svbd.dto.employee.EmployeeWithLastSalaryBO;
import com.svbd.svbd.entity.Employee;

import java.sql.SQLException;
import java.util.*;

import static com.svbd.svbd.converter.EmployeeConverter.toEmployeeShortBO;
import static com.svbd.svbd.converter.EmployeeConverter.toEmployeeWithLastSalaryBOs;

public class EmployeeManagementService {

    private final EmployeeService employeeService = new EmployeeService();

    public final EmployeeRepository repository = new EmployeeRepository();
    private final SalaryService salaryService = new SalaryService();

    public Set<EmployeeShortBO> getAllShortEmployeesData() {
        return toEmployeeShortBO(repository.findAllEmployeeIdAndName());
    }

    public Set<EmployeeShortBO> getAllShortEmployeesDataExcludeIds(Collection<Long> excludeIds) {
        return toEmployeeShortBO(repository.findAllIdNotIn(excludeIds));
    }

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
