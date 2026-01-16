package com.svbd.svbd.converter;

import com.svbd.svbd.dto.shift.row.ShiftRowBO;
import com.svbd.svbd.dto.shift.row.ShiftRowRequestBO;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.entity.ShiftRow;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public final class ShiftRowConverter {

    private ShiftRowConverter() {
    }

    public static ShiftRowBO toShiftRowBO(ShiftRow row) {
        var shiftRowBO = new ShiftRowBO();
        shiftRowBO.setShiftRowId(row.getId());
        if (nonNull(row.getStartShift())) {
            shiftRowBO.setStartShift(row.getStartShift().toLocalTime());
        }
        if (nonNull(row.getEndShift())) {
            shiftRowBO.setEndShift(row.getEndShift().toLocalTime());
        }
        shiftRowBO.setEmployeeId(row.getEmployee().getEmployeeId());
        shiftRowBO.setEmployeeName(row.getEmployee().getName());
        shiftRowBO.setTotalWorkTime(row.getTotalTime());
        return shiftRowBO;
    }

    public static Set<ShiftRowBO> toShiftRowBOs(Collection<ShiftRow> rows) {
        return rows.stream()
                .map(ShiftRowConverter::toShiftRowBO)
                .collect(Collectors.toSet());
    }

    public static ShiftRow toShiftRow(ShiftRowRequestBO shiftRowBO, Map<Long, Employee> employeeMap) {
        var shiftRow = new ShiftRow();
        shiftRow.setId(shiftRowBO.getShiftRowId());
        shiftRow.setEmployee(employeeMap.get(shiftRowBO.getEmployeeId()));
        shiftRow.setShift(new Shift(shiftRowBO.getShiftDate()));
        shiftRow.setStartShift(shiftRowBO.getStartShift());
        shiftRow.setEndShift(shiftRowBO.getEndShift());
        shiftRow.setTotalTime(shiftRowBO.getTotalWorkTime());
        return shiftRow;
    }

    public static List<ShiftRow> toShiftRow(Collection<ShiftRowRequestBO> shiftRowBOs, Map<Long, Employee> employeeMap) {
        return shiftRowBOs.stream()
                .map(shiftRowRequestBO -> toShiftRow(shiftRowRequestBO, employeeMap))
                .filter(shiftRow -> Objects.nonNull(shiftRow.getEmployee()))
                .toList();
    }
}
