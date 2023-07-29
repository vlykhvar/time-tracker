package com.svbd.svbd.service;

import com.svbd.svbd.repository.shift.ShiftRepository;
import com.svbd.svbd.entity.Shift;

import java.time.LocalDate;

public class ShiftService {

    private ShiftRepository repository = new ShiftRepository();

    public Shift getShiftByDate(LocalDate date) {
        Shift shift;
        var optionalShift = repository.getShiftByDate(date);
        if (optionalShift.isPresent()) {
            shift = optionalShift.get();
        } else {
            shift = new Shift();
            repository.getShiftByDate(date.minusDays(1))
                    .ifPresent(yesterdayShift -> {
                        shift.setCashOnMorning(yesterdayShift.getCashOnEvening());
                        shift.setCashKeyOnMorning(yesterdayShift.getCashKeyOnEvening());
                    });
        }
        return shift;
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
