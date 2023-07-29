package com.svbd.svbd.enums;

public enum ShiftTime {


    ZERO("0:00"),
    ONE("1:00"),
    TWO("2:00"),
    THREE("3:00"),
    FOUR("4:00"),
    FIVE("5:00"),
    SIX("6:00"),
    SEVEN("7:00"),
    EIGHT("8:00"),
    NINE("9:00"),
    TEN("100"),
    ELEVEN("11:00");


    private final String time;

    ShiftTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}
