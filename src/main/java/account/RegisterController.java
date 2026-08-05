package account;

import app.SceneFactory;
import app.SceneType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

/**
 * Controls the player registration scene.
 */
public class RegisterController {

    @FXML
    private CredentialFieldsController credentialFieldsController;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleCreateAccount() {
        String username = credentialFieldsController.getUsername();
        String password = credentialFieldsController.getPassword();
        String confirmation = confirmPasswordField.getText();

        if (username.isBlank()
                || password.isBlank()
                || confirmation.isBlank()) {
            messageLabel.setText("Complete all required fields.");
            return;
        }

        if (!password.equals(confirmation)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        messageLabel.setText(
                "Registration will be implemented in Issue #7."
        );
    }

    @FXML
    private void handleBack() {
        SceneFactory.show(SceneType.LOGIN);
    }
}