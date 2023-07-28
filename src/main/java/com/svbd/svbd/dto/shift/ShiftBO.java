package com.svbd.svbd.dto.shift;

import com.svbd.svbd.dto.shift.row.ShiftRowBO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShiftBO {

    private Long shiftId;
    private LocalDate date;
    private final List<ShiftRowBO> rows = new ArrayList<>();

    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<ShiftRowBO> getRows() {
        return rows;
    }
}
