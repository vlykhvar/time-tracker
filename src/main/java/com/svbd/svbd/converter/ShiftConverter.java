package com.svbd.svbd.converter;

import com.svbd.svbd.dto.shift.ShiftBO;
import com.svbd.svbd.entity.Shift;

import static com.svbd.svbd.converter.ShiftRowConverter.toShiftRowBOs;

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
        shiftBO.setBonusTime(shift.getBonusTime());
        shiftBO.getRows().addAll(toShiftRowBOs(shift.getShiftRows()));
        return shiftBO;
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
        shift.setBonusTime(shiftBO.getBonusTime());
        return shift;
    }
}
