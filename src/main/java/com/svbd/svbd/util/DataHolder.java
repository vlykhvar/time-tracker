package com.svbd.svbd.util;

public final class DataHolder {

    private DataHolder() {
    }

    private Object data;
    private final static DataHolder INSTANCE = new DataHolder();

    public static DataHolder getInstance() {
        return INSTANCE;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return this.data;
    }
}
