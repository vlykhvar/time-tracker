package com.svbd.svbd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class ShiftRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shift_row_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shift_date", referencedColumnName = "shift_date")
    private Shift shift;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "EMPLOYEE_ID")
    private Employee employee;

    @Column(name = "start_shift")
    private LocalDateTime startShift;

    @Column(name = "end_shift")
    private LocalDateTime endShift;

    @Column(name = "total_time")
    private Integer totalTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public LocalDateTime getStartShift() {
        return startShift;
    }

    public void setStartShift(LocalDateTime startShift) {
        this.startShift = startShift;
    }

    public LocalDateTime getEndShift() {
        return endShift;
    }

    public void setEndShift(LocalDateTime endShift) {
        this.endShift = endShift;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }
}
