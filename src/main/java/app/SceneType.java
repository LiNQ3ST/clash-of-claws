package app;

/**
 * desc...
 *
 * @author Quinton Nisonger
 * @version 0.1.0
 * @since 8/3/2026
 */

public enum SceneType {
  MAIN("/app/main-view.fxml"),
  BATTLE("/battle/battle.fxml");

  private final String fxmlPath;

  SceneType(String fxmlPath) {
    this.fxmlPath = fxmlPath;
  }

  public String getFxmlPath() {
    return fxmlPath;
  }
}