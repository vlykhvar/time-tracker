package com.svbd.svbd.service.report;

import com.svbd.svbd.dto.report.ReportRequest;
import com.svbd.svbd.enums.EReportType;

public interface ReportingUnit {

    EReportType getReportType();

    String generateReport(ReportRequest request) throws Exception;
}
