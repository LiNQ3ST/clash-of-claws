

/**
 * ui test arena
 *
 * @author Nabiha Fatima
 * @version 0.1.0
 * @since 8/13/26
 */
package adminarena;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 * Tests the player-facing Arena JavaFX scene.
 *
 * @author Nabiha Fatima
 * @version 0.1.0
 * @since 8/13/2026
 */
@Tag("testfx")
class PlayerArenaControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/adminarena/player-arena.fxml"
                )
        );

        Parent root = loader.load();

        stage.setScene(
                new Scene(root, 960, 600)
        );

        stage.show();
    }

    @Test
    void selectingEasyArenaDisplaysArenaInformation() {

        clickOn("Whiskerton Claw Pit");

        verifyThat(
                "#arenaNameLabel",
                hasText("Whiskerton Claw Pit")
        );

        verifyThat(
                "#townNameLabel",
                hasText("Whiskerton")
        );

        verifyThat(
                "#difficultyLabel",
                hasText("EASY")
        );

        verifyThat(
                "#rewardLabel",
                hasText("200 coins")
        );
    }
}