package com.svbd.svbd.service;

import com.svbd.svbd.dto.shift.ShiftBO;
import com.svbd.svbd.dto.shift.row.ShiftRowBO;
import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.exception.ShiftNotFoundException;
import com.svbd.svbd.repository.shift.ShiftRowRepository;
import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.util.Collection;

import static com.svbd.svbd.converter.ShiftConverter.enrichShiftDate;
import static com.svbd.svbd.converter.ShiftConverter.toShiftBO;
import static com.svbd.svbd.converter.ShiftRowConverter.toShiftRow;
import static java.util.Objects.isNull;

public class ShiftManagementService {

    private ShiftService shiftService = new ShiftService();
    private ShiftRowRepository rowRepository = new ShiftRowRepository();

    public ShiftBO getShiftByDate(LocalDate date) {
        Shift shift = null;
        try {
            shift = shiftService.getShiftByDate(date);
        } catch (ShiftNotFoundException ignored) {
        }

        if (isNull(shift)) {
            shift = new Shift();
            try {
                var yesterdayShift = shiftService.getShiftByDate(date.minusDays(1));
                shift.setCashOnMorning(yesterdayShift.getCashOnEvening());
                shift.setCashKeyOnMorning(yesterdayShift.getCashKeyOnEvening());
            } catch (ShiftNotFoundException ignored) {
            }
        }
        return toShiftBO(shift);
    }

    @Transient
    public void creatOrUpdate(ShiftBO shiftBo, Collection<ShiftRowBO> shiftRowBOs) {
        var existShift = shiftService.getShiftByDateWithRows(shiftBo.getDate());
        var updateShift = enrichShiftDate(existShift, shiftBo);
        var shiftRows = toShiftRow(updateShift.getShiftDate(), shiftRowBOs);
        updateShift.getShiftRows().clear();
        updateShift.getShiftRows().addAll(shiftRows);
        shiftService.updateShift(updateShift);

    }

    private void createShift(Shift shift) {
    }

    private void updateShift(Shift shift) {
    }
}
