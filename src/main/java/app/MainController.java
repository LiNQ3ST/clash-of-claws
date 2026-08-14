package app;


import account.AccountService;
import account.Player;
import creature.Cat;
import creature.CatDAO;
import creature.CatGenerator;
import account.AccountService;
import account.AccountService;
import account.Player;
import creature.Cat;
import creature.CatDAO;
import creature.CatGenerator;
import javafx.fxml.FXML;
import battle.BattleType;
import battle.BattleType;
import javafx.scene.control.Label;

public class MainController {

  @FXML
  private void handleBackToLogin() {
    SceneFactory.show(SceneType.LOGIN);
  }
  @FXML
  private Label currencyLabel;

  @FXML
  private void initialize() {
    refreshCurrency();
  }

  @FXML
  private void handleBattle() {
    Player player =
        AccountService.getInstance()
            .getCurrentPlayer()
            .orElse(null);

    if (player == null || player.getPlayerId() == null) {
      return;
  @FXML
  private void handleOptions() {
    SceneFactory.show(SceneType.OPTIONS);
  }

  @FXML
  private void handleBattle() {
    Player player =
        AccountService.getInstance()
            .getCurrentPlayer()
            .orElse(null);

    if (player == null || player.getPlayerId() == null) {
      return;
    }

    if (player.getActiveCatId() == null) {
      return;
    }

    CatDAO catDAO = new CatDAO();

    Cat playerCat =
        catDAO.findById(
            player.getActiveCatId(),
            player.getPlayerId()
        );

    if (playerCat == null) {
      return;
    }

    if (playerCat.getCurrentHp() <= 0) {
      return;
    }

    Cat opponentCat =
        new CatGenerator().generateCat();

    SceneFactory.showBattle(
        playerCat,
        opponentCat,
        BattleType.WILD
    );
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