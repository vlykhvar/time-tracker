package com.svbd.svbd.util;

import com.svbd.svbd.enums.Exceptions;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;


/**
 * Утилитарный класс для удобного отображения стандартных диалоговых окон JavaFX.
 */
public final class AlertUtil {

    private AlertUtil() {
    }

    /**
     * Показывает диалоговое окно на основе предопределенного типа исключения.
     * @param exception Перечисление с типом, заголовком и текстом для окна.
     */
    public static void showAlert(Exceptions exception) {
        showAlert(exception.getAlertType(), exception.getTitle(), exception.getText());
    }

    /**
     * Показывает простое информационное или диалоговое окно с ошибкой.
     * @param type Тип окна (например, INFORMATION, ERROR).
     * @param title Заголовок окна.
     * @param message Основное сообщение.
     */
    public static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Убираем заголовок для более чистого вида
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Показывает диалоговое окно подтверждения с кнопками "Да" и "Нет".
     * Кнопки будут автоматически локализованы (например, "Yes"/"No" или "Да"/"Нет").
     * @param title Заголовок окна.
     * @param message Вопрос для подтверждения.
     * @return {@code true} если пользователь нажал "Да", иначе {@code false}.
     */
    public static boolean showConfirmationDialog(String title, String message) {
        // Используем стандартные ButtonType для автоматической локализации
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);

        Optional<ButtonType> result = alert.showAndWait();

        // Более читаемый и безопасный способ проверить результат
        return result.isPresent() && result.get() == ButtonType.YES;
    }
}
