package com.svbd.svbd.service;

import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.exception.ShiftNotFoundException;
import com.svbd.svbd.repository.shift.ShiftRepository;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository repository;

    public Optional<Shift> getShiftByDate(LocalDate date) throws ShiftNotFoundException {
        return repository.findById(date);
    }

    public Shift getShiftByDateWithRows(LocalDate date) {
        return repository.findByIdWithShiftRows(date).orElse(new Shift());
    }

    public LocalDate createShift(Shift shift) {
        return repository.save(shift).getShiftDate();
    }

    public void updateShift(Shift shift) {
        repository.save(shift);
    }

    public boolean existShiftByDate(LocalDate date) {
        return repository.existsById(date);
    }

    public List<Shift> findAllByPeriod(LocalDate dateFrom, LocalDate dateTo) {
        return repository.findAllInPeriodWithShiftRows(dateFrom, dateTo);
    }
}
