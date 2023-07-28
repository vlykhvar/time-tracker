package com.svbd.svbd.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Shift {

    @Id
    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL)
    private List<ShiftRow> shiftRows = new ArrayList<>();

    public Shift() {
    }

    public Shift(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public List<ShiftRow> getShiftRows() {
        return shiftRows;
    }

    public void setShiftRows(List<ShiftRow> shiftRows) {
        this.shiftRows = shiftRows;
    }
}
