package com.svbd.svbd.util;

import java.util.Collection;

public final class MathUtil {

    private MathUtil() {
    }

    public static Long calculateTotalDinnerPriceForShit(Collection<Integer> employeeShiftTimes, Long dinnerPrice) {
        long totalDinnerPrice = 0;
        for (var employeeShiftTime : employeeShiftTimes) {
            if (employeeShiftTime <= 3) {
            } else if (employeeShiftTime <= 7) {
                totalDinnerPrice += dinnerPrice;
            } else if (employeeShiftTime <= 11) {
                totalDinnerPrice += dinnerPrice * 2;
            } else {
                totalDinnerPrice += dinnerPrice * 3;
            }
        }
        return totalDinnerPrice;
    }
}
