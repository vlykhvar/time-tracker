package com.svbd.svbd.dto.shift.row;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShiftRowRequestBO {

    private Long shiftRowId;
    private LocalDate shiftDate;
    private Long employeeId;
    private String employeeName;
    private LocalDateTime startShift;
    private LocalDateTime endShift;
    private Integer totalWorkTime;


    public Long getShiftRowId() {
        return shiftRowId;
    }

    public void setShiftRowId(Long shiftRowId) {
        this.shiftRowId = shiftRowId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
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

    public Integer getTotalWorkTime() {
        return totalWorkTime;
    }

    public void setTotalWorkTime(Integer totalWorkTime) {
        this.totalWorkTime = totalWorkTime;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }
}
