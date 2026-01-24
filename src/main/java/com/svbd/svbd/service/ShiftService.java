package com.svbd.svbd.service;

import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.exception.ShiftNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftService {
    Optional<Shift> getShiftByDate(LocalDate date) throws ShiftNotFoundException;
    Shift getShiftByDateWithRows(LocalDate date);
    LocalDate createShift(Shift shift);
    void updateShift(Shift shift);
    boolean existShiftByDate(LocalDate date);
    List<Shift> findAllByPeriod(LocalDate dateFrom, LocalDate dateTo);
}
