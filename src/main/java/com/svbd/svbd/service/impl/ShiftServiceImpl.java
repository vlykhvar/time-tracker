package com.svbd.svbd.service.impl;

import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.exception.ShiftNotFoundException;
import com.svbd.svbd.repository.shift.ShiftRepository;
import com.svbd.svbd.service.ShiftService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository repository;

    public ShiftServiceImpl(ShiftRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Shift> getShiftByDate(LocalDate date) throws ShiftNotFoundException {
        return repository.findById(date);
    }

    @Override
    public Shift getShiftByDateWithRows(LocalDate date) {
        return repository.findByIdWithShiftRows(date).orElse(new Shift());
    }

    @Override
    public LocalDate createShift(Shift shift) {
        return repository.save(shift).getShiftDate();
    }

    @Override
    public void updateShift(Shift shift) {
        repository.save(shift);
    }

    @Override
    public boolean existShiftByDate(LocalDate date) {
        return repository.existsById(date);
    }

    @Override
    public List<Shift> findAllByPeriod(LocalDate dateFrom, LocalDate dateTo) {
        return repository.findAllInPeriodWithShiftRows(dateFrom, dateTo);
    }
}
