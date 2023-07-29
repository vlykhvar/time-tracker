package com.svbd.svbd.service;

import com.svbd.svbd.repository.salary.SalaryRepository;
import com.svbd.svbd.entity.Salary;
import org.hibernate.HibernateException;

import java.time.LocalDateTime;

public class SalaryService {

    private final SalaryRepository repository = new SalaryRepository();

    public Long createSalary(Salary salary) throws HibernateException {
        salary.setCreateAt(LocalDateTime.now());
        return repository.createSalary(salary);
    }
}
