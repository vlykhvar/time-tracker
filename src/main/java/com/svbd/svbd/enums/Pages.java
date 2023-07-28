package com.svbd.svbd.enums;

public enum Pages {

    MAIN_PAGE("/fxml/mainPage.fxml"),
    CREATING_EMPLOYEE("/fxml/creatingEmploeePage.fxml"),
    TABLE_EMPLOYEE("/fxml/emploeeTablePage.fxml");

    private final String pagePath;

    Pages(String pagePath) {
        this.pagePath = pagePath;
    }

    public String getPagePath() {
        return pagePath;
    }
}
