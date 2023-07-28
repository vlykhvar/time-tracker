package com.svbd.svbd.converter;

import com.svbd.svbd.dto.employee.EmployeeWithLastSalaryBO;
import com.svbd.svbd.dto.shift.ShiftBO;
import com.svbd.svbd.dto.shift.row.ShiftRowBO;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.entity.ShiftRow;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public final class ShiftConverter {

    private ShiftConverter() {
    }

    public static ShiftBO toShiftBO(Shift shift) {
        var shiftBO = new ShiftBO();
        shiftBO.setDate(shift.getShiftDate());
        shiftBO.getRows().addAll(toShiftRowBOs(shift.getShiftRows()));
        return shiftBO;
    }

    public static ShiftRowBO toShiftRowBO(ShiftRow row) {
        var shiftRowBO = new ShiftRowBO();
        shiftRowBO.setStartShift(row.getStartShift());
        shiftRowBO.setEndShift(row.getEndShift());
        shiftRowBO.setEmployeeId(row.getEmployee().getEmployeeId());
        shiftRowBO.setEmployeeName(row.getEmployee().getName());
        return shiftRowBO;
    }

    public static Set<ShiftRowBO> toShiftRowBOs(Collection<ShiftRow> rows) {
        return rows.stream()
                .map(ShiftConverter::toShiftRowBO)
                .collect(Collectors.toSet());
    }
}
