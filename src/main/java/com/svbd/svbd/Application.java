package com.svbd.svbd;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

import static com.svbd.svbd.util.StageUtil.creatMainStage;

public class Application extends javafx.application.Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void start(Stage stage) throws IOException {
        creatMainStage(stage, springContext);
    }

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(SvbdApplication.class)
                .sources(Application.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }

    @Bean("hostServices")
    public HostServices hostServices() {
        return getHostServices();
    }

    public static void main(String[] args) {
        launch();
    }
}