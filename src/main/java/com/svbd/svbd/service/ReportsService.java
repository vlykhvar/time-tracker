package com.svbd.svbd.service;

import com.svbd.svbd.dto.report.ReportRequest;
import com.svbd.svbd.enums.EReportType;

import java.io.IOException;
import java.time.LocalDate;

public interface ReportsService {

    String generateReport(EReportType eReportType, ReportRequest request) throws Exception;

}
