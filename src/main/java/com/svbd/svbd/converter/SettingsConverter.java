package com.svbd.svbd.converter;

import com.svbd.svbd.dto.settings.CompanySettingsBO;
import com.svbd.svbd.dto.settings.DinnerSettingBO;
import com.svbd.svbd.entity.CompanySettings;
import com.svbd.svbd.entity.DinnerSetting;

import java.util.Collection;
import java.util.List;

import static com.svbd.svbd.util.DateTimeUtil.formatDateForShowing;
import static com.svbd.svbd.util.DateTimeUtil.parseLocalDate;

public final class SettingsConverter {

    private SettingsConverter(){
    }

    public static CompanySettingsBO toCompanySettingsBO(CompanySettings companySettings) {
        var companySettingsBO = new CompanySettingsBO();
        companySettingsBO.setCompanyName(companySettings.getCompanyName());
        companySettingsBO.setCompanyPassword(companySettings.getPassword());
        return companySettingsBO;
    }

    public static DinnerSettingBO toDinnerSettingBO(DinnerSetting dinnerSetting) {
        var dinnerSettingBO = new DinnerSettingBO();
        dinnerSettingBO.setId(dinnerSetting.getId());
        dinnerSettingBO.setDateFrom(formatDateForShowing(dinnerSetting.getDateFrom()));
        dinnerSettingBO.setDateTo(formatDateForShowing(dinnerSetting.getDateTo()));
        dinnerSettingBO.setPrice(dinnerSetting.getPrice());
        return dinnerSettingBO;
    }

    public static List<DinnerSettingBO> toDinnerSettingBOs(Collection<DinnerSetting> dinnerSettings) {
        return dinnerSettings.stream()
                .map(SettingsConverter::toDinnerSettingBO)
                .toList();
    }

    public static DinnerSetting toDinnerSetting(DinnerSettingBO dinnerSettingBO) {
        var dinnerSetting = new DinnerSetting();
        dinnerSetting.setId(dinnerSettingBO.getId());
        dinnerSetting.setDateFrom(parseLocalDate(dinnerSettingBO.getDateFrom()));
        dinnerSetting.setDateTo(parseLocalDate(dinnerSettingBO.getDateTo()));
        dinnerSetting.setPrice(dinnerSettingBO.getPrice());
        return dinnerSetting;
    }

    public static List<DinnerSetting> toDinnerSettings(Collection<DinnerSettingBO> dinnerSettingBOs) {
        return dinnerSettingBOs.stream()
                .map(SettingsConverter::toDinnerSetting)
                .toList();
    }
}
