package com.svbd.svbd.entity.shift;

import com.svbd.svbd.entity.employees.Employee;

import java.time.LocalDate;
import java.util.Set;

public class Shift {

    private Long id;
    private LocalDate shiftDate;

    private Set<Employee> employees;
}
