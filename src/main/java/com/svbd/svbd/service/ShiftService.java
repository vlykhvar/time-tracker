package com.svbd.svbd.service;

import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.exception.ShiftNotFoundException;
import com.svbd.svbd.repository.shift.ShiftRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ShiftService {

    private ShiftRepository repository = new ShiftRepository();

    public Shift getShiftByDate(LocalDate date) throws ShiftNotFoundException {
        return repository.getShiftByDate(date).orElseThrow(ShiftNotFoundException::new);
    }

    public Shift getShiftByDateWithRows(LocalDate date) {
        return repository.findShiftByDateJoinShiftRows(date).orElse(new Shift());
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

    public List<Shift> findAllByPeriod(LocalDate dateFrom, LocalDate dateTo) {
        return repository.findAllShiftsInPeriod(dateFrom, dateTo);
    }
}
