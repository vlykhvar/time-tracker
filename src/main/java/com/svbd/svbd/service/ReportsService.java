package com.svbd.svbd.service;

import com.svbd.svbd.dto.report.MainReport;
import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.enums.ColorRgb;
import com.svbd.svbd.repository.projection.EmployShiftSalaryProjection;
import com.svbd.svbd.repository.settings.CompanySettingsRepository;
import com.svbd.svbd.repository.shift.ShiftRepository;
import com.svbd.svbd.repository.shift.ShiftRowRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.svbd.svbd.enums.ColorRgb.*;
import static com.svbd.svbd.util.ConstantUtil.EMPTY;
import static com.svbd.svbd.util.DateTimeUtil.formatDateForShowing;
import static com.svbd.svbd.util.DateTimeUtil.getStringHourAndMinuteFromLocalDateTime;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.poi.ss.usermodel.BorderStyle.*;
import static org.apache.poi.xssf.usermodel.XSSFFont.DEFAULT_FONT_NAME;

@Service
public class ReportsService {

    private static final String MAIN_REPORT_HEADER = "Звіт компанії %s з %s по %s";
    private static final String CASH_ON_MORNING_FIELD = "Каса на ранок";
    private static final String CASH_FIELD = "Каса прокату";
    private static final String DAILY_REVENUE_FIELD = "Каса магазину";
    private static final String CASH_ON_EVENING_FIELD = "Каса на вечір";
    private static final String CASH_KEY_ON_MORNING_FIELD = "Каса ключі на ранок";
    private static final String CASH_KEY_FIELD = "Каса ключі";
    private static final String CASH_KEY_ON_EVENING_FIELD = "Каса ключі на вечір";
    private static final String TAXI = "Таксі";

    private final ShiftRepository shiftRepository;
    private final ShiftRowRepository shiftRowRepository;
    private final CompanySettingsRepository companySettingsRepository;

    @Autowired
    public ReportsService(ShiftRepository shiftRepository, ShiftRowRepository shiftRowRepository, CompanySettingsRepository companySettingsRepository) {
        this.shiftRepository = shiftRepository;
        this.shiftRowRepository = shiftRowRepository;
        this.companySettingsRepository = companySettingsRepository;
    }

    public String generateMainRepost(MainReport request) throws IOException {
        var workbook = new XSSFWorkbook();
        var sheet = prepareHeader(workbook, request.dateFrom(), request.dateTo());

        prepareSecondRow(workbook, sheet, request.dateFrom(), request.dateTo());
        prepareEmployeeSalaryRows(workbook, sheet, request.dateFrom(), request.dateTo());
        prepareShiftRows(workbook, sheet, request.dateFrom(), request.dateTo());

        int noOfColumns = sheet.getRow(1).getPhysicalNumberOfCells();
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, noOfColumns - 1));
        var a = sheet.getRow(1);
        a.getPhysicalNumberOfCells();
        int b = 0;
        while (b < a.getPhysicalNumberOfCells()) {
            sheet.autoSizeColumn(b);
            b++;
        }
        sheet.createFreezePane(1, 0, 1, 0);

        var currDir = new File(".");
        var path = currDir.getAbsolutePath();
        var fileLocation = path.substring(0, path.length() - 1) + "temsp.xls";
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

    @Transactional(readOnly = true)
    public String generateDailyReport(LocalDate date) throws IOException {
        var shift = shiftRepository.findByIdWithShiftRows(date)
                .orElseThrow(() -> new NoSuchElementException("Shift not found for date: " + date));

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
            cell.setCellValue("Дата зміни " + formatDateForShowing(shift.getShiftDate()));
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

            // Auto-size columns
            for (int i = 0; i <= 6; i++) {
                sheet.autoSizeColumn(i);
            }

            // --- Write to File ---
            var currDir = new File(".");
            var path = currDir.getAbsolutePath();
            var fileLocation = path.substring(0, path.length() - 1) + "daily.xlsx";
            try (var outputStream = new FileOutputStream(fileLocation)) {
                workbook.write(outputStream);
            }
            return fileLocation;
        }
    }

    private void createStyledCell(Row row, int colNum, String value, CellStyle style) {
        Cell cell = row.createCell(colNum);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createStyledCell(Sheet sheet, int rowNum, int colNum, String value, CellStyle style) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        createStyledCell(row, colNum, value, style);
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
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
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


    /* Private Methods */

    private Sheet prepareHeader(XSSFWorkbook workbook, LocalDate from, LocalDate to) {
        var companyName = companySettingsRepository.getCompanyName();
        Sheet sheet = workbook.createSheet("report");
        Row header = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName(DEFAULT_FONT_NAME);
        font.setFontHeightInPoints((short) 15);
        font.setBold(true);
        font.setColor(new XSSFColor(BAHAMA_BLUE.getRgbColor(), new DefaultIndexedColorMap()));
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        Cell headerCell = header.createCell(1);
        headerCell.setCellValue(String.format(MAIN_REPORT_HEADER, companyName, from, to));
        headerCell.setCellStyle(headerStyle);
        return sheet;
    }

    private void prepareSecondRow(Workbook workbook, Sheet sheet, LocalDate from, LocalDate to) {
        var currentDateForRow = from;
        var header = sheet.createRow(1);
        var cellStyle = prepareCellStyle(workbook, LIGHT_GRAYISH_BLUE, 11, false, MEDIUM);
        int i = 0;
        while (currentDateForRow.isBefore(to) || currentDateForRow.isEqual(to)) {
            Cell rowCell = header.createCell(i);
            rowCell.setCellStyle(cellStyle);
            if (i == 0) {
                rowCell.setCellValue("ПІБ співробітника/Дата");
                i++;
                continue;
            } else if (currentDateForRow.getDayOfMonth() == 15) {
                rowCell.setCellValue(formatDateForShowing(currentDateForRow));
                i++;
                rowCell = header.createCell(i);
                rowCell.setCellStyle(prepareCellStyle(workbook, LIGHT_GRAYISH_BLUE, 11, true, MEDIUM));
                rowCell.setCellValue("Заробітня плата за першу половину місяця");
            } else if (currentDateForRow.isEqual(
                    currentDateForRow.withDayOfMonth(
                            currentDateForRow.getMonth().length(currentDateForRow.isLeapYear())))) {
                rowCell.setCellValue(formatDateForShowing(currentDateForRow));
                i++;
                rowCell = header.createCell(i);
                rowCell.setCellValue("Заробітня плата за другу половину місяця");
                rowCell.setCellStyle(cellStyle);
                i++;
                rowCell = header.createCell(i);
                rowCell.setCellValue("Всього за місяць");
                rowCell.setCellStyle(prepareCellStyle(workbook, LIGHT_GRAYISH_BLUE, 11, true, MEDIUM));
            } else {
                rowCell.setCellValue(formatDateForShowing(currentDateForRow));
            }
            currentDateForRow = currentDateForRow.plusDays(1);
            i++;
        }
    }

    private void prepareEmployeeSalaryRows(Workbook workbook, Sheet sheet, LocalDate from, LocalDate to) {
        var siftRows = shiftRowRepository.findEmployeeShiftSalariesForPeriod(from, to);
        var siftRowsByEmployeeAndDate = new HashMap<Long, HashMap<LocalDate, EmployShiftSalaryProjection>>();
        siftRows.forEach(siftRow -> {
            if (siftRowsByEmployeeAndDate.containsKey(siftRow.getEmployeeId())) {
                var shiftRowsByDate = siftRowsByEmployeeAndDate.get(siftRow.getEmployeeId());
                shiftRowsByDate.put(siftRow.getShiftDate(), siftRow);
            } else {
                var shiftRowsByDate = new HashMap<LocalDate, EmployShiftSalaryProjection>();
                shiftRowsByDate.put(siftRow.getShiftDate(), siftRow);
                siftRowsByEmployeeAndDate.put(siftRow.getEmployeeId(), shiftRowsByDate);
            }
        });
        var employeeNameById = new HashMap<Long, String>();
        siftRows.forEach(employShiftSalaryProjection -> employeeNameById.put(
                employShiftSalaryProjection.getEmployeeId(), employShiftSalaryProjection.getName()));

        int rowCount = 2;
        int cellCount = 0;
        for (var key : siftRowsByEmployeeAndDate.keySet()) {
            var row = sheet.createRow(rowCount);
            var currentDate = from;
            var employeeShiftByDate = siftRowsByEmployeeAndDate.get(key);
            long halfMountTotal = 0;
            long mountTotal = 0;
            while (currentDate.isBefore(to) || currentDate.isEqual(to)) {
                var cell = row.createCell(cellCount);
                cell.setCellStyle(prepareCellStyle(workbook, BRIGHT_YELLOW, 11, false, THIN));

                if (cellCount == 0) {
                    cell.setCellValue(employeeNameById.get(key));
                    cellCount++;
                    continue;
                }

                var employeeDaySalary = getSalaryForDate(employeeShiftByDate, currentDate);
                cell.setCellValue(employeeDaySalary);
                halfMountTotal = halfMountTotal + employeeDaySalary;
                mountTotal = mountTotal + employeeDaySalary;

                if (currentDate.getDayOfMonth() == 15) {
                    cellCount++;
                    cell = row.createCell(cellCount);
                    cell.setCellValue(halfMountTotal);
                    cell.setCellStyle(prepareCellStyle(workbook, VERY_SOFT_MAGENTA, 11, true, THIN));
                    halfMountTotal = 0;
                } else if (currentDate.isEqual(currentDate.withDayOfMonth(
                        currentDate.getMonth().length(currentDate.isLeapYear())))) {
                    cellCount++;
                    cell = row.createCell(cellCount);
                    cell.setCellValue(halfMountTotal);
                    cell.setCellStyle(prepareCellStyle(workbook, VERY_SOFT_MAGENTA, 11, true, THIN));
                    halfMountTotal = 0;
                    cellCount++;
                    cell = row.createCell(cellCount);
                    cell.setCellValue(mountTotal);
                    cell.setCellStyle(prepareCellStyle(workbook, VERY_SOFT_MAGENTA, 11, true, THIN));
                    mountTotal = 0;
                }

                currentDate = currentDate.plusDays(1);
                cellCount++;
            }

            cellCount = 0;
            rowCount++;
        }
        prepareTotalColumn(workbook, sheet, from, to, siftRows);
    }

    private void prepareTotalColumn(Workbook workbook, Sheet sheet, LocalDate from, LocalDate to,
                                    List<EmployShiftSalaryProjection> employeeShifts) {
        var row = sheet.createRow(sheet.getLastRowNum() + 1);
        var rowCell = row.createCell(0);
        rowCell.setCellStyle(prepareCellStyle(workbook, LIGHT_GRAYISH_BLUE, 11, true, MEDIUM));
        rowCell.setCellValue("Разом");
        int cellCount = 1;
        LocalDate currentDate = from;
        while (currentDate.isBefore(to) || currentDate.isEqual(to)) {
            rowCell = row.createCell(cellCount);
            rowCell.setCellValue(EMPTY);
            rowCell.setCellStyle(prepareCellStyle(workbook, LIGHT_GRAYISH_BLUE, 11, true, NONE));
            if (currentDate.getDayOfMonth() == 15) {
                cellCount++;
                rowCell = row.createCell(cellCount);
                LocalDate finalCurrentDate = currentDate;
                var halfMountTotal = employeeShifts.stream()
                        .filter(employeeShift -> employeeShift.getShiftDate().isAfter(from) ||
                                employeeShift.getShiftDate().isEqual(from))
                        .filter(employeeShift ->
                                employeeShift.getShiftDate().getMonth().equals(finalCurrentDate.getMonth()))
                        .filter(employeeShift -> employeeShift.getShiftDate().isBefore(finalCurrentDate) ||
                                employeeShift.getShiftDate().isEqual(finalCurrentDate))
                        .map(EmployShiftSalaryProjection::getSalary)
                        .reduce(0L, Long::sum);
                rowCell.setCellValue(halfMountTotal);
                rowCell.setCellStyle(prepareCellStyle(workbook, VERY_SOFT_MAGENTA, 11, true, MEDIUM));
            } else if (currentDate.isEqual(currentDate.withDayOfMonth(
                    currentDate.getMonth().length(currentDate.isLeapYear())))) {
                cellCount++;
                rowCell = row.createCell(cellCount);
                LocalDate finalCurrentDate = currentDate;
                var halfMountTotal = employeeShifts.stream()
                        .filter(employeeShift -> employeeShift.getShiftDate().getDayOfMonth() > 15)
                        .filter(employeeShift ->
                                employeeShift.getShiftDate().getMonth().equals(finalCurrentDate.getMonth()))
                        .filter(employeeShift -> employeeShift.getShiftDate().isBefore(finalCurrentDate) ||
                                employeeShift.getShiftDate().isEqual(finalCurrentDate))
                        .map(EmployShiftSalaryProjection::getSalary)
                        .reduce(0L, Long::sum);
                rowCell.setCellValue(halfMountTotal);
                rowCell.setCellStyle(prepareCellStyle(workbook, VERY_SOFT_MAGENTA, 11, true, MEDIUM));
                cellCount++;
                rowCell = row.createCell(cellCount);
                var mountTotal = employeeShifts.stream()
                        .filter(employeeShift ->
                                employeeShift.getShiftDate().getMonth().equals(finalCurrentDate.getMonth()))
                        .map(EmployShiftSalaryProjection::getSalary)
                        .reduce(0L, Long::sum);
                rowCell.setCellValue(mountTotal);
                rowCell.setCellStyle(prepareCellStyle(workbook, VERY_SOFT_MAGENTA, 11, true, MEDIUM));
            }
            currentDate = currentDate.plusDays(1);
            cellCount++;
        }

    }

    private void prepareShiftRows(Workbook workbook, Sheet sheet, LocalDate from, LocalDate to) {
        var currentDateForRow = from;
        var shifts = shiftRepository.findAllInPeriodWithShiftRows(from, to);
        var shiftByDate = shifts.stream()
                .collect(Collectors.toMap(Shift::getShiftDate, x -> x));
        var rowTaxi = sheet.createRow(sheet.getLastRowNum() + 1);
        var totalDinner = sheet.createRow(sheet.getLastRowNum() + 1);
        var shiftCash = sheet.createRow(sheet.getLastRowNum() + 1);
        var keyCash = sheet.createRow(sheet.getLastRowNum() + 1);
        var dailyRevenue = sheet.createRow(sheet.getLastRowNum() + 1);
        var totalCash = sheet.createRow(sheet.getLastRowNum() + 1);
        int cellPosition = 0;
        Cell cell = null;
        var cells = new ArrayList<Cell>();
        while (currentDateForRow.isBefore(to) || currentDateForRow.isEqual(to)) {
            if (cellPosition == 0) {
                cells.clear();
                cell = rowTaxi.createCell(cellPosition);
                cell.setCellValue("Таксі");
                cells.add(cell);
                cell = shiftCash.createCell(cellPosition);
                cell.setCellValue("Каса проката");
                cells.add(cell);
                cell = keyCash.createCell(cellPosition);
                cell.setCellValue("Каса ключи");
                cells.add(cell);
                cell = dailyRevenue.createCell(cellPosition);
                cell.setCellValue("Какса магазину");
                cells.add(cell);
                cell = totalCash.createCell(cellPosition);
                cell.setCellValue("Загальний виторг");
                cells.add(cell);
                cell = totalDinner.createCell(cellPosition);
                cell.setCellValue("Витрати на обід");
                cells.add(cell);
                cells.forEach(cellForStyles -> cellForStyles.setCellStyle(
                        prepareCellStyle(workbook, LIGHT_GRAYISH_BLUE, 11, true, MEDIUM)));
                cellPosition++;
                continue;
            }

            var currentShift = shiftByDate.get(currentDateForRow);
            cells.clear();
            cell = rowTaxi.createCell(cellPosition);
            cell.setCellValue(nonNull(currentShift) ? currentShift.getTaxi() : 0);
            cells.add(cell);
            cell = shiftCash.createCell(cellPosition);
            cell.setCellValue(nonNull(currentShift) ? currentShift.getTotalCash() : 0);
            cells.add(cell);
            cell = keyCash.createCell(cellPosition);
            cell.setCellValue(nonNull(currentShift) ? currentShift.getCashKeyTotal() : 0);
            cells.add(cell);
            cell = dailyRevenue.createCell(cellPosition);
            cell.setCellValue(nonNull(currentShift) && nonNull(currentShift.getDailyRevenue()) ? currentShift.getDailyRevenue() : 0);
            cells.add(cell);
            cell = totalCash.createCell(cellPosition);
            cell.setCellValue(nonNull(currentShift) ? currentShift.getCashKeyTotal() + currentShift.getTotalCash() + currentShift.getDailyRevenue() : 0);
            cells.add(cell);
            cell = totalDinner.createCell(cellPosition);
            cell.setCellValue(nonNull(currentShift) ? currentShift.getTotalDinner() : 0);
            cells.add(cell);
            cells.forEach(cellForStyles -> cellForStyles.setCellStyle(
                    prepareCellStyle(workbook, BRIGHT_YELLOW, 11, false, THIN)));
            if (currentDateForRow.getDayOfMonth() == 15) {
                LocalDate finalCurrentDateForRow = currentDateForRow;
                var shiftInCurrentPeriods = shifts.stream()
                        .filter(employeeShift -> employeeShift.getShiftDate().isAfter(from) ||
                                employeeShift.getShiftDate().isEqual(from))
                        .filter(employeeShift ->
                                employeeShift.getShiftDate().getMonth().equals(finalCurrentDateForRow.getMonth()))
                        .filter(employeeShift -> employeeShift.getShiftDate().isBefore(finalCurrentDateForRow) ||
                                employeeShift.getShiftDate().isEqual(finalCurrentDateForRow))
                        .collect(Collectors.toSet());
                cellPosition++;
                cells.clear();
                cell = rowTaxi.createCell(cellPosition);
                cell.setCellValue(shiftInCurrentPeriods.stream()
                        .map(Shift::getTaxi)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = shiftCash.createCell(cellPosition);
                cell.setCellValue(shiftInCurrentPeriods.stream()
                        .map(Shift::getTotalCash)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = keyCash.createCell(cellPosition);
                cell.setCellValue(shiftInCurrentPeriods.stream()
                        .map(Shift::getCashKeyTotal)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = dailyRevenue.createCell(cellPosition);
                cell.setCellValue(shiftInCurrentPeriods.stream()
                        .map(Shift::getDailyRevenue)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = totalCash.createCell(cellPosition);
                cell.setCellValue(shiftInCurrentPeriods.stream()
                        .map(shift -> shift.getTotalCash() + shift.getCashKeyTotal() + shift.getDailyRevenue())
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = totalDinner.createCell(cellPosition);
                cell.setCellValue(shiftInCurrentPeriods.stream()
                        .map(Shift::getTotalDinner)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cells.forEach(cellForStyles -> cellForStyles.setCellStyle(
                        prepareCellStyle(workbook, VERY_SOFT_MAGENTA, 11, true, MEDIUM)));
            } else if (currentDateForRow.isEqual(currentDateForRow.withDayOfMonth(
                    currentDateForRow.getMonth().length(currentDateForRow.isLeapYear())))) {
                LocalDate finalCurrentDateForRow = currentDateForRow;
                var shiftInCurrentPeriods = shifts.stream()
                        .filter(employeeShift -> employeeShift.getShiftDate().getDayOfMonth() > 15)
                        .filter(employeeShift ->
                                employeeShift.getShiftDate().getMonth().equals(finalCurrentDateForRow.getMonth()))
                        .filter(employeeShift -> employeeShift.getShiftDate().isBefore(finalCurrentDateForRow) ||
                                employeeShift.getShiftDate().isEqual(finalCurrentDateForRow))
                        .collect(Collectors.toSet());
                cellPosition++;
                cells.clear();
                cell = rowTaxi.createCell(cellPosition);
                cell.setCellValue(shiftInCurrentPeriods.stream()
                        .map(Shift::getTaxi)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = shiftCash.createCell(cellPosition);
                cell.setCellValue(shiftInCurrentPeriods.stream()
                        .map(Shift::getTotalCash)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = keyCash.createCell(cellPosition);
                cell.setCellValue(shiftInCurrentPeriods.stream()
                        .map(Shift::getCashKeyTotal)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = dailyRevenue.createCell(cellPosition);
                cell.setCellValue(shiftInCurrentPeriods.stream()
                        .map(Shift::getDailyRevenue)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = totalCash.createCell(cellPosition);
                cell.setCellValue(shiftInCurrentPeriods.stream()
                        .map(shift -> shift.getTotalCash() + shift.getCashKeyTotal() + shift.getDailyRevenue())
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = totalDinner.createCell(cellPosition);
                cell.setCellValue(shiftInCurrentPeriods.stream()
                        .map(Shift::getTotalDinner)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                var shiftsInCurrentMonth = shifts.stream()
                        .filter(employeeShift ->
                                employeeShift.getShiftDate().getMonth().equals(finalCurrentDateForRow.getMonth()))
                        .collect(Collectors.toSet());
                cellPosition++;
                cell = rowTaxi.createCell(cellPosition);
                cell.setCellValue(shiftsInCurrentMonth.stream()
                        .map(Shift::getTaxi)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = shiftCash.createCell(cellPosition);
                cell.setCellValue(shiftsInCurrentMonth.stream()
                        .map(Shift::getTotalCash)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = keyCash.createCell(cellPosition);
                cell.setCellValue(shiftsInCurrentMonth.stream()
                        .map(Shift::getCashKeyTotal)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = dailyRevenue.createCell(cellPosition);
                cell.setCellValue(shiftsInCurrentMonth.stream()
                        .map(Shift::getDailyRevenue)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = totalCash.createCell(cellPosition);
                cell.setCellValue(shiftsInCurrentMonth.stream()
                        .map(shift -> shift.getTotalCash() + shift.getCashKeyTotal() + shift.getDailyRevenue())
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cell = totalDinner.createCell(cellPosition);
                cell.setCellValue(shiftsInCurrentMonth.stream()
                        .map(Shift::getTotalDinner)
                        .reduce(0L, Long::sum));
                cells.add(cell);
                cells.forEach(cellForStyles -> cellForStyles.setCellStyle(
                        prepareCellStyle(workbook, VERY_SOFT_MAGENTA, 11, true, MEDIUM)));
            }

            cellPosition++;
            currentDateForRow = currentDateForRow.plusDays(1);
        }

    }

    private Long getSalaryForDate(Map<LocalDate, EmployShiftSalaryProjection> employeeShiftByDate, LocalDate date) {
        return employeeShiftByDate.containsKey(date) ? employeeShiftByDate.get(date).getSalary() : 0;
    }

    private CellStyle prepareCellStyle(Workbook workbook, ColorRgb colorRgb,
                                       int fontHeight, Boolean bold, BorderStyle borderStyle) {
        var cellStyle = workbook.createCellStyle();
        if (nonNull(colorRgb)) {
            cellStyle.setFillForegroundColor(new XSSFColor(colorRgb.getRgbColor(), new DefaultIndexedColorMap()));
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName(DEFAULT_FONT_NAME);
        font.setFontHeightInPoints((short) fontHeight);
        font.setBold(bold);
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setBorderRight(borderStyle);
        return cellStyle;
    }
}
