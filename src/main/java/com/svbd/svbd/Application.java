package com.svbd.svbd;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.svbd.svbd.enums.Pages.MAIN_PAGE;
import static com.svbd.svbd.util.StageUtil.creatStage;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        creatStage(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}