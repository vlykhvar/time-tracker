package com.svbd.svbd.service;

import com.svbd.svbd.dto.settings.DinnerSettingBO;
import com.svbd.svbd.dto.shift.ShiftBO;
import com.svbd.svbd.dto.shift.ShiftRequestBO;
import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.entity.ShiftRow;
import com.svbd.svbd.exception.DinnerNotFoundException;
import com.svbd.svbd.exception.ShiftNotFoundException;
import jakarta.persistence.Transient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

import static com.svbd.svbd.converter.ShiftConverter.enrichShiftDate;
import static com.svbd.svbd.converter.ShiftConverter.toShiftBO;
import static com.svbd.svbd.converter.ShiftRowConverter.toShiftRow;
import static com.svbd.svbd.util.DateTimeUtil.parseLocalDate;
import static com.svbd.svbd.util.MathUtil.calculateTotalDinnerPriceForShit;
import static java.util.Objects.isNull;

@Service
public class ShiftManagementService {

    private final ShiftService shiftService;
    private final SettingsManagementService settingsManagementService;

    public ShiftManagementService(ShiftService shiftService, SettingsManagementService settingsManagementService) {
        this.shiftService = shiftService;
        this.settingsManagementService = settingsManagementService;
    }

    public ShiftBO getShiftByDate(LocalDate date) {
        Shift shift = null;
        try {
            shift = shiftService.getShiftByDate(date);
        } catch (ShiftNotFoundException ignored) {
        }
        if (isNull(shift)) {
            shift = new Shift();
        }
        Shift yesterdayShift = null;
        try {
            yesterdayShift = shiftService.getShiftByDate(date.minusDays(1));
        } catch (ShiftNotFoundException ignored) {
        }
        shift.setCashOnMorning(isNull(yesterdayShift) ? 0L : yesterdayShift.getCashOnEvening());
        shift.setCashKeyOnMorning(isNull(yesterdayShift) ? 0L : yesterdayShift.getCashKeyOnEvening());

        return toShiftBO(shift);
    }

    @Transient
    public void creatOrUpdate(ShiftRequestBO shiftBO) {
        var existShift = shiftService.getShiftByDateWithRows(shiftBO.getDate());
        var updateShift = enrichShiftDate(existShift, shiftBO);
        var shiftRows = toShiftRow(shiftBO.getShiftRowBOs());
        updateShift.getShiftRows().clear();
        updateShift.getShiftRows().addAll(shiftRows);
        try {
            var dinnerSetting = settingsManagementService.getDinnerSettingForDay(shiftBO.getDate());
            var totalWorkTimes = shiftRows.stream()
                    .map(ShiftRow::getTotalTime)
                    .filter(Objects::nonNull)
                    .toList();
            var totalDinnerTime = calculateTotalDinnerPriceForShit(totalWorkTimes, dinnerSetting.getPrice());
            updateShift.setTotalDinner(totalDinnerTime);
        } catch (DinnerNotFoundException e) {
            updateShift.setTotalDinner(0L);
        }
        shiftService.updateShift(updateShift);
    }

    public void updateShiftsByDinnerSettings(DinnerSettingBO dinnerSettingBO) {
        var shifts = shiftService.findAllByPeriod(parseLocalDate(dinnerSettingBO.getDateFrom()),
                parseLocalDate(dinnerSettingBO.getDateTo()));
        shifts.forEach(shift -> {
            var totalWorkTimes = shift.getShiftRows().stream()
                    .map(ShiftRow::getTotalTime)
                    .filter(Objects::nonNull)
                    .toList();
            var totalDinnerTime = calculateTotalDinnerPriceForShit(totalWorkTimes, dinnerSettingBO.getPrice());
            shift.setTotalDinner(totalDinnerTime);
            shiftService.updateShift(shift);
        });
    }
}
