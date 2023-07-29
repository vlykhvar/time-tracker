package com.svbd.svbd.util;

import com.svbd.svbd.enums.Pages;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.svbd.svbd.enums.Pages.MAIN_PAGE;

public final class StageUtil {

    private StageUtil() {
    }

    public static void changeStage(Stage currentStage, Pages nextPage) throws IOException {
        var fxmlLoader = new FXMLLoader(StageUtil.class.getResource(nextPage.getPagePath()));
        Parent root = fxmlLoader.load();
        var nextStage = new Stage();
        nextStage.setScene(new Scene(root));
        nextStage.show();
        currentStage.close();
    }

    public static void showStage(Pages nextPage) throws IOException {
        var fxmlLoader = new FXMLLoader(StageUtil.class.getResource(nextPage.getPagePath()));
        Parent root = fxmlLoader.load();
        var nextStage = new Stage();
        nextStage.setScene(new Scene(root));
        nextStage.show();
    }

    public static void creatStage(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StageUtil.class.getResource(MAIN_PAGE.getPagePath()));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 560);
        stage.setTitle("SVBD");
        stage.setScene(scene);
        stage.show();
    }
}
