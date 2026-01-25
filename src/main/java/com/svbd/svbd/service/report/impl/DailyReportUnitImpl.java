package com.svbd.svbd.service.report.impl;

import com.svbd.svbd.dto.report.ReportRequest;
import com.svbd.svbd.enums.EReportType;
import com.svbd.svbd.repository.settings.CompanySettingsRepository;
import com.svbd.svbd.repository.shift.ShiftRepository;
import com.svbd.svbd.service.report.ReportingUnit;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.NoSuchElementException;

import static com.svbd.svbd.util.DateTimeUtil.formatDateForShowing;
import static com.svbd.svbd.util.DateTimeUtil.getStringHourAndMinuteFromLocalDateTime;
import static java.util.Objects.isNull;

@Service
public class DailyReportUnitImpl implements ReportingUnit {

    private static final String CASH_ON_MORNING_FIELD = "Каса на ранок";
    private static final String CASH_FIELD = "Каса прокату";
    private static final String DAILY_REVENUE_FIELD = "Каса магазину";
    private static final String CASH_ON_EVENING_FIELD = "Каса на вечір";
    private static final String CASH_KEY_ON_MORNING_FIELD = "Каса ключі на ранок";
    private static final String CASH_KEY_FIELD = "Каса ключі";
    private static final String CASH_KEY_ON_EVENING_FIELD = "Каса ключі на вечір";
    private static final String TAXI = "Таксі";

    private final ShiftRepository shiftRepository;
    private final CompanySettingsRepository companySettingsRepository;

    @Autowired
    public DailyReportUnitImpl(ShiftRepository shiftRepository, CompanySettingsRepository companySettingsRepository) {
        this.shiftRepository = shiftRepository;
        this.companySettingsRepository = companySettingsRepository;
    }

    @Override
    public EReportType getReportType() {
        return EReportType.DAILY;
    }

    @Transactional(readOnly = true)
    public String generateReport(ReportRequest request) throws Exception {
        var date = request.dateFrom();
        var shift = shiftRepository.findByIdWithShiftRows(date)
                .orElseThrow(() -> new NoSuchElementException("Shift not found for date: " + date));
        var companyName = companySettingsRepository.getCompanyName();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet(formatDateForShowing(shift.getShiftDate()));
            sheet.setPrintGridlines(false);

            // --- Create Cell Styles ---
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook, HorizontalAlignment.CENTER);
            CellStyle commentHeaderStyle = createHeaderStyle(workbook); // Same as header but can be customized
            CellStyle commentDataStyle = createDataStyle(workbook, HorizontalAlignment.LEFT);
            commentDataStyle.setWrapText(true);

            // --- Build Report ---
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("Дата зміни " + formatDateForShowing(shift.getShiftDate()) + ". Підрозділ: " + companyName);
            cell.setCellStyle(titleStyle);

            row = sheet.createRow(1);
            createStyledCell(row, 2, "ПІБ", headerStyle);
            createStyledCell(row, 4, "Бонус години", headerStyle);
            createStyledCell(row, 6, String.valueOf(shift.getBonusTime()), dataStyle);

            row = sheet.createRow(2);
            createStyledCell(row, 3, "Розпочав", headerStyle);
            createStyledCell(row, 4, "Закінчив", headerStyle);
            createStyledCell(row, 5, "Робочих годин", headerStyle);
            createStyledCell(row, 6, "Всього годин", headerStyle);

            // Cash block
            createStyledCell(sheet, 3, 0, CASH_ON_MORNING_FIELD, headerStyle);
            createStyledCell(sheet, 3, 1, String.valueOf(shift.getCashOnMorning()), dataStyle);
            createStyledCell(sheet, 4, 0, CASH_FIELD, headerStyle);
            createStyledCell(sheet, 4, 1, String.valueOf(shift.getTotalCash()), dataStyle);
            createStyledCell(sheet, 5, 0, CASH_ON_EVENING_FIELD, headerStyle);
            createStyledCell(sheet, 5, 1, String.valueOf(shift.getCashOnEvening()), dataStyle);
            createStyledCell(sheet, 6, 0, CASH_KEY_ON_MORNING_FIELD, headerStyle);
            createStyledCell(sheet, 6, 1, String.valueOf(shift.getCashKeyOnMorning()), dataStyle);
            createStyledCell(sheet, 7, 0, CASH_KEY_FIELD, headerStyle);
            createStyledCell(sheet, 7, 1, String.valueOf(shift.getCashKeyTotal()), dataStyle);
            createStyledCell(sheet, 8, 0, CASH_KEY_ON_EVENING_FIELD, headerStyle);
            createStyledCell(sheet, 8, 1, String.valueOf(shift.getCashKeyOnEvening()), dataStyle);
            createStyledCell(sheet, 9, 0, DAILY_REVENUE_FIELD, headerStyle);
            createStyledCell(sheet, 9, 1, String.valueOf(shift.getDailyRevenue()), dataStyle);
            createStyledCell(sheet, 10, 0, TAXI, headerStyle);
            createStyledCell(sheet, 10, 1, String.valueOf(shift.getTaxi()), dataStyle);

            var startRow = 3;
            var sortedRows = shift.getShiftRows().stream()
                    .sorted(Comparator.comparing(x -> x.getEmployee().getName()))
                    .toList();

            for (var shiftRow : sortedRows) {
                if (shiftRow.getTotalTime() == 0) {
                    continue;
                }
                row = sheet.getRow(startRow);
                if (isNull(row)) {
                    row = sheet.createRow(startRow);
                }
                createStyledCell(row, 2, shiftRow.getEmployee().getName(), dataStyle);
                createStyledCell(row, 3, getStringHourAndMinuteFromLocalDateTime(shiftRow.getStartShift()), dataStyle);
                createStyledCell(row, 4, getStringHourAndMinuteFromLocalDateTime(shiftRow.getEndShift()), dataStyle);
                createStyledCell(row, 5, String.valueOf(shiftRow.getTotalTime()), dataStyle);
                createStyledCell(row, 6, String.valueOf(shiftRow.getTotalTime() + shift.getBonusTime()), dataStyle);
                startRow++;
            }

            // Comments
            int commentStartRow = Math.max(startRow, 11);
            row = sheet.createRow(commentStartRow);
            createStyledCell(row, 0, "Комметарі до змінни", commentHeaderStyle);
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 6));

            row = sheet.createRow(commentStartRow + 1);
            cell = row.createCell(0);
            cell.setCellValue(shift.getComments());
            cell.setCellStyle(commentDataStyle);
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum() + 3, 0, 6));

            // Merged regions (existing logic)
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, 5));
            sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(1, 2, 2, 2));

            makePretty(sheet);

            // --- Write to File ---
            var currDir = new File(".");
            var path = currDir.getAbsolutePath();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("%s daily report %s .xls", timestamp, request.dateFrom());
            var fileLocation = path.substring(0, path.length() - 1) + fileName;
            var outputStream = new FileOutputStream(fileLocation);
            try {
                workbook.write(outputStream);
                workbook.close();
                outputStream.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                workbook.close();
                outputStream.close();
            }
            return fileLocation;
        }
    }

    private static void makePretty(XSSFSheet sheet) {
        int maxColumns = 0;
        for (Row row : sheet) {
            if (row.getLastCellNum() > maxColumns) {
                maxColumns = row.getLastCellNum();
            }
        }
        for (int i = 0; i <= maxColumns; i++) {
            sheet.autoSizeColumn(i);
        }

        int lastRow = sheet.getLastRowNum() + 3;
        int maxCol = 0;
        for (int i = 0; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (row != null && row.getLastCellNum() > maxCol) {
                maxCol = row.getLastCellNum();
            }
        }

        PropertyTemplate pt = new PropertyTemplate();

        CellRangeAddress region = new CellRangeAddress(0, lastRow, 0, maxCol - 1);

        pt.drawBorders(region, BorderStyle.THIN, IndexedColors.BLACK.getIndex(), BorderExtent.ALL);

        pt.applyBorders(sheet);
    }

    private void createStyledCell(Sheet sheet, int rowNum, int colNum, String value, CellStyle style) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        createStyledCell(row, colNum, value, style);
    }

    private void createStyledCell(Row row, int colNum, String value, CellStyle style) {
        Cell cell = row.createCell(colNum);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void setBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        Font titleFont = workbook.createFont();
        titleFont.setFontName("Calibri");
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return titleStyle;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        Font headerFont = workbook.createFont();
        headerFont.setFontName("Calibri");
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(headerStyle);
        return headerStyle;
    }

    private CellStyle createDataStyle(Workbook workbook, HorizontalAlignment alignment) {
        Font dataFont = workbook.createFont();
        dataFont.setFontName("Calibri");
        dataFont.setFontHeightInPoints((short) 11);
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setFont(dataFont);
        dataStyle.setAlignment(alignment);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(dataStyle);
        return dataStyle;
    }
}
