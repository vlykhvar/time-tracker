package com.svbd.svbd;

import com.svbd.svbd.enums.Pages;
import com.svbd.svbd.util.StageManager;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

public class Application extends javafx.application.Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void start(Stage stage) throws IOException {
        // Получаем наш StageManager из контекста Spring
        StageManager stageManager = springContext.getBean(StageManager.class);

        // Используем его для загрузки главной сцены
        var result = stageManager.load(Pages.MAIN_PAGE);
        stage.setScene(result.scene());
        stage.setTitle("Time Tracker");
        stage.show();
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