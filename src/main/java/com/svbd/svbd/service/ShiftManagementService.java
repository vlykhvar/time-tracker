package com.svbd.svbd.service;

import com.svbd.svbd.dto.settings.DinnerSettingBO;
import com.svbd.svbd.dto.shift.ShiftBO;
import com.svbd.svbd.dto.shift.ShiftRequestBO;

import java.time.LocalDate;

public interface ShiftManagementService {
    ShiftBO getShiftByDate(LocalDate date);
    void creatOrUpdate(ShiftRequestBO shiftBO);
    void updateShiftsByDinnerSettings(DinnerSettingBO dinnerSettingBO);
}
