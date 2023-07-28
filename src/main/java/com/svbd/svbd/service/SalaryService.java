package com.svbd.svbd.service;

import com.svbd.svbd.dao.salary.SalaryDaoImpl;
import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.settings.DatabaseModule;
import com.svbd.svbd.settings.HibernateModule;
import org.hibernate.HibernateException;

import java.sql.SQLException;

public class SalaryService {

    private final SalaryDaoImpl repository = new SalaryDaoImpl();

    public Long createSalary(Salary salary) throws HibernateException {
        return repository.createSalary(salary);
    }
}
