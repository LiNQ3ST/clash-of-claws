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
    OPTIONS("/account/Options.fxml"),
    BATTLE("/battle/battle.fxml"),
    TRADER("/marketplace/trader-view.fxml"),
    ARENA("/adminarena/arena-admin.fxml"),
    STARTER("/creature/starter.fxml"),
    CAT_DEX("/creature/cat-dex.fxml"),
    PARTY("/creature/party.fxml"),
    STORAGE("/creature/storage.fxml"),
    MAIN("/app/main-view.fxml");

  private final String fxmlPath;

  SceneType(String fxmlPath) {
    this.fxmlPath = fxmlPath;
  }

  public String getFxmlPath() {
    return fxmlPath;
  }
}