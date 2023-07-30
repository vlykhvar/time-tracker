package com.svbd.svbd;

import javafx.stage.Stage;

import java.io.IOException;

import static com.svbd.svbd.util.StageUtil.creatMainStage;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        creatMainStage(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}