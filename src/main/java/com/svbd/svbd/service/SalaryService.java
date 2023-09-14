package com.svbd.svbd.service;

import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.repository.projection.SalaryEmployeeProjection;
import com.svbd.svbd.repository.salary.SalaryRepository;
import org.hibernate.HibernateException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class SalaryService {

    private final SalaryRepository repository = new SalaryRepository();

    public Long createSalary(Salary salary) throws HibernateException {
        salary.setDateFrom(LocalDate.now());
        return repository.createSalary(salary);
    }

    public void removeSalaryById(Collection<Long> salaryIds) {
        if (salaryIds.isEmpty()) {
            return;
        }
        repository.removeSalariesByIds(salaryIds);
    }

    public List<SalaryEmployeeProjection> getActualSalaryForEmployees(Collection<Long> employeeId) {
        return repository.findAllByEmployeeIdsAndStartDateEndDateBetweenDate(employeeId, LocalDate.now());
    }
}
