package com.svbd.svbd;

import com.svbd.svbd.enums.Pages;
import com.svbd.svbd.util.StageManager;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Objects;

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

        // Load the image
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image_Editor.png")));
        ImageView imageView = new ImageView(image);
        
        // Create a progress indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("-fx-progress-color: #00e5ff;"); // Neon cyan color
        progressIndicator.setPrefSize(30, 30); // Slightly smaller

        // Create a label
        Label label = new Label("Завантаження...");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 2, 1.0, 0, 0);");

        // Container for loader and label
        VBox loaderBox = new VBox(5, progressIndicator, label);
        loaderBox.setAlignment(Pos.CENTER);
        loaderBox.setPadding(new Insets(10)); // Padding around the loader
        
        // Main Root: VBox to stack Image ABOVE Loader
        VBox root = new VBox(imageView, loaderBox);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: transparent;"); // Keep transparent background

        // If the image has transparency, the loader will appear "floating" below it.
        // If the image is a rectangle, the loader will be attached to the bottom edge.

        Scene scene = new Scene(root);
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
