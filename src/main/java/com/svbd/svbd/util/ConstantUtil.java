package com.svbd.svbd.util;

public final class ConstantUtil {

    private ConstantUtil() {
    }

    public static final String NUMBER_REGEX = "^[+,-]{0,1}(?:[1-9]\\d*|0)(?:\\.(?:0$|[0-9]*[^1]$))*$";
    public static final String TIME_REGEX = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$";
}
