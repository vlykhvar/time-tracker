package com.svbd.svbd.service;

import com.svbd.svbd.dto.employee.EmployeeBO;
import com.svbd.svbd.dto.employee.EmployeeShortBO;
import com.svbd.svbd.dto.employee.EmployeeWithLastSalaryBO;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.exception.OverlapingDateException;
import com.svbd.svbd.repository.employee.EmployeeRepository;
import com.svbd.svbd.repository.projection.SalaryEmployeeProjection;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.svbd.svbd.converter.EmployeeConverter.*;
import static com.svbd.svbd.converter.EmployeeConverter.toEmployeeWithLastSalaryBOs;

public class EmployeeManagementService {

    private final EmployeeService employeeService = new EmployeeService();

    private final EmployeeRepository repository = new EmployeeRepository();
    private final SalaryService salaryService = new SalaryService();

    public Set<EmployeeShortBO> getAllShortEmployeesData() {
        return toEmployeeShortBO(repository.findAllEmployeeIdAndName());
    }

    public Set<EmployeeShortBO> getAllShortEmployeesDataExcludeIds(Collection<Long> excludeIds) {
        return toEmployeeShortBO(repository.findAllIdNotIn(excludeIds));
    }

    public void createEmployee(Employee employee)  {
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
        var employees = employeeService.findAllActiveEmployee();
        var employeeIds = employees.stream().map(Employee::getEmployeeId).collect(Collectors.toSet());
        var salaryByEmployeeId = salaryService.getActualSalaryForEmployees(employeeIds).stream()
                .collect(Collectors.toMap(SalaryEmployeeProjection::employeeId, SalaryEmployeeProjection::anHour));
        var employeeBOs = toEmployeeWithLastSalaryBOs(employees);
        employeeBOs.forEach(employeeWithLastSalaryBO -> employeeWithLastSalaryBO.setPerHour(
                salaryByEmployeeId.getOrDefault(employeeWithLastSalaryBO.getId(), 0L))
        );

        return employeeBOs;
    }

    public EmployeeBO getEmployee(Long employeeId) throws Exception {
       return toEmployeeBO(repository.findById(employeeId).orElseThrow(() -> new Exception()));
    }

    public void updateEmployee(EmployeeBO employeeBO) throws OverlapingDateException {
        var employee = toEmployee(employeeBO);
        isThereOverlappingDate(employee.getSalaries());
        repository.updateEmployee(employee);
    }

    private void isThereOverlappingDate(Collection<Salary> salaries) throws OverlapingDateException {
        if (salaries.size() <= 1) {
            return;
        }
        var sortedSalaries = salaries.stream()
                .sorted(Comparator.comparing(Salary::getCreateAt))
                .toList();

        for(int i = 1; i < sortedSalaries.size(); i++) {
            if (isDateInRange(sortedSalaries.get(0).getCreateAt(), sortedSalaries.get(i)) ||
                    isDateInRange(sortedSalaries.get(0).getRemovedAt(), sortedSalaries.get(i))) {
                throw new OverlapingDateException();
            }
        }
        isThereOverlappingDate(sortedSalaries.subList(1, salaries.size()));
    }

    private boolean isDateInRange(LocalDate date, Salary salary) {
        return date.isAfter(salary.getCreateAt()) && date.isAfter(salary.getRemovedAt());
    }
}
