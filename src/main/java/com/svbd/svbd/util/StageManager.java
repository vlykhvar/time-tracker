package com.svbd.svbd.util;

import com.svbd.svbd.enums.Pages;
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
 * Сервис для управления окнами (Stage) в JavaFX приложении, интегрированном со Spring.
 * Гарантирует, что контроллеры для новых окон создаются через Spring,
 * получая все необходимые зависимости.
 */
@Component
public class StageManager {

    private final ConfigurableApplicationContext springContext;

    // Spring автоматически внедрит сюда свой контекст
    public StageManager(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }

    public record FxmlLoadResult<T>(Scene scene, T controller) {}

    /**
     * Загружает FXML файл и возвращает Scene и контроллер.
     * Этот метод полезен, когда нужно получить доступ к контроллеру перед отображением сцены,
     * например, для передачи данных.
     *
     * @param page Страница для загрузки из перечисления Pages.
     * @param <T> Тип контроллера FXML файла.
     * @return Объект FxmlLoadResult, содержащий загруженную Scene и экземпляр контроллера.
     * @throws IOException Если FXML файл не найден или произошла ошибка при загрузке.
     */
    public <T> FxmlLoadResult<T> load(Pages page) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(page.getPagePath()));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/application.css")).toExternalForm());
        T controller = fxmlLoader.getController();
        return new FxmlLoadResult<>(scene, controller);
    }

    /**
     * Создает и возвращает новый Stage, настроенный как модальное окно.
     *
     * @param ownerScene Сцена родительского окна, которое будет заблокировано.
     * @param title Заголовок нового окна.
     * @return Новый экземпляр Stage.
     */
    public Stage createModalStage(Scene ownerScene, String title) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image_Editor.png"))));
        stage.initOwner(ownerScene.getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        return stage;
    }

    public void showModalStage(Pages page, Scene ownerScene, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(page.getPagePath()));

        // Ключевой момент: говорим загрузчику получать контроллеры из Spring
        fxmlLoader.setControllerFactory(springContext::getBean);

        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/application.css")).toExternalForm());

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image_Editor.png"))));
        stage.initOwner(ownerScene.getWindow());
        stage.initModality(Modality.WINDOW_MODAL); // Блокирует родительское окно
        stage.setScene(scene);
        stage.showAndWait(); // Показывает окно и ждет его закрытия
    }
}