package com.svbd.svbd.converter;

import com.svbd.svbd.dto.shift.row.ShiftRowBO;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.entity.ShiftRow;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.svbd.svbd.util.DateTimeUtil.*;
import static java.util.Objects.isNull;

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

    public static ShiftRow toShiftRow(LocalDate shiftDate, ShiftRowBO shiftRowBO) {
        var shiftRow = new ShiftRow();
        shiftRow.setId(shiftRowBO.getShiftRowId());
        shiftRow.setEmployee(new Employee(shiftRowBO.getEmployeeId()));
        shiftRow.setShift(new Shift(shiftDate));
        shiftRow.setStartShift(isNull(shiftRowBO.getStartShift()) || shiftRowBO.getStartShift().isEmpty() ? null :
                toLocalDateTime(shiftDate, shiftRowBO.getStartShift()));
        shiftRow.setEndShift(isNull(shiftRowBO.getEndShift()) || shiftRowBO.getEndShift().isEmpty() ? null :
                prepareNightShiftEndDate(shiftDate, shiftRowBO.getEndShift()));
        shiftRow.setTotalTime(prepareWorkTotalTime(shiftRow.getStartShift(), shiftRow.getEndShift()));
        return shiftRow;
    }

    public static List<ShiftRow> toShiftRow(LocalDate shiftDate, Collection<ShiftRowBO> shiftRowBOs) {
        return shiftRowBOs.stream()
                .map(row -> toShiftRow(shiftDate, row))
                .toList();
    }
}
