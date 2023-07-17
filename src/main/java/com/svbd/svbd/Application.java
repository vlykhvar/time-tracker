package com.svbd.svbd;

import com.svbd.svbd.settings.H2Embedded;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    H2Embedded db = new H2Embedded();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("mainPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("SVBD");
        stage.setScene(scene);
        stage.show();
        db.connection();
    }

    public static void main(String[] args) {
        launch();
    }
}