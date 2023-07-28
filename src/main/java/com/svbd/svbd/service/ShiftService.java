package com.svbd.svbd.service;

import com.svbd.svbd.dao.shift.ShiftDao;
import com.svbd.svbd.dto.shift.ShiftBO;
import com.svbd.svbd.entity.Shift;

import java.time.LocalDate;

import static com.svbd.svbd.converter.ShiftConverter.toShiftBO;

public class ShiftService {

    private ShiftDao repository = new ShiftDao();

    public Shift getShiftByDate(LocalDate date) {
        return repository.getShiftByDate(date).orElse(new Shift());
    }

    public LocalDate createShift(Shift shift) {
        return repository.createShift(shift);
    }

    public void updateShift(Shift shift) {
        repository.updateShift(shift);
    }

    public boolean existShiftByDate(LocalDate date) {
        return repository.existRowByDate(date);
    }
}
