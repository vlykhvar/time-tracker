package com.svbd.svbd.service;

import com.svbd.svbd.dto.report.MainReport;

import java.io.IOException;
import java.time.LocalDate;

public interface ReportsService {
    String generateMainRepost(MainReport request) throws IOException;
    String generateDailyReport(LocalDate date) throws IOException;
}
