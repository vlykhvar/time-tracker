package com.svbd.svbd.repository.projection;

import java.time.LocalDate;

public interface EmployShiftSalaryProjection {

     Long getEmployeeId();

     String getName();

     LocalDate getShiftDate();

     Long getSalary();
}
