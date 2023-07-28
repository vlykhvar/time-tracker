package com.svbd.svbd.service;

import com.svbd.svbd.dao.shift.ShiftRowRepository;
import com.svbd.svbd.dto.shift.ShiftBO;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.entity.ShiftRow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.svbd.svbd.converter.ShiftConverter.toShiftBO;

public class ShiftManagementService {

    private ShiftService shiftService = new ShiftService();
    private ShiftRowRepository rowRepository = new ShiftRowRepository();


    public ShiftBO getShiftByDate(LocalDate date) {
       return toShiftBO(shiftService.getShiftByDate(date));
    }

    public void creatOrUpdate(Shift shift) {
        var exist = shiftService.existShiftByDate(shift.getShiftDate());
        if (exist) {
            updateShift(shift);
        } else {
            createShift(shift);
        }
    }

    private void createShift(Shift shift) {
        var date = shiftService.createShift(shift);
        List<ShiftRow> rows = new ArrayList<>();
        for (int i = 0; i <= 2; i++) {
            var row = new ShiftRow();
            row.setShift(new Shift(date));
            row.setEmployee(new Employee(33L));
            rows.add(row);
        }
        rowRepository.createShiftRows(rows);
    }

    private void updateShift(Shift shift) {
        var shiftForUpdate = shiftService.getShiftByDate(shift.getShiftDate());
        var rowIds = shiftForUpdate.getShiftRows().stream().map(ShiftRow::getId).collect(Collectors.toSet());
        rowRepository.removeByIds(rowIds);
        shiftForUpdate.getShiftRows().clear();
        List<ShiftRow> rows = new ArrayList<>();
        for (int i = 0; i <= 2; i++) {
            var row = new ShiftRow();
            row.setShift(new Shift(shift.getShiftDate()));
            row.setEmployee(new Employee(33L));
            rows.add(row);
        }
        shift.getShiftRows().addAll(rows);
        shiftService.updateShift(shift);
    }
}
