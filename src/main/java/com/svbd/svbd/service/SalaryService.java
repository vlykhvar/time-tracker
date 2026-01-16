package com.svbd.svbd.service;

import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.repository.projection.SalaryEmployeeProjection;
import com.svbd.svbd.repository.salary.SalaryRepository;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
public class SalaryService {

    @Autowired
    private SalaryRepository repository;

    public Long createSalary(Salary salary) throws HibernateException {
        salary.setDateFrom(LocalDate.now());
        return repository.save(salary).getSalaryId();
    }

    public void removeSalaryById(Collection<Long> salaryIds) {
        if (salaryIds.isEmpty()) {
            return;
        }
        repository.deleteByIds(salaryIds);
    }
}
