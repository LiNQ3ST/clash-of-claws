package account;

import app.SceneFactory;
import app.SceneType;
import database.DatabaseManager;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;

/**
 * Tests JavaFX scene transitions and account UI behavior.
 *
 * @author Sahtra Green
 * @version 0.1.0
 * @since 8/10/2026
 */

@Tag("testfx")
@ExtendWith(ApplicationExtension.class)
public class AccountSceneTest {

    private static final String TEST_PASSWORD = "testpass123";
    private static final String WRONG_PASSWORD = "wrongpass";
    private static final String INVALID_LOGIN_MESSAGE =
            "Invalid username or password. Please try again.";
    private static final String MISSING_CREDENTIALS_MESSAGE =
            "Please enter credentials to continue.";

    private Stage stage;
    private Scene loginScene;
    private String testUsername;

    @Start
    public void start(Stage stage) {
        AccountService.getInstance().logout();

        this.stage = stage;

        SceneFactory.initialize(stage);
        SceneFactory.show(SceneType.LOGIN);

        loginScene = stage.getScene();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        AccountService.getInstance().logout();

        if (testUsername != null) {
            PlayerDAO playerDAO = new PlayerDAO();
            Player testPlayer = playerDAO.findByUsername(testUsername).orElse(null);

            if (testPlayer != null) {
                playerDAO.deleteAccount(testPlayer.getPlayerId());
            }
        }
    }

    @Test
    public void accountFlow(FxRobot robot) throws SQLException, TimeoutException {
        DatabaseManager.getInstance().initializeDatabase();

        testUsername = "test_" + UUID.randomUUID().toString().substring(0, 8);
        PlayerDAO playerDAO = new PlayerDAO();

        assertTrue(playerDAO.findByUsername(testUsername).isEmpty());
        assertTrue(robot.lookup("CLASH OF CLAWS").tryQuery().isPresent());


        robot.clickOn("#usernameField").write(testUsername);
        robot.clickOn("#passwordField").write(TEST_PASSWORD);

        robot.clickOn("LOG IN");

        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        waitForLabel(errorLabel, INVALID_LOGIN_MESSAGE);

        assertEquals(INVALID_LOGIN_MESSAGE, errorLabel.getText());
        assertSame(loginScene, stage.getScene());
        assertTrue(AccountService.getInstance().getCurrentPlayer().isEmpty());


        // Create a new account through the registration scene.
        robot.clickOn("CREATE ACCOUNT");

        Scene registerScene = stage.getScene();
        assertNotSame(loginScene, registerScene);
        assertTrue(robot.lookup("JOIN THE BATTLE").tryQuery().isPresent());


        robot.clickOn("#usernameField").write(testUsername);
        robot.clickOn("#passwordField").write(TEST_PASSWORD);
        robot.clickOn("#confirmationField").write(TEST_PASSWORD);

        robot.clickOn("CREATE ACCOUNT");

        // Verify registration succeeded and the account-created alert is displayed.
        waitForNode(robot, "Log In");

        Player createdPlayer = playerDAO.findByUsername(testUsername).orElseThrow();
        assertNotNull(createdPlayer.getPlayerId());
        assertTrue(robot.lookup("Log In").tryQuery().isPresent());

        robot.clickOn("Log In");

        // Return to Login and submit the correct username with a blank password.
        waitForNode(robot, "CLASH OF CLAWS");

        Scene loginAfterRegistration = stage.getScene();
        assertNotSame(registerScene, loginAfterRegistration);


        robot.clickOn("#usernameField").write(testUsername);

        robot.clickOn("LOG IN");

        Label loginError = robot.lookup("#errorLabel").queryAs(Label.class);
        waitForLabel(loginError, MISSING_CREDENTIALS_MESSAGE);

        assertEquals(MISSING_CREDENTIALS_MESSAGE, loginError.getText());
        assertSame(loginAfterRegistration, stage.getScene());
        assertTrue(AccountService.getInstance().getCurrentPlayer().isEmpty());


        // Submit the correct username with the wrong password.
        robot.clickOn("#passwordField").write(WRONG_PASSWORD);

        robot.clickOn("LOG IN");

        waitForLabel(loginError, INVALID_LOGIN_MESSAGE);

        assertEquals(INVALID_LOGIN_MESSAGE, loginError.getText());
        assertSame(loginAfterRegistration, stage.getScene());
        assertTrue(AccountService.getInstance().getCurrentPlayer().isEmpty());


        // Replace the wrong password with the correct password and log in.
        PasswordField passwordField =
                robot.lookup("#passwordField").queryAs(PasswordField.class);
        robot.interact(passwordField::clear);
        robot.clickOn("#passwordField").write(TEST_PASSWORD);

        robot.clickOn("LOG IN");

        // A new player has no active cat, so successful login routes to Starter.
        waitForNode(robot, "Choose Your Starter Cat");

        assertNotSame(loginAfterRegistration, stage.getScene());
        assertTrue(robot.lookup("Choose Your Starter Cat").tryQuery().isPresent());

        Player currentPlayer = AccountService.getInstance()
                .getCurrentPlayer()
                .orElseThrow();

        assertEquals(testUsername, currentPlayer.getUsername());
        assertEquals(createdPlayer.getPlayerId(), currentPlayer.getPlayerId());
        assertNull(currentPlayer.getActiveCatId());
    }

    private static void waitForLabel(Label label, String expectedText)
            throws TimeoutException {
        WaitForAsyncUtils.waitFor(
                10,
                TimeUnit.SECONDS,
                () -> expectedText.equals(label.getText())
        );
    }

    private static void waitForNode(FxRobot robot, String text)
            throws TimeoutException {
        WaitForAsyncUtils.waitFor(
                10,
                TimeUnit.SECONDS,
                () -> robot.lookup(text).tryQuery().isPresent()
        );
    }

}