package com.svbd.svbd.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Entity
@Table(name = "shift")
public class Shift {

    @Id
    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;

    private Long taxi;

    @Column(name = "total_cash")
    private Long totalCash;

    @Column(name = "cash_on_evening")
    private Long cashOnEvening;

    @Column(name = "cash_on_morning")
    private Long cashOnMorning;

    @Column(name = "cash_key_on_evening")
    private Long cashKeyOnEvening;

    @Column(name = "cash_key_on_morning")
    private Long cashKeyOnMorning;

    @Column(name = "cash_key_total")
    private Long cashKeyTotal;

    @Column(name = "bonus_time")
    private Long bonusTime;

    @Column(name = "total_dinner")
    private Long totalDinner;

    @Column(name = "comments", length = 10000)
    private String comments;

    @Column(name = "daily_revenue")
    private Long dailyRevenue = 0L;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getTaxi() {
        return taxi;
    }

    public void setTaxi(Long taxi) {
        this.taxi = taxi;
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

    public Long getBonusTime() {
        return bonusTime;
    }

    public void setBonusTime(Long bonusTime) {
        this.bonusTime = bonusTime;
    }

    public Long getTotalDinner() {
        return totalDinner;
    }

    public void setTotalDinner(Long totalDinner) {
        this.totalDinner = totalDinner;
    }

    public Long getDailyRevenue() {
        return isNull(dailyRevenue) ? 0 : dailyRevenue;
    }

    public void setDailyRevenue(Long dailyRevenue) {
        this.dailyRevenue = dailyRevenue;
    }
}
