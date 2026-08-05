package account;

import app.SceneFactory;
import app.SceneType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controls the player login scene.
 */
public class LoginController {

    @FXML
    private CredentialFieldsController credentialFieldsController;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = credentialFieldsController.getUsername();
        String password = credentialFieldsController.getPassword();

        SceneFactory.show(SceneType.MAIN);
    }

    @FXML
    private void handleCreateAccount() {
        SceneFactory.show(SceneType.REGISTER);
    }


}