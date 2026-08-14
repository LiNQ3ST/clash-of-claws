package marketplace;

import javafx.scene.control.Alert;

/**
 * Reusable JavaFX notifications used by the Trader slice.
 */
public final class TraderNotification {

    private TraderNotification() {
        // Utility class.
    }

    public static void success(String title, String header, String content) {
        show(Alert.AlertType.INFORMATION, title, header, content);
    }

    public static void warning(String title, String header, String content) {
        show(Alert.AlertType.WARNING, title, header, content);
    }

    public static void error(String title, String header, String content) {
        show(Alert.AlertType.ERROR, title, header, content);
    }

    public static void show(
            Alert.AlertType alertType,
            String title,
            String header,
            String content
    ) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

