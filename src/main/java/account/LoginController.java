package account;

import app.SceneFactory;
import app.SceneType;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
        errorLabel.setText("");

        String username = credentialFieldsController.getUsername();
        String password = credentialFieldsController.getPassword();

        Task<Player> loginTask = new Task<>() {
            @Override
            protected Player call() throws Exception {
                return accountService.authenticate(username, password);
            }
        };

        loginTask.setOnSucceeded(event -> {
            Player player = loginTask.getValue();
            routePlayer(player);
        });

        loginTask.setOnFailed(event -> {
            Throwable exception = loginTask.getException();

            if (exception instanceof IllegalArgumentException) {
                errorLabel.setText(exception.getMessage());
            } else {
                errorLabel.setText("Unable to log in. Please try again.");
            }
        });

        Thread loginThread = new Thread(loginTask);
        loginThread.setDaemon(true);
        loginThread.start();
    }

    @FXML
    private void handleCreateAccount() {
        SceneFactory.show(SceneType.REGISTER);
    }


    private void routePlayer(Player player) {
        if (player.getActiveCatId() == null) {
            SceneFactory.show(SceneType.CAT_DEX);
        } else {
            SceneFactory.show(SceneType.MAIN);
        }
    }
}