package com.svbd.svbd.service.impl;

import com.svbd.svbd.dto.report.ReportRequest;
import com.svbd.svbd.enums.EReportType;
import com.svbd.svbd.service.ReportsService;
import com.svbd.svbd.service.report.ReportingUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReportsServiceImpl implements ReportsService {

    private final Map<EReportType, ReportingUnit> reportByType = new HashMap<>();

    @Autowired
    public ReportsServiceImpl(Collection<ReportingUnit> reportingUnits) {
        reportingUnits.forEach(reportingUnit ->
                reportByType.put(reportingUnit.getReportType(), reportingUnit));
    }

    @Override
    public String generateReport(EReportType eReportType, ReportRequest request) throws Exception {
        return reportByType.get(eReportType).generateReport(request);
    }
}
