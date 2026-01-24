package com.svbd.svbd.dto.report;

import java.time.LocalDate;

public record ReportRequest(LocalDate dateFrom, LocalDate dateTo) {
}
