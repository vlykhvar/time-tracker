package com.svbd.svbd.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shift_id")
    private Long id;

    @Column(name = "shift_date")
    private LocalDate shiftDate;

    @OneToMany(mappedBy = "shift")
    private List<ShiftRow> shiftRows = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
