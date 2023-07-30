package com.svbd.svbd.enums;

public enum  Pages {

    MAIN_PAGE("/fxml/mainPage.fxml"),
    EMPLOYEE_PROFILE("/fxml/employeeProfile.fxml"),
    TABLE_EMPLOYEE("/fxml/emploeeTablePage.fxml");

    private final String pagePath;

    Pages(String pagePath) {
        this.pagePath = pagePath;
    }

    public String getPagePath() {
        return pagePath;
    }
}