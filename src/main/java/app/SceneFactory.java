package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class SceneFactory {

    private static final double WINDOW_WIDTH = 960;
    private static final double WINDOW_HEIGHT = 600;
    private static Stage primaryStage;

    private SceneFactory() {
        // Utility class.
    }

    public static void initialize(Stage stage) {
        if (stage == null) {
            throw new IllegalArgumentException("Stage cannot be null.");
        }
        primaryStage = stage;
    }

    public static void showMainScene() {
        showScene("/app/main-view.fxml", "Clash of Claws");
    }

    public static void showTraderScene() {
        showScene("/marketplace/trader-view.fxml", "Clash of Claws - Trader");
    }

    private static void showScene(String fxmlPath, String title) {
        if (primaryStage == null) {
            throw new IllegalStateException("SceneFactory must be initialized first.");
        }

        URL resource = SceneFactory.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IllegalStateException("FXML resource was not found: " + fxmlPath);
        }

        try {
            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load scene: " + fxmlPath, exception);
        }
    }
}

