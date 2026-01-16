package com.svbd.svbd.dto.shift.row;

import com.svbd.svbd.dto.employee.EmployeeShortBO;

import java.time.LocalDate;
import java.time.LocalTime;

public class ShiftRowBO {

    private Long shiftRowId;
    private LocalDate shiftDate;
    private Long employeeId;
    private String employeeName;
    private LocalTime startShift;
    private LocalTime endShift;
    private Integer totalWorkTime;

    public ShiftRowBO() {
    }

    public ShiftRowBO(EmployeeShortBO employeeShortBO) {
        this.employeeId = employeeShortBO.getId();
        this.employeeName = employeeShortBO.getName();
    }

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

    public LocalTime getStartShift() {
        return startShift;
    }

    public void setStartShift(LocalTime startShift) {
        this.startShift = startShift;
    }

    public LocalTime getEndShift() {
        return endShift;
    }

    public void setEndShift(LocalTime endShift) {
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
