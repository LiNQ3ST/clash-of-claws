package app;

import adminarena.Arena;
import battle.BattleController;
import battle.BattleType;
import creature.Cat;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

/**
 * Creates and displays application scenes.
 *
 * @author Quinton Nisonger, Sahtra Green, Todd Gonzales, Nabiha Fatima
 * @version 0.1.2
 * @since 8/5/2026
 */

public final class SceneFactory {

  private static final double WINDOW_WIDTH = 960;
  private static final double WINDOW_HEIGHT = 600;
  private static final double MIN_WINDOW_WIDTH = 760;
  private static final double MIN_WINDOW_HEIGHT = 620;

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
    primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
    primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);
  }

  public static Scene create(SceneType type) {
    URL resource = getResource(type);

    try {
      FXMLLoader loader = new FXMLLoader(resource);
      Parent root = loader.load();

      return new Scene(
          root,
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
    showScene(create(type));
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
      BattleType battleType) {

    showBattle(
        playerCat,
        opponentCat,
        battleType,
        null
    );
  }

  public static void showBattle(
      Cat playerCat,
      Cat opponentCat,
      BattleType battleType,
      Arena arena) {

    ensureInitialized();

    if (playerCat == null) {
      throw new IllegalArgumentException(
          "Player cat cannot be null."
      );
    }

    if (opponentCat == null) {
      throw new IllegalArgumentException(
          "Opponent cat cannot be null."
      );
    }

    if (battleType == null) {
      throw new IllegalArgumentException(
          "Battle type cannot be null."
      );
    }

    if (battleType == BattleType.ARENA && arena == null) {
      throw new IllegalArgumentException(
          "Arena battles require an Arena."
      );
    }

    URL resource = getResource(SceneType.BATTLE);

    try {
      FXMLLoader loader = new FXMLLoader(resource);
      Parent root = loader.load();

      BattleController controller = loader.getController();

      controller.startBattle(
          playerCat,
          opponentCat,
          battleType,
          arena
      );

      Scene scene = new Scene(
          root,
          WINDOW_WIDTH,
          WINDOW_HEIGHT
      );

      showScene(scene);

    } catch (IOException exception) {
      throw new IllegalStateException(
          "Unable to load battle scene.",
          exception
      );
    }
  }

  private static URL getResource(SceneType type) {
    URL resource = SceneFactory.class.getResource(
        type.getFxmlPath()
    );

    if (resource == null) {
      throw new IllegalStateException(
          "FXML resource was not found: "
              + type.getFxmlPath()
      );
    }

    return resource;
  }

  private static void showScene(Scene scene) {
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private static void ensureInitialized() {
    if (primaryStage == null) {
      throw new IllegalStateException(
          "SceneFactory must be initialized first."
      );
    }
  }
}

