package com.svbd.svbd.converter;

import com.svbd.svbd.dto.shift.row.ShiftRowBO;
import com.svbd.svbd.dto.shift.row.ShiftRowRequestBO;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.entity.ShiftRow;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.svbd.svbd.util.DateTimeUtil.getStringHourAndMinuteFromLocalDateTime;

public final class ShiftRowConverter {

    private ShiftRowConverter() {
    }

    public static ShiftRowBO toShiftRowBO(ShiftRow row) {
        var shiftRowBO = new ShiftRowBO();
        shiftRowBO.setShiftRowId(row.getId());
        shiftRowBO.setStartShift(getStringHourAndMinuteFromLocalDateTime(row.getStartShift()));
        shiftRowBO.setEndShift(getStringHourAndMinuteFromLocalDateTime(row.getEndShift()));
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

    public static ShiftRow toShiftRow(ShiftRowRequestBO shiftRowBO) {
        var shiftRow = new ShiftRow();
        shiftRow.setId(shiftRowBO.getShiftRowId());
        shiftRow.setEmployee(new Employee(shiftRowBO.getEmployeeId()));
        shiftRow.setShift(new Shift(shiftRowBO.getShiftDate()));
        shiftRow.setStartShift(shiftRowBO.getStartShift());
        shiftRow.setEndShift(shiftRowBO.getEndShift());
        shiftRow.setTotalTime(shiftRowBO.getTotalWorkTime());
        return shiftRow;
    }

    public static List<ShiftRow> toShiftRow(Collection<ShiftRowRequestBO> shiftRowBOs) {
        return shiftRowBOs.stream()
                .map(ShiftRowConverter::toShiftRow)
                .toList();
    }
}
