package account;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controls the reusable username and password fields used by account scenes.
 *
 * @author Sahtra Green
 * @version 0.1.0
 * @since 8/3/2026
 */

public class CredentialFieldsController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private void initialize() {
        visiblePasswordField.textProperty()
                .bindBidirectional(
                        passwordField.textProperty()
                );
    }

    @FXML
    private void handleShowPassword() {
        boolean showPassword =
                showPasswordCheckBox.isSelected();

        visiblePasswordField.setVisible(showPassword);
        visiblePasswordField.setManaged(showPassword);

        passwordField.setVisible(!showPassword);
        passwordField.setManaged(!showPassword);
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public void clear() {
        usernameField.clear();
        passwordField.clear();
    }
}