module com.svbd.svbd {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires jakarta.activation;
    requires java.naming;
    requires jdk.internal.opt;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.svbd.svbd to javafx.fxml, javafx.base;
    opens com.svbd.svbd.controller to javafx.fxml, javafx.base;
    opens com.svbd.svbd.entity to org.hibernate.orm.core, javafx.base, javafx.fxml;
    opens com.svbd.svbd.dto.employee to javafx.base, javafx.fxml;
    opens com.svbd.svbd.dto.shift.row to javafx.base, javafx.fxml;
    opens com.svbd.svbd.repository.projection to org.hibernate.orm.core;
    opens com.svbd.svbd.dto.salary to javafx.base;
    opens com.svbd.svbd.service to javafx.base, javafx.fxml;
    opens com.svbd.svbd.dto.report to javafx.base, javafx.fxml;

    exports com.svbd.svbd;
    exports com.svbd.svbd.settings;
    exports com.svbd.svbd.controller;
    exports com.svbd.svbd.enums;
    exports com.svbd.svbd.controller.customfield;
    exports com.svbd.svbd.repository.projection;
    exports com.svbd.svbd.dto.report;
    exports com.svbd.svbd.service;
    exports com.svbd.svbd.dto.shift.row;
    exports com.svbd.svbd.dto.employee;

    opens com.svbd.svbd.enums to javafx.fxml;
}