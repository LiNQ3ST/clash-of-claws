package app;

/**
 * desc...
 *
 * @author Quinton Nisonger, Sahtra Green
 * @version 0.1.1
 * @since 8/5/2026
 */
public enum SceneType {
    LOGIN("/account/Login.fxml"),
    REGISTER("/account/Register.fxml"),
    BATTLE("/battle/battle.fxml"),
    TRADER("/marketplace/trader-view.fxml"),
    ARENA("/adminarena/arena-admin.fxml"),
    CAT_DEX("/creature/cat-dex.fxml"),
    MAIN("/creature/main-view.fxml");

  private final String fxmlPath;

  SceneType(String fxmlPath) {
    this.fxmlPath = fxmlPath;
  }

  public String getFxmlPath() {
    return fxmlPath;
  }
}