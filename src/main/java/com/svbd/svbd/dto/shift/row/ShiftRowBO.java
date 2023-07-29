package com.svbd.svbd.dto.shift.row;

import com.svbd.svbd.dto.employee.EmployeeShortBO;

public class ShiftRowBO {

    private Long shiftRowId;
    private Long employeeId;
    private String employeeName;
    private String startShift;
    private String endShift;
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

    public String getStartShift() {
        return startShift;
    }

    public void setStartShift(String startShift) {
        this.startShift = startShift;
    }

    public String getEndShift() {
        return endShift;
    }

    public void setEndShift(String endShift) {
        this.endShift = endShift;
    }

    public Integer getTotalWorkTime() {
        return totalWorkTime;
    }

    public void setTotalWorkTime(Integer totalWorkTime) {
        this.totalWorkTime = totalWorkTime;
    }
}
