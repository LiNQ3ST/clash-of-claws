package account;

import app.SceneFactory;
import app.SceneType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;

/**
 * Controls the player registration scene.
 *
 * @author Sahtra Green
 * @version 0.1.0
 * @since 8/3/2026
 */

public class RegisterController {

    private final AccountService accountService = AccountService.getInstance();

    @FXML
    private CredentialFieldsController credentialFieldsController;

    @FXML
    private PasswordField confirmationField;

    @FXML
    private TextField visibleConfirmationField;

    @FXML
    private CheckBox showConfirmationCheckBox;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        visibleConfirmationField.textProperty()
                .bindBidirectional(confirmationField.textProperty());
    }

    @FXML
    private void handleShowConfirmation() {
        boolean showPassword = showConfirmationCheckBox.isSelected();

        visibleConfirmationField.setVisible(showPassword);
        visibleConfirmationField.setManaged(showPassword);

        confirmationField.setVisible(!showPassword);
        confirmationField.setManaged(!showPassword);
    }

    @FXML
    private void handleCreateAccount() {
        try {
            accountService.register(credentialFieldsController.getUsername(),
                    credentialFieldsController.getPassword(), confirmationField.getText()
            );

            showAccountCreatedAlert();
            SceneFactory.show(
                    SceneType.STARTER
            );

        } catch (IllegalArgumentException exception) {
            errorLabel.setText(exception.getMessage());

        } catch (SQLException exception) {
            errorLabel.setText("Unable to create account. Please try again.");
        }
    }

    @FXML
    private void handleBack() {
        SceneFactory.show(SceneType.LOGIN);
    }

    private void showAccountCreatedAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Account Created");
        alert.setHeaderText("Your account was successfully created!");
        alert.setContentText("Log in now to start playing.");

        ButtonType loginButton = new ButtonType("Log In");

        alert.getButtonTypes().setAll(loginButton);

        alert.showAndWait();

        SceneFactory.show(SceneType.LOGIN);
    }
}