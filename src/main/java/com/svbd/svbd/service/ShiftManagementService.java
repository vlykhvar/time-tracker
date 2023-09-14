package com.svbd.svbd.service;

import com.svbd.svbd.dto.shift.ShiftBO;
import com.svbd.svbd.dto.shift.row.ShiftRowBO;
import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.entity.ShiftRow;
import com.svbd.svbd.exception.DinnerNotFoundException;
import com.svbd.svbd.exception.ShiftNotFoundException;
import com.svbd.svbd.repository.shift.ShiftRowRepository;
import com.svbd.svbd.util.MathUtil;
import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

import static com.svbd.svbd.converter.ShiftConverter.enrichShiftDate;
import static com.svbd.svbd.converter.ShiftConverter.toShiftBO;
import static com.svbd.svbd.converter.ShiftRowConverter.toShiftRow;
import static java.util.Objects.isNull;

public class ShiftManagementService {

    private final ShiftService shiftService = new ShiftService();
    private final ShiftRowRepository rowRepository = new ShiftRowRepository();
    private final SettingsManagementService settingsManagementService = new SettingsManagementService();

    public ShiftBO getShiftByDate(LocalDate date) {
        Shift shift = null;
        try {
            shift = shiftService.getShiftByDate(date);
        } catch (ShiftNotFoundException ignored) {
        }

        if (isNull(shift)) {
            shift = new Shift();
            try {
                var yesterdayShift = shiftService.getShiftByDate(date.minusDays(1));
                shift.setCashOnMorning(yesterdayShift.getCashOnEvening());
                shift.setCashKeyOnMorning(yesterdayShift.getCashKeyOnEvening());
            } catch (ShiftNotFoundException ignored) {
            }
        }
        return toShiftBO(shift);
    }

    @Transient
    public void creatOrUpdate(ShiftBO shiftBo, Collection<ShiftRowBO> shiftRowBOs) {
        var existShift = shiftService.getShiftByDateWithRows(shiftBo.getDate());
        var updateShift = enrichShiftDate(existShift, shiftBo);
        var shiftRows = toShiftRow(updateShift.getShiftDate(), shiftRowBOs);
        updateShift.getShiftRows().clear();
        updateShift.getShiftRows().addAll(shiftRows);
        try {
            var dinnerSetting = settingsManagementService.getDinnerSettingForDay(shiftBo.getDate());
            var totalWorkTimes = shiftRows.stream()
                    .map(ShiftRow::getTotalTime)
                    .filter(Objects::nonNull)
                    .toList();
            var totalDinnerTime = MathUtil.calculateTotalDinnerPriceForShit(totalWorkTimes, dinnerSetting.getPrice());
            updateShift.setTotalDinner(totalDinnerTime);
        } catch (DinnerNotFoundException e) {
            updateShift.setTotalDinner(0L);
        }
        shiftService.updateShift(updateShift);
    }
}
