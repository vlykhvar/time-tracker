package com.svbd.svbd;

import com.svbd.svbd.enums.Pages;
import com.svbd.svbd.util.StageManager;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

public class Application extends javafx.application.Application {

    private ConfigurableApplicationContext springContext;
    private Stage splashStage;

    @Override
    public void start(Stage primaryStage) {
        showSplashScreen();

        Task<Scene> loadTask = new Task<>() {
            @Override
            protected Scene call() throws Exception {
                // 1. Initialize Spring Context
                springContext = new SpringApplicationBuilder(SvbdApplication.class)
                        .sources(Application.class)
                        .run(getParameters().getRaw().toArray(new String[0]));

                // 2. Get StageManager from context
                StageManager stageManager = springContext.getBean(StageManager.class);

                // 3. Load Main FXML
                // StageManager.load returns a result containing the Scene.
                // We return the Scene directly to avoid "root already set" errors.
                var result = stageManager.load(Pages.MAIN_PAGE);
                return result.scene();
            }
        };

        loadTask.setOnSucceeded(event -> {
            Scene mainScene = loadTask.getValue();
            
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Time Tracker");
            
            splashStage.close();
            primaryStage.show();
        });

        loadTask.setOnFailed(event -> {
            splashStage.close();
            Throwable ex = loadTask.getException();
            ex.printStackTrace();
            showErrorAlert(ex);
        });

        new Thread(loadTask).start();
    }

    private void showSplashScreen() {
        splashStage = new Stage();
        splashStage.initStyle(StageStyle.TRANSPARENT);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        Label label = new Label("Завантаження...");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        VBox root = new VBox(15, progressIndicator, label);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 20; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #444;");

        Scene scene = new Scene(root, 300, 200);
        scene.setFill(Color.TRANSPARENT);

        splashStage.setScene(scene);
        splashStage.show();
    }

    private void showErrorAlert(Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка запуску");
        alert.setHeaderText("Не вдалося запустити додаток");
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
        Platform.exit();
    }

    @Override
    public void init() {
        // Spring initialization moved to background task in start()
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
    }

    @Bean("hostServices")
    public HostServices hostServices() {
        return getHostServices();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
