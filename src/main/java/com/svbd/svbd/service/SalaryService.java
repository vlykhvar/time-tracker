package com.svbd.svbd.service;

import com.svbd.svbd.dao.salary.SalaryDaoImpl;
import com.svbd.svbd.entity.Salary;
import org.hibernate.HibernateException;

import java.time.LocalDateTime;

public class SalaryService {

    private final SalaryDaoImpl repository = new SalaryDaoImpl();

    public Long createSalary(Salary salary) throws HibernateException {
        salary.setCreateAt(LocalDateTime.now());
        return repository.createSalary(salary);
    }
}
