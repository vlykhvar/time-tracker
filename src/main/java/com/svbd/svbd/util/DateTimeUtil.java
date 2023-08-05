package com.svbd.svbd.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.svbd.svbd.util.ConstantUtil.EMPTY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class DateTimeUtil {

    private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private DateTimeUtil() {
    }

    public static LocalDateTime toLocalDateTime(LocalDate date, String time) {
        return LocalDateTime.of(date, LocalTime.parse(time));
    }

    public static String getStringHourAndMinuteFromLocalDateTime(LocalDateTime localDateTime) {
        if (isNull(localDateTime)) {
            return EMPTY;
        }
        var hourString = localDateTime.getHour() < 10 ? "0" + localDateTime.getHour() : localDateTime.getHour();
        var minuteString = localDateTime.getMinute() < 10 ? localDateTime.getMinute() + "0" : localDateTime.getMinute();
        return hourString + ":" + minuteString;
    }

    public static String formatDateForShowing(LocalDate date) {
        return isNull(date) ? EMPTY : date.format(DATE_PATTERN);
    }

    public static LocalDate parseLocalDate(String date) {
        return isNull(date) || date.isEmpty() ? null : LocalDate.parse(date, DATE_PATTERN);
    }

    public static Integer prepareWorkTotalTime(LocalDate shiftDate,
                                               LocalTime startEmployeeShift,
                                               LocalTime endEmployeeShift) {
        var startShiftLocalDateTime = LocalDateTime.of(shiftDate, startEmployeeShift);
        LocalDateTime endShiftLocalDateTime;
        if (endEmployeeShift.getHour() > 0 && endEmployeeShift.getHour() < 8) {
            endShiftLocalDateTime = LocalDateTime.of(shiftDate.plusDays(1L), endEmployeeShift);
        } else {
            endShiftLocalDateTime = LocalDateTime.of(shiftDate, endEmployeeShift);
        }
        return prepareWorkTotalTime(startShiftLocalDateTime, endShiftLocalDateTime);
    }

    public static Integer prepareWorkTotalTime(LocalDateTime startEmployeeShift,
                                               LocalDateTime endEmployeeShift) {
        return nonNull(startEmployeeShift) && nonNull (endEmployeeShift) ?
                (int) ChronoUnit.HOURS.between(startEmployeeShift, endEmployeeShift) :
                0;
    }

    public static LocalDateTime prepareNightShiftEndDate(LocalDate shiftDate, String endTime) {
        var localTime = LocalTime.parse(endTime);
        LocalDateTime endShiftLocalDateTime;
        if (localTime.getHour() > 0 && localTime.getHour() < 8) {
            endShiftLocalDateTime = LocalDateTime.of(shiftDate.plusDays(1L), localTime);
        } else {
            endShiftLocalDateTime = LocalDateTime.of(shiftDate, localTime);
        }
        return endShiftLocalDateTime;
    }
}
