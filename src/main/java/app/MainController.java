package app;

import account.AccountService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    private Label currencyLabel;

    @FXML
    private void initialize() {
        refreshCurrency();
    }

    @FXML
    private void handleOptions() {
        SceneFactory.show(SceneType.OPTIONS);
    }

    @FXML
    private void handleBattle() {
        SceneFactory.show(SceneType.BATTLE);
    }

    @FXML
    private void handleArena() {
        SceneFactory.show(SceneType.ARENA);
    }

    @FXML
    private void handleTrader() {
        SceneFactory.show(SceneType.TRADER);
    }

    @FXML
    private void handleCatDex() {
        SceneFactory.show(SceneType.CAT_DEX);
    }

    @FXML
    private void handleParty() {
        SceneFactory.show(SceneType.PARTY);
    }

    @FXML
    private void handleStorage() {
        SceneFactory.show(SceneType.STORAGE);
    }

    private void refreshCurrency() {
        AccountService.getInstance()
                .getCurrentPlayer()
                .ifPresent(player ->
                        currencyLabel.setText(
                                "Coins: " + player.getCurrencyBalance()
                        )
                );
    }
}