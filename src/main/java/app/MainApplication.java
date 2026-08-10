package app;

import database.DatabaseManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;

public class MainApplication extends Application {

  @Override
  public void start(Stage stage) {
    initializeDatabase();

    stage.setTitle("Clash of Claws");

    SceneFactory.initialize(stage);
    SceneFactory.show(SceneType.LOGIN);
  }

  private void initializeDatabase() {
    try {
      DatabaseManager.getInstance().initializeDatabase();
    } catch (SQLException exception) {
      throw new IllegalStateException(
          "Unable to initialize the Clash of Claws database.",
          exception
      );
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}