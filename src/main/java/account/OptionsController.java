package account;

import app.SceneFactory;
import app.SceneType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.sql.SQLException;

public class OptionsController {

    private final AccountService accountService =
            AccountService.getInstance();

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmationField;

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        if (accountService.getCurrentPlayer().isEmpty()) {
            SceneFactory.show(SceneType.LOGIN);
        }
    }

    @FXML
    private void handleUpdatePassword() {
        messageLabel.setText("");

        try {
            accountService.updatePassword(
                    currentPasswordField.getText(),
                    newPasswordField.getText(),
                    confirmationField.getText()
            );

            currentPasswordField.clear();
            newPasswordField.clear();
            confirmationField.clear();

            messageLabel.setStyle("-fx-text-fill: #356b32;");
            messageLabel.setText("Password updated successfully!");

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());

        } catch (SQLException exception) {
            showError("Unable to update password.");
        }
    }

    @FXML
    private void handleBack() {
        SceneFactory.show(SceneType.MAIN);
    }

    @FXML
    private void handleLogout() {
        accountService.logout();
        SceneFactory.show(SceneType.LOGIN);
    }

    @FXML
    private void handleExitGame() {
        Platform.exit();
    }

    @FXML
    private void handleDeleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Delete Account");
        alert.setHeaderText("Delete your Clash of Claws account?");
        alert.setContentText(
                "This will permanently delete your cats, items, and player data."
        );

        ButtonType deleteButton = new ButtonType(
                "Delete Account",
                ButtonBar.ButtonData.OK_DONE
        );

        alert.getButtonTypes().setAll(
                ButtonType.CANCEL,
                deleteButton
        );

        alert.showAndWait().ifPresent(result -> {
            if (result != deleteButton) {
                return;
            }

            try {
                accountService.deleteCurrentAccount();
                SceneFactory.show(SceneType.LOGIN);

            } catch (SQLException exception) {
                showError("Unable to delete account.");
            }
        });
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: #8b2f22;");
        messageLabel.setText(message);
    }
}