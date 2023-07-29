package com.svbd.svbd.service;

import com.svbd.svbd.dto.shift.row.ShiftRowBO;
import com.svbd.svbd.repository.shift.ShiftRowRepository;
import com.svbd.svbd.dto.shift.ShiftBO;
import com.svbd.svbd.entity.Shift;
import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.util.Collection;

import static com.svbd.svbd.converter.ShiftConverter.*;

public class ShiftManagementService {

    private ShiftService shiftService = new ShiftService();
    private ShiftRowRepository rowRepository = new ShiftRowRepository();

    public ShiftBO getShiftByDate(LocalDate date) {
       return toShiftBO(shiftService.getShiftByDate(date));
    }

    @Transient
    public void creatOrUpdate(ShiftBO shiftBo, Collection<ShiftRowBO> shiftRowBOs) {
        var existShift = shiftService.getShiftByDate(shiftBo.getDate());
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
