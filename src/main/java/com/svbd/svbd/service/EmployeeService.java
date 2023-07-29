package com.svbd.svbd.service;

import com.svbd.svbd.repository.employee.EmployeeRepository;
import com.svbd.svbd.entity.Employee;

import java.util.List;

public class EmployeeService {

    private EmployeeRepository repository = new EmployeeRepository();

    public Long createEmployee(Employee employee) {
        return repository.createEmployee(employee);
    }

    public void removeById(Long employeeId) {
        repository.removeById(employeeId);
    }

    public List<Employee> getAllEmployee() {
        return repository.findAll();
    }

    public List<Employee> findAllWithLastSalary() {
        return repository.findAllWithLastSalary();
    }
}
