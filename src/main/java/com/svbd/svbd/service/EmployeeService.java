package com.svbd.svbd.service;

import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.repository.employee.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Long createEmployee(Employee employee) {
        // Метод save вернет сохраненную сущность с ID
        return repository.save(employee).getEmployeeId();
    }

    public void removeById(Long employeeId) {
        // "Мягкое" удаление
        repository.findById(employeeId).ifPresent(employee -> {
            employee.setRemovedAt(LocalDate.now());
            repository.save(employee);
        });
    }

    public List<Employee> findAllActiveEmployee() {
        return repository.findAllRemovedAtIsNull();
    }
}
