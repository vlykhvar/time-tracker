package com.svbd.svbd.service.impl;

import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.repository.salary.SalaryRepository;
import com.svbd.svbd.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SalaryServiceImpl implements SalaryService {

    @Autowired
    private SalaryRepository repository;

    public Long createSalary(Salary salary) {
        salary.setDateFrom(LocalDate.now());
        return repository.save(salary).getSalaryId();
    }
}
