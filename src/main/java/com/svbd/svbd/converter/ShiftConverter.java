package com.svbd.svbd.converter;

import com.svbd.svbd.dto.shift.ShiftBO;
import com.svbd.svbd.dto.shift.row.ShiftRowBO;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.entity.ShiftRow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.svbd.svbd.util.DateTimeUtil.getStringHourAndMinuteFromLocalDateTime;
import static com.svbd.svbd.util.DateTimeUtil.toLocalDateTime;
import static java.util.Objects.isNull;
import static jdk.internal.joptsimple.internal.Strings.EMPTY;

public final class ShiftConverter {

    private ShiftConverter() {
    }

    public static ShiftBO toShiftBO(Shift shift) {
        var shiftBO = new ShiftBO();
        shiftBO.setDate(shift.getShiftDate());
        shiftBO.setCashKeyOnEvening(shift.getCashKeyOnEvening());
        shiftBO.setCashKeyOnMorning(shift.getCashKeyOnMorning());
        shiftBO.setCashKeyTotal(shift.getCashKeyTotal());
        shiftBO.setComments(shiftBO.getComments());
        shiftBO.setCashOnEvening(shift.getCashOnEvening());
        shiftBO.setCashOnMorning(shift.getCashOnMorning());
        shiftBO.setTotalCash(shift.getTotalCash());
        shiftBO.setTaxi(shift.getTaxi());
        shiftBO.setComments(shift.getComments());
        shiftBO.getRows().addAll(toShiftRowBOs(shift.getShiftRows()));
        return shiftBO;
    }

    public static ShiftRowBO toShiftRowBO(ShiftRow row) {
        var shiftRowBO = new ShiftRowBO();
        shiftRowBO.setShiftRowId(row.getId());
        shiftRowBO.setStartShift(getStringHourAndMinuteFromLocalDateTime(row.getStartShift()));
        shiftRowBO.setEndShift(getStringHourAndMinuteFromLocalDateTime(row.getEndShift()));
        shiftRowBO.setEmployeeId(row.getEmployee().getEmployeeId());
        shiftRowBO.setEmployeeName(row.getEmployee().getName());
        return shiftRowBO;
    }

    public static Set<ShiftRowBO> toShiftRowBOs(Collection<ShiftRow> rows) {
        return rows.stream()
                .map(ShiftConverter::toShiftRowBO)
                .collect(Collectors.toSet());
    }

    public static Shift enrichShiftDate(Shift shift, ShiftBO shiftBO) {
        shift.setShiftDate(shiftBO.getDate());
        shift.setCashKeyOnEvening(shiftBO.getCashKeyOnEvening());
        shift.setCashKeyOnMorning(shiftBO.getCashKeyOnMorning());
        shift.setCashKeyTotal(shiftBO.getCashKeyTotal());
        shift.setComments(shiftBO.getComments());
        shift.setCashOnEvening(shiftBO.getCashOnEvening());
        shift.setCashOnMorning(shiftBO.getCashOnMorning());
        shift.setTotalCash(shiftBO.getTotalCash());
        shift.setTaxi(shiftBO.getTaxi());
        shift.setComments(shiftBO.getComments());
        return shift;
    }

    public static ShiftRow toShiftRow(LocalDate shiftDate, ShiftRowBO shiftRowBO) {
        var shiftRow = new ShiftRow();
        shiftRow.setId(shiftRowBO.getShiftRowId());
        shiftRow.setEmployee(new Employee(shiftRowBO.getEmployeeId()));
        shiftRow.setShift(new Shift(shiftDate));
        shiftRow.setStartShift(isNull(shiftRowBO.getStartShift()) || shiftRowBO.getStartShift().isEmpty() ? null :
                toLocalDateTime(shiftDate, shiftRowBO.getStartShift()));
        shiftRow.setEndShift(isNull(shiftRowBO.getEndShift()) || shiftRowBO.getEndShift().isEmpty() ? null :
                toLocalDateTime(shiftDate, shiftRowBO.getEndShift()));
        return shiftRow;
    }

    public static List<ShiftRow> toShiftRow(LocalDate shiftDate, Collection<ShiftRowBO> shiftRowBOs) {
        return shiftRowBOs.stream()
                .map(row -> toShiftRow(shiftDate, row))
                .toList();
    }
}
