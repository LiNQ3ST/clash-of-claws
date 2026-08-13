package app;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import battle.BattleController;
import creature.Cat;
import java.net.URL;

/**
 * Desc go here
 *
 * @author Quinton Nisonger, Sahtra Green, Todd Gonzales, Nabiha Fatima
 * @version 0.1.1
 * @since 8/5/2026
 */

public final class SceneFactory {

    private static final double WINDOW_WIDTH = 960;
    private static final double WINDOW_HEIGHT = 600;

    private static Stage primaryStage;

    private SceneFactory() {
        // Utility class.
    }

    public static void initialize(Stage stage) {
        if (stage == null) {
            throw new IllegalArgumentException(
                    "Stage cannot be null."
            );
        }

        primaryStage = stage;
    }

    public static Scene create(SceneType type) {
        URL resource = SceneFactory.class.getResource(
                type.getFxmlPath()
        );

        if (resource == null) {
            throw new IllegalStateException(
                    "FXML resource was not found: "
                            + type.getFxmlPath()
            );
        }

        try {
            FXMLLoader loader = new FXMLLoader(resource);

            return new Scene(
                    loader.load(),
                    WINDOW_WIDTH,
                    WINDOW_HEIGHT
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to load scene: " + type,
                    exception
            );
        }
    }

    public static void show(SceneType type) {
        ensureInitialized();

        primaryStage.setScene(create(type));
        primaryStage.show();
    }

    public static void showMainScene() {
        show(SceneType.MAIN);
    }

    public static void showTraderScene() {
        show(SceneType.TRADER);
    }

    public static void showBattle(
            Cat playerCat,
            Cat opponentCat,
            String battleType
    ) {
        ensureInitialized();

        URL resource = SceneFactory.class.getResource(
                SceneType.BATTLE.getFxmlPath()
        );

        if (resource == null) {
            throw new IllegalStateException(
                    "Battle FXML resource was not found."
            );
        }

        try {
            FXMLLoader loader = new FXMLLoader(resource);

            Parent root = loader.load();

            BattleController controller = loader.getController();

            controller.startBattle(
                    playerCat,
                    opponentCat,
                    battleType
            );

            Scene scene = new Scene(
                    root,
                    WINDOW_WIDTH,
                    WINDOW_HEIGHT
            );

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to load battle scene.",
                    exception
            );
        }
    }

    private static void ensureInitialized() {
        if (primaryStage == null) {
            throw new IllegalStateException(
                    "SceneFactory must be initialized first."
            );
        }
    }
}

