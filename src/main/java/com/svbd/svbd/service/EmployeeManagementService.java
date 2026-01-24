package com.svbd.svbd.service;

import com.svbd.svbd.dto.employee.EmployeeBO;
import com.svbd.svbd.dto.employee.EmployeeShortBO;
import com.svbd.svbd.dto.employee.EmployeeWithLastSalaryBO;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.exception.OverlapingDateException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.svbd.svbd.converter.EmployeeConverter.toEmployeeShortBO;

public interface EmployeeManagementService {

    Set<EmployeeShortBO> getAllShortEmployeesData();

    Set<EmployeeShortBO> getAllShortEmployeesDataExcludeIds(Collection<Long> excludeIds);

    void createEmployee(Employee employee);

    void removeById(Long employeeId);

    List<EmployeeWithLastSalaryBO> getEmployeesWithLastSalaryBO();

    EmployeeBO getEmployee(Long employeeId) throws Exception;

    void updateEmployee(EmployeeBO employeeBO) throws OverlapingDateException;
}
