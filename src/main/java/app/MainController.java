package app;

import account.AccountService;
import account.Player;
import battle.BattleType;
import creature.Cat;
import creature.CatDAO;
import creature.CatGenerator;
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

    Player player =
        AccountService.getInstance()
            .getCurrentPlayer()
            .orElse(null);

    if (player == null || player.getPlayerId() == null) {
      System.out.println("Wild battle blocked: no logged-in player.");
      return;
    }

    if (player.getActiveCatId() == null) {
      System.out.println("Wild battle blocked: no active cat.");
      return;
    }

    CatDAO catDAO = new CatDAO();

    Cat playerCat =
        catDAO.findById(
            player.getActiveCatId(),
            player.getPlayerId()
        );

    if (playerCat == null) {
      System.out.println("Wild battle blocked: active cat not found.");
      return;
    }

    if (playerCat.getCurrentHp() <= 0) {
      System.out.println(
          "Wild battle blocked: "
              + playerCat.getName()
              + " has 0 HP."
      );
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
    SceneFactory.show(SceneType.PLAYER_ARENA);
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