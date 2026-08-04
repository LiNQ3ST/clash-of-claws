package account;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controls the reusable username and password fields.
 */
public class CredentialFieldsController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public void clear() {
        usernameField.clear();
        passwordField.clear();
    }
}