package com.svbd.svbd.repository;

import com.svbd.svbd.repository.projection.EmployShiftSalaryProjection;
import org.hibernate.transform.ResultTransformer;

import java.util.List;

public class CustomProjectionResultTransformer implements ResultTransformer {

    @Override
    public EmployShiftSalaryProjection transformTuple(Object[] tuple, String[] aliases) {
        var dto = new EmployShiftSalaryProjection();
        dto.setEmployeeId((Long) tuple[0]);
        dto.setName((String) tuple[1]);
        java.sql.Date sqlDate = (java.sql.Date) tuple[2];
        if (sqlDate != null) {
            dto.setShiftDate(sqlDate.toLocalDate());
        }
        dto.setSalary((Long) tuple[3]);
        return dto;
    }

    @Override
    public List transformList(List collection) {
        return collection;
    }
}
