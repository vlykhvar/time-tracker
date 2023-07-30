package com.svbd.svbd.util;

import com.svbd.svbd.enums.Pages;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static com.svbd.svbd.enums.Pages.MAIN_PAGE;

public final class StageUtil {

    private StageUtil() {
    }

    public static void changeStage(Stage currentStage, Pages nextPage) throws IOException {
        var fxmlLoader = new FXMLLoader(StageUtil.class.getResource(nextPage.getPagePath()));
        var scene = new Scene(fxmlLoader.load());
        var nextStage = new Stage();
        scene.getStylesheets().add(StageUtil.class.getResource("/application.css").toExternalForm());
        prepareIcon(nextStage);
        nextStage.setScene(scene);
        nextStage.show();
        currentStage.close();
    }

    public static void showStage(Pages nextPage) throws IOException {
        var fxmlLoader = new FXMLLoader(StageUtil.class.getResource(nextPage.getPagePath()));
        var scene = new Scene(fxmlLoader.load());
        var nextStage = new Stage();
        scene.getStylesheets().add(StageUtil.class.getResource("/application.css").toExternalForm());
        prepareIcon(nextStage);
        prepareIcon(nextStage);
        nextStage.setScene(scene);
        nextStage.show();
    }

    public static void creatMainStage(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StageUtil.class.getResource(MAIN_PAGE.getPagePath()));
        prepareIcon(stage);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(StageUtil.class.getResource("/application.css").toExternalForm());
        stage.setTitle("SVBD");
        stage.setScene(scene);
        stage.show();
    }

    private static void prepareIcon(Stage stage) {
        stage.getIcons().add(new Image(Objects.requireNonNull(StageUtil.class.getResourceAsStream("/Image_Editor.png"))));
    }
}
