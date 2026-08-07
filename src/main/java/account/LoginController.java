package account;

import app.SceneFactory;
import app.SceneType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.SQLException;

/**
 * Controls the player login scene.
 *
 * @author Sahtra Green
 * @version 0.1.0
 * @since 8/3/2026
 */

public class LoginController {

    private final AccountService accountService = AccountService.getInstance();

    @FXML
    private CredentialFieldsController credentialFieldsController;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        try {
            Player player = accountService.authenticate(
                    credentialFieldsController.getUsername(),
                    credentialFieldsController.getPassword()
            );

            routePlayer(player);

        } catch (IllegalArgumentException exception) {
            errorLabel.setText(exception.getMessage());

        } catch (SQLException exception) {
            errorLabel.setText(
                    "Unable to log in. Please try again."
            );
        }
    }

    @FXML
    private void handleCreateAccount() {
        SceneFactory.show(SceneType.REGISTER);
    }


    private void routePlayer(Player player) {
        SceneFactory.show(SceneType.MAIN);
    }
}