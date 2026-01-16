package com.svbd.svbd.util;

import com.svbd.svbd.enums.Pages;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * Service for managing Stages (windows) in a Spring-integrated JavaFX application.
 * Ensures that controllers for new windows are created by Spring,
 * receiving all necessary dependencies.
 */
@Component
public class StageManager {

    /**
     * A helper record to return the result of an FXML load operation.
     * @param scene The Scene, ready to be displayed.
     * @param controller The controller instance created by Spring.
     */
    public record FxmlLoadResult<T>(Scene scene, T controller) {}

    private final ConfigurableApplicationContext springContext;

    public StageManager(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }

    public <T> FxmlLoadResult<T> load(Pages page) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(page.getPagePath()));
        fxmlLoader.setControllerFactory(springContext::getBean);

        Parent root = fxmlLoader.load();
        T controller = fxmlLoader.getController();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/application.css")).toExternalForm());

        return new FxmlLoadResult<>(scene, controller);
    }

    public Stage createModalStage(Scene ownerScene, String title) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image_Editor.png"))));
        stage.initOwner(ownerScene.getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        return stage;
    }
}
