package com.svbd.svbd.service.impl;

import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.repository.employee.EmployeeRepository;
import com.svbd.svbd.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
public class EmployeeServiceIpml implements EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeServiceIpml(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Long createEmployee(Employee employee) {
        return repository.save(employee).getEmployeeId();
    }

    @Override
    public void removeById(Long employeeId) {
        // "Мягкое" удаление
        repository.findById(employeeId).ifPresent(employee -> {
            employee.setRemovedAt(LocalDate.now());
            repository.save(employee);
        });
    }

    @Override
    public List<Employee> findAllByIds(Collection<Long> employeeIds) {
        return repository.findAllById(employeeIds);
    }

    @Override
    public List<Employee> findAllActiveEmployee() {
        return repository.findAllRemovedAtIsNull();
    }
}
