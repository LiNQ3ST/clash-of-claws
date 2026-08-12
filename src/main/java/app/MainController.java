package app;


import account.AccountService;
import javafx.fxml.FXML;

public class MainController {

    @FXML
    private void handleBackToLogin() {
        SceneFactory.show(SceneType.LOGIN);
    }

    @FXML
    private void handleBattle() {
        AccountService.getInstance().logout();
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
}