package com.svbd.svbd.service.impl;

import com.svbd.svbd.dto.settings.DinnerSettingBO;
import com.svbd.svbd.dto.shift.ShiftBO;
import com.svbd.svbd.dto.shift.ShiftRequestBO;
import com.svbd.svbd.dto.shift.row.ShiftRowRequestBO;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.entity.ShiftRow;
import com.svbd.svbd.exception.DinnerNotFoundException;
import com.svbd.svbd.service.EmployeeService;
import com.svbd.svbd.service.SettingsManagementService;
import com.svbd.svbd.service.ShiftManagementService;
import com.svbd.svbd.service.ShiftService;
import jakarta.persistence.Transient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.svbd.svbd.converter.ShiftConverter.enrichShiftDate;
import static com.svbd.svbd.converter.ShiftConverter.toShiftBO;
import static com.svbd.svbd.converter.ShiftRowConverter.toShiftRow;
import static com.svbd.svbd.util.DateTimeUtil.parseLocalDate;
import static com.svbd.svbd.util.MathUtil.calculateTotalDinnerPriceForShit;

@Service
public class ShiftManagementServiceImpl implements ShiftManagementService {

    private final ShiftService shiftService;
    private final SettingsManagementService settingsManagementService;
    private final EmployeeService employeeService;

    public ShiftManagementServiceImpl(ShiftService shiftService,
                                      SettingsManagementService settingsManagementService,
                                      EmployeeService employeeService) {
        this.shiftService = shiftService;
        this.settingsManagementService = settingsManagementService;
        this.employeeService = employeeService;
    }

    @Override
    @Transactional
    public ShiftBO getShiftByDate(LocalDate date) {
        // 1. Find today's shift. If not found, create a new, empty Shift object.
        // The 'orElseGet' ensures we always have a non-null Shift object to work with.
        Shift shift = shiftService.getShiftByDate(date).orElseGet(Shift::new);

        // 2. Find yesterday's shift. This will return an Optional<Shift>.
        var yesterdayShiftOpt = shiftService.getShiftByDate(date.minusDays(1));

        // 3. Use the Optional to safely get values or use a default.
        // The 'map' function is only executed if yesterday's shift is present.
        // The 'orElse(0L)' provides a default value if it's not.
        long cashOnMorning = yesterdayShiftOpt.map(Shift::getCashOnEvening).orElse(0L);
        long cashKeyOnMorning = yesterdayShiftOpt.map(Shift::getCashKeyOnEvening).orElse(0L);

        shift.setCashOnMorning(cashOnMorning);
        shift.setCashKeyOnMorning(cashKeyOnMorning);

        return toShiftBO(shift);
    }

    @Override
    @Transient
    public void creatOrUpdate(ShiftRequestBO shiftBO) {
        var existShift = shiftService.getShiftByDateWithRows(shiftBO.getDate());
        var updateShift = enrichShiftDate(existShift, shiftBO);

        List<Long> employeeIds = shiftBO.getShiftRowBOs().stream()
                .map(ShiftRowRequestBO::getEmployeeId)
                .toList();
        Map<Long, Employee> employeeMap = employeeService.findAllByIds(employeeIds).stream()
                .collect(Collectors.toMap(Employee::getEmployeeId, Function.identity()));

        var shiftRows = toShiftRow(shiftBO.getShiftRowBOs(), employeeMap);
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

    @Override
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
