package battle;

import static org.junit.jupiter.api.Assertions.*;

import app.SceneType;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

/**
 * TestFX tests for the Battle scene.
 *
 * @author Quinton Nisonger
 * @version 0.1.0
 * @since 8/11/2026
 */

class BattleFxTest extends ApplicationTest {

  @Override
  public void start(Stage stage)
      throws Exception {

    URL resource =
        BattleFxTest.class.getResource(
            SceneType.BATTLE.getFxmlPath()
        );

    assertNotNull(
        resource,
        "Battle FXML should exist."
    );

    FXMLLoader loader =
        new FXMLLoader(resource);

    Parent root =
        loader.load();

    stage.setScene(
        new Scene(root, 960, 600)
    );

    stage.show();
  }

  @Test
  void battleSceneLoads() {

    assertNotNull(
        lookup("#battleMessageLabel")
            .query()
    );

    assertNotNull(
        lookup("#playerNameLabel")
            .query()
    );

    assertNotNull(
        lookup("#playerHealthLabel")
            .query()
    );

    assertNotNull(
        lookup("#opponentNameLabel")
            .query()
    );

    assertNotNull(
        lookup("#opponentHealthLabel")
            .query()
    );
  }
}

