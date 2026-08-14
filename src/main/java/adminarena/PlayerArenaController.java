package adminarena;

import account.AccountService;
import account.Player;
import app.SceneFactory;
import app.SceneType;
import creature.Cat;
import creature.CatDAO;
import creature.CatGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

/**
 * Controls the player-facing Arena scene.
 * Loads the player's active cat and generates a new
 * opponent cat when an arena battle begins.
 *
 * @author Nabiha Fatima
 * @version 0.1.0
 * @since 8/11/2026
 */
public class PlayerArenaController {

    @FXML
    private Label arenaNameLabel;

    @FXML
    private Label townNameLabel;

    @FXML
    private Label difficultyLabel;

    @FXML
    private Label rewardLabel;

    @FXML
    private Label activeCatNameLabel;

    @FXML
    private Label activeCatHealthLabel;

    @FXML
    private Button challengeButton;

    private final AccountService accountService =
            AccountService.getInstance();

    private final CatDAO catDAO =
            new CatDAO();

    private final CatGenerator catGenerator =
            new CatGenerator();

    private Arena arena;
    private Player currentPlayer;
    private Cat activeCat;

    @FXML
    private void initialize() {
        loadCurrentPlayer();
        challengeButton.setDisable(true);
    }

    public void setArena(Arena arena) {
        if (arena == null) {
            showError("Arena could not be loaded.");
            challengeButton.setDisable(true);
            return;
        }

        this.arena = arena;

        arenaNameLabel.setText(
                arena.getArenaName()
        );

        townNameLabel.setText(
                arena.getTownName()
        );

        difficultyLabel.setText(
                arena.getDifficulty()
        );

        rewardLabel.setText(
                arena.getRewardAmount() + " coins"
        );

        updateChallengeButton();
    }

    private void loadCurrentPlayer() {
        currentPlayer = accountService
                .getCurrentPlayer()
                .orElse(null);

        if (currentPlayer == null) {
            activeCatNameLabel.setText(
                    "No player logged in"
            );

            activeCatHealthLabel.setText(
                    "HP: --"
            );

            challengeButton.setDisable(true);
            return;
        }

        Integer activeCatId =
                currentPlayer.getActiveCatId();

        if (activeCatId == null) {
            activeCatNameLabel.setText(
                    "No active cat selected"
            );

            activeCatHealthLabel.setText(
                    "HP: --"
            );

            challengeButton.setDisable(true);
            return;
        }

        activeCat = catDAO.findById(
                activeCatId,
                currentPlayer.getPlayerId()
        );

        if (activeCat == null) {
            activeCatNameLabel.setText(
                    "Active cat could not be loaded"
            );

            activeCatHealthLabel.setText(
                    "HP: --"
            );

            challengeButton.setDisable(true);
            return;
        }

        activeCatNameLabel.setText(
                activeCat.getName()
        );

        activeCatHealthLabel.setText(
                "HP: " + activeCat.getCurrentHp()
        );

        updateChallengeButton();
    }

    @FXML
    private void handleEasyArena() {
        setArena(
                new Arena(
                        1,
                        "Whiskerton Claw Pit",
                        "Whiskerton",
                        "EASY",
                        200,
                        true
                )
        );
    }

    @FXML
    private void handleMediumArena() {
        setArena(
                new Arena(
                        2,
                        "Shadow Alley Arena",
                        "Moonpaw",
                        "MEDIUM",
                        400,
                        true
                )
        );
    }

    @FXML
    private void handleHardArena() {
        setArena(
                new Arena(
                        3,
                        "Royal Paw Coliseum",
                        "Clawchester",
                        "HARD",
                        700,
                        true
                )
        );
    }

    @FXML
    private void handleBackToTown() {
        SceneFactory.show(
                SceneType.MAIN
        );
    }

    @FXML
    private void handleChallengeArena() {
        if (arena == null) {
            showError(
                    "No arena is currently selected."
            );
            return;
        }

        if (!arena.isActive()) {
            showError(
                    "This arena is currently inactive."
            );
            return;
        }

        if (currentPlayer == null) {
            showError(
                    "You must be logged in to challenge an arena."
            );
            return;
        }

        if (activeCat == null) {
            showError(
                    "Select an active cat before entering the arena."
            );
            return;
        }

        if (activeCat.getCurrentHp() <= 0) {
            showError(
                    "Your active cat must be healed before battling."
            );
            return;
        }

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Confirm Arena Challenge"
        );

        confirmation.setHeaderText(
                "Challenge "
                        + arena.getArenaName()
                        + "?"
        );

        confirmation.setContentText(
                "You will battle the arena opponent for "
                        + arena.getRewardAmount()
                        + " coins."
        );

        confirmation.showAndWait()
                .ifPresent(response -> {
                    if (response == ButtonType.OK) {

                        Cat opponentCat =
                                catGenerator.generateCat();

                        SceneFactory.showBattle(
                                activeCat,
                                opponentCat,
                                "ARENA"
                        );
                    }
                });
    }

    private void updateChallengeButton() {
        boolean canChallenge =
                arena != null
                        && arena.isActive()
                        && currentPlayer != null
                        && activeCat != null
                        && activeCat.getCurrentHp() > 0;

        challengeButton.setDisable(
                !canChallenge
        );
    }

    private void showError(String message) {
        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle("Arena");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}