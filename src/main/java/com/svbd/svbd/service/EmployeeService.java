package com.svbd.svbd.service;

import com.svbd.svbd.entity.Employee;
import java.util.Collection;
import java.util.List;

public interface EmployeeService {
    Long createEmployee(Employee employee);
    void removeById(Long employeeId);
    List<Employee> findAllByIds(Collection<Long> employeeIds);
    List<Employee> findAllActiveEmployee();
}
