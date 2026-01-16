package com.svbd.svbd.service;

import com.svbd.svbd.dto.settings.CompanySettingsBO;
import com.svbd.svbd.dto.settings.DinnerSettingBO;
import com.svbd.svbd.entity.CompanySettings;
import com.svbd.svbd.entity.DinnerSetting;
import com.svbd.svbd.exception.DinnerNotFoundException;
import com.svbd.svbd.exception.OverlapingDateException;
import com.svbd.svbd.repository.settings.CompanySettingsRepository;
import com.svbd.svbd.repository.settings.DinnerSettingRepository;
import com.svbd.svbd.util.DateTimeUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static com.svbd.svbd.converter.SettingsConverter.*;
import static com.svbd.svbd.util.DateTimeUtil.validateStartAndEndDate;
import static java.util.Objects.nonNull;

@Service
public class SettingsManagementService {

    private final CompanySettingsRepository companySettingsRepository;
    private final DinnerSettingRepository dinnerSettingRepository;

    public SettingsManagementService(CompanySettingsRepository companySettingsRepository, DinnerSettingRepository dinnerSettingRepository) {
        this.companySettingsRepository = companySettingsRepository;
        this.dinnerSettingRepository = dinnerSettingRepository;
    }

    public CompanySettingsBO getCompanySettings() {
        CompanySettings companySettings;
        var companySettingsOptional = companySettingsRepository.findFirstBy();
        if (companySettingsOptional.isEmpty()) {
            companySettings = new CompanySettings();
            companySettings.setCompanyName("SVBD");
            companySettings.setPassword("1234");
            companySettingsRepository.save(companySettings);
        } else {
            companySettings = companySettingsOptional.stream().findFirst().get();
        }
        return toCompanySettingsBO(companySettings);
    }

    public void saveCompanyName(String name) {
        var companySettings = companySettingsRepository.findFirstBy().get();
        companySettings.setCompanyName(name);
        companySettingsRepository.save(companySettings);
    }

    public void savePassword(String password) {
        var companySettings = companySettingsRepository.findFirstBy().get();
        companySettings.setPassword(password);
        companySettingsRepository.save(companySettings);
    }

    public List<DinnerSettingBO> getDinnerSettings() {
        return toDinnerSettingBOs(dinnerSettingRepository.findAll());
    }

    public void removeDinnerSettingById(Long dinnerSettingId) {
        dinnerSettingRepository.removeById(dinnerSettingId);
    }

    public void createDinnerSettings(DinnerSettingBO dinnerSettingBO) throws OverlapingDateException {
        var dinnerSettings = dinnerSettingRepository.findAll();
        var dinner = new DinnerSetting();
        dinner.setPrice(dinnerSettingBO.getPrice());
        dinner.setDateFrom(DateTimeUtil.parseLocalDate(dinnerSettingBO.getDateFrom()));
        validateOverlappingPeriod(dinner.getDateFrom(), dinnerSettings);
        dinnerSettings.add(dinner);
        if (dinnerSettings.size() > 1) {
            dinnerSettings = dinnerSettings.stream().sorted(Comparator.comparing(DinnerSetting::getDateFrom)).toList();
            for (int i = 0; i < dinnerSettings.size(); i++) {
                var currentSetting = dinnerSettings.get(i);
                if (nonNull(currentSetting.getDateTo()) || i == dinnerSettings.size() - 1) {
                    continue;
                }
                var nextSettings = dinnerSettings.get(i + 1);
                currentSetting.setDateTo(nextSettings.getDateFrom().minusDays(1));
            }
        }

        dinnerSettingRepository.saveAll(dinnerSettings);
    }

    public DinnerSettingBO getDinnerSettingForDay(LocalDate date) throws DinnerNotFoundException {
        var dinnerSetting = dinnerSettingRepository.findBetweenDateFromAndDateTo(date)
                .orElseThrow(DinnerNotFoundException::new);
        return toDinnerSettingBO(dinnerSetting);
    }

    public void updateDinnerSettings(Collection<DinnerSettingBO> dinnerSettingBOs) throws OverlapingDateException {
        var dinnerSettings = toDinnerSettings(dinnerSettingBOs);
        dinnerSettings = dinnerSettings.stream().sorted(Comparator.comparing(DinnerSetting::getDateFrom)).toList();
        for (int i = 0; i < dinnerSettings.size(); i++) {
            var currentSetting = dinnerSettings.get(i);
            if (!validateStartAndEndDate(currentSetting.getDateFrom(), currentSetting.getDateTo())) {
                throw new OverlapingDateException();
            }
            if (nonNull(currentSetting.getDateTo()) || i == dinnerSettings.size() - 1) {
                continue;
            }
            var nextSettings = dinnerSettings.get(i + 1);
            currentSetting.setDateTo(nextSettings.getDateFrom().minusDays(1));
        }
        isThereOverlappingDate(dinnerSettings);
        dinnerSettingRepository.saveAll(dinnerSettings);
    }

    /*Private methods*/

    private void validateOverlappingPeriod(LocalDate dateFrom, Collection<DinnerSetting> dinnerSettings) {
        for (var dinnerSetting : dinnerSettings) {
            if (dateFrom.isEqual(dinnerSetting.getDateFrom()) || nonNull(dinnerSetting.getDateTo()) &&
                    (dateFrom.isEqual(dinnerSetting.getDateTo()) ||
                    (dateFrom.isAfter(dinnerSetting.getDateFrom()) && dateFrom.isBefore(dinnerSetting.getDateTo())))) {
                throw new OverlapingDateException();
            }
        }
    }

    private void isThereOverlappingDate(Collection<DinnerSetting> dinnerSettings) throws OverlapingDateException {
        if (dinnerSettings.size() <= 1) {
            return;
        }
        var sortedDinnerSettings = dinnerSettings.stream()
                .sorted(Comparator.comparing(DinnerSetting::getDateFrom))
                .toList();

        for (int i = 1; i < sortedDinnerSettings.size(); i++) {
            if (isDateInRange(sortedDinnerSettings.get(0).getDateFrom(), sortedDinnerSettings.get(i)) ||
                    isDateInRange(sortedDinnerSettings.get(0).getDateTo(), sortedDinnerSettings.get(i))) {
                throw new OverlapingDateException();
            }
        }
        isThereOverlappingDate(sortedDinnerSettings.subList(1, dinnerSettings.size()));
    }

    private boolean isDateInRange(LocalDate date, DinnerSetting salary) {
        return date.isAfter(salary.getDateFrom()) && date.isAfter(salary.getDateTo()) &&
                !date.isEqual(salary.getDateFrom()) && !date.isEqual(salary.getDateTo());
    }
}
