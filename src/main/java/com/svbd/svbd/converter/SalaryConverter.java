package com.svbd.svbd.converter;

import com.svbd.svbd.dto.salary.SalaryBO;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Salary;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public final class SalaryConverter {

    private SalaryConverter() {
    }

    public static SalaryBO toSalaryBO(Salary salary) {
        var salaryBO = new SalaryBO();
        salaryBO.setId(salary.getSalaryId());
        salaryBO.setAnHour(salary.getAnHour());
        salaryBO.setStartDate(salary.getDateFrom());
        salaryBO.setEndDate(salary.getDateTo());
        return salaryBO;
    }

    public static Set<SalaryBO> toSalaryBOs(Collection<Salary> salaries) {
        return salaries.stream()
                .map(SalaryConverter::toSalaryBO)
                .collect(Collectors.toSet());
    }

    public static Salary toSalary(SalaryBO salaryBO) {
        var salary = new Salary();
        salary.setSalaryId(salaryBO.getId());
        salary.setEmployee(new Employee(salaryBO.getEmployeeId()));
        salary.setAnHour(salaryBO.getAnHour());
        salary.setDateFrom(salaryBO.getStartDate());
        salary.setDateTo(salaryBO.getEndDate());
        return salary;
    }

    public static Set<Salary> toSalaries(Collection<SalaryBO> salaryBOs) {
        return salaryBOs.stream()
                .map(SalaryConverter::toSalary)
                .collect(Collectors.toSet());
    }

}
