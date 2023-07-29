package com.svbd.svbd.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.util.Objects.isNull;
import static jdk.internal.joptsimple.internal.Strings.EMPTY;

public final class DateTimeUtil {

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
}
