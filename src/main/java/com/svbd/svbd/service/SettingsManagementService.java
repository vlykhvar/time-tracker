package com.svbd.svbd.service;

import com.svbd.svbd.dto.settings.CompanySettingsBO;
import com.svbd.svbd.dto.settings.DinnerSettingBO;
import com.svbd.svbd.exception.DinnerNotFoundException;
import com.svbd.svbd.exception.OverlapingDateException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface SettingsManagementService {
    CompanySettingsBO getCompanySettings();
    void saveCompanyName(String name);
    void savePassword(String password);
    List<DinnerSettingBO> getDinnerSettings();
    void removeDinnerSettingById(Long dinnerSettingId);
    void createDinnerSettings(DinnerSettingBO dinnerSettingBO) throws OverlapingDateException;
    DinnerSettingBO getDinnerSettingForDay(LocalDate date) throws DinnerNotFoundException;
    void updateDinnerSettings(Collection<DinnerSettingBO> dinnerSettingBOs) throws OverlapingDateException;
}
