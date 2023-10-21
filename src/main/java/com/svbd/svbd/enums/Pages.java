package com.svbd.svbd.enums;

public enum  Pages {

    MAIN_PAGE("/fxml/mainPage.fxml"),
    EMPLOYEE_PROFILE("/fxml/employeeProfile.fxml"),
    TABLE_EMPLOYEE("/fxml/emploeeTablePage.fxml"),
    REPORTS_PAGE("/fxml/reportsPage.fxml"),
    SETTINGS_PAGE("/fxml/settings.fxml"),
    ABOUT("/fxml/about.fxml");

    private final String pagePath;

    Pages(String pagePath) {
        this.pagePath = pagePath;
    }

    public String getPagePath() {
        return pagePath;
    }
}
