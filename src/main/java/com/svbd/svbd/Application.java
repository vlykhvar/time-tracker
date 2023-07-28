package com.svbd.svbd;

import com.svbd.svbd.settings.DatabaseModule;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.svbd.svbd.Pages.MAIN_PAGE;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(MAIN_PAGE.getPagePath()));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("SVBD");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}