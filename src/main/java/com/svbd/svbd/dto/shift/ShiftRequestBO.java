package com.svbd.svbd.dto.shift;

import com.svbd.svbd.dto.shift.row.ShiftRowRequestBO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShiftRequestBO {
    private LocalDate date;
    private Long bonusTime;
    private Long totalCash;
    private Long cashOnEvening;
    private Long cashOnMorning;
    private Long cashKeyOnEvening;
    private Long cashKeyOnMorning;
    private Long cashKeyTotal;
    private Long taxi;
    private Long totalDinner;
    private String comments;
    private Long dailyRevenue;
    private final List<ShiftRowRequestBO> shiftRowBOs = new ArrayList<>();

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getBonusTime() {
        return bonusTime;
    }

    public void setBonusTime(Long bonusTime) {
        this.bonusTime = bonusTime;
    }

    public Long getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(Long totalCash) {
        this.totalCash = totalCash;
    }

    public Long getCashOnEvening() {
        return cashOnEvening;
    }

    public void setCashOnEvening(Long cashOnEvening) {
        this.cashOnEvening = cashOnEvening;
    }

    public Long getCashOnMorning() {
        return cashOnMorning;
    }

    public void setCashOnMorning(Long cashOnMorning) {
        this.cashOnMorning = cashOnMorning;
    }

    public Long getCashKeyOnEvening() {
        return cashKeyOnEvening;
    }

    public void setCashKeyOnEvening(Long cashKeyOnEvening) {
        this.cashKeyOnEvening = cashKeyOnEvening;
    }

    public Long getCashKeyOnMorning() {
        return cashKeyOnMorning;
    }

    public void setCashKeyOnMorning(Long cashKeyOnMorning) {
        this.cashKeyOnMorning = cashKeyOnMorning;
    }

    public Long getCashKeyTotal() {
        return cashKeyTotal;
    }

    public void setCashKeyTotal(Long cashKeyTotal) {
        this.cashKeyTotal = cashKeyTotal;
    }

    public Long getTaxi() {
        return taxi;
    }

    public void setTaxi(Long taxi) {
        this.taxi = taxi;
    }

    public Long getTotalDinner() {
        return totalDinner;
    }

    public void setTotalDinner(Long totalDinner) {
        this.totalDinner = totalDinner;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<ShiftRowRequestBO> getShiftRowBOs() {
        return shiftRowBOs;
    }

    public Long getDailyRevenue() {
        return dailyRevenue;
    }

    public void setDailyRevenue(Long dailyRevenue) {
        this.dailyRevenue = dailyRevenue;
    }
}
