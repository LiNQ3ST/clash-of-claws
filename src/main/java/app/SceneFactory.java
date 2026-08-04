package app;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

/**
 * Desc go here
 *
 * @author Quinton Nisonger
 * @version 0.1.0
 * @since 8/3/2026
 */
public final class SceneFactory {

  private SceneFactory() {
    // Utility class.
  }

  public static Scene create(SceneType type) {
    try {
      return new Scene(
          FXMLLoader.load(SceneFactory.class.getResource(type.getFxmlPath())),
          960,
          540);
    } catch (IOException exception) {
      throw new IllegalStateException(
          "Unable to load scene: " + type,
          exception);
    }
  }
}