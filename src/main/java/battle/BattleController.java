package battle;

import creature.CatGenerator;
import javafx.event.ActionEvent;
import account.AccountService;
import account.Player;
import account.PlayerDAO;
import creature.CatDAO;
import app.SceneFactory;
import app.SceneType;
import creature.Cat;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the basic Battle Engine scene.
 *
 * @author Quinton Nisonger
 * @version 0.1.0
 * @since 8/3/2026
 */
public class BattleController {

  private static final Duration LETTER_DELAY = Duration.millis(28);

  @FXML
  private Label battleMessageLabel;

  @FXML
  private Label messageAdvanceIndicator;

  // Player
  @FXML
  private Label playerNameLabel;
  @FXML
  private Label playerHealthLabel;

  // Opponent
  @FXML
  private Label opponentNameLabel;
  @FXML
  private Label opponentHealthLabel;

  @FXML
  private HBox actionMenu;

  @FXML
  private VBox attackMenu;

  @FXML
  private VBox bagMenu;

  @FXML
  private Button abilityButton1;

  @FXML
  private Button abilityButton2;

  @FXML
  private Button abilityButton3;

  @FXML
  private Button abilityButton4;

  @FXML
  private Button itemButton1;

  @FXML
  private Button itemButton2;

  @FXML
  private Button itemButton3;

  @FXML
  private Button itemButton4;

  @FXML
  private Button itemButton5;

  @FXML
  private Button itemButton6;

  @FXML
  private Button runButton;

  @FXML
  private VBox wildVictoryMenu;

  private BattleEngine battleEngine;
  private Player currentPlayer;
  private Battle battleRecord;

  private final BattleDAO battleDAO = new BattleDAO();
  private final PlayerDAO playerDAO = new PlayerDAO();
  private final CatDAO catDAO = new CatDAO();

  private Timeline messageTimeline;
  private String fullMessage = "";
  private int messageCharacterIndex;

  private Runnable messageAdvanceAction;
  private boolean messageTyping;
  private boolean continuingWilds;

  public void startBattle(
      Cat playerCat,
      Cat opponentCat,
      BattleType battleType) {

    startBattle(
        playerCat,
        opponentCat,
        battleType,
        null
    );
  }

  public void startBattle(
      Cat playerCat,
      Cat opponentCat,
      BattleType battleType,
      Integer arenaId) {

    currentPlayer =
        AccountService.getInstance()
            .getCurrentPlayer()
            .orElse(null);

    if (currentPlayer == null
        || currentPlayer.getPlayerId() == null) {

      throw new IllegalStateException(
          "A logged-in player is required to start a battle."
      );
    }

    wildVictoryMenu.setVisible(false);
    wildVictoryMenu.setManaged(false);

    if (battleType == BattleType.ARENA
        && arenaId == null) {

      throw new IllegalArgumentException(
          "Arena battles require an arena ID."
      );
    }

    battleEngine =
        new BattleEngine(
            playerCat,
            opponentCat,
            battleType
        );

    battleRecord =
        new Battle(
            currentPlayer.getPlayerId(),
            battleType.name(),
            arenaId,
            BattleResult.IN_PROGRESS.name()
        );

    battleDAO.insert(battleRecord);

    playerNameLabel.setText(playerCat.getName());
    opponentNameLabel.setText(opponentCat.getName());

    updateHealthLabels();
    loadAbilityButtons();
    configureBattleActions();

    if (battleType == BattleType.WILD) {

      String openingMessage;

      if (continuingWilds) {
        openingMessage =
            "You continue deeper into the Wilds...";
      } else {
        openingMessage =
            "Welcome to the Wilds!";
      }

      continuingWilds = false;

      showMessage(
          openingMessage,
          () -> showMessage(
              "A wild "
                  + opponentCat.getName()
                  + " appeared!",
              this::showActionMenu
          )
      );

      return;
    }

    showMessage(
        playerCat.getName()
            + " is battling "
            + opponentCat.getName()
            + "!",
        this::showActionMenu
    );
  }

  private void persistPlayerCat() {
    boolean updated =
        catDAO.update(
            battleEngine.getPlayerCat(),
            currentPlayer.getPlayerId()
        );

    if (!updated) {
      throw new IllegalStateException(
          "Player cat health could not be saved."
      );
    }
  }

  private void loadAbilityButtons() {
    ArrayList<String> abilities =
        battleEngine.getPlayerCat().getAbilities();

    Button[] buttons = {
        abilityButton1,
        abilityButton2,
        abilityButton3,
        abilityButton4
    };

    for (int i = 0; i < buttons.length; i++) {
      if (i < abilities.size()) {
        String abilityId = abilities.get(i);
        int amount =
            battleEngine.getAbilityAmount(abilityId);

        buttons[i].setUserData(abilityId);

        if (abilityId.equals("HEALING_PURR")) {
          buttons[i].setText(
              formatAbilityName(abilityId)
                  + " - Heal "
                  + amount
          );
        } else {
          buttons[i].setText(
              formatAbilityName(abilityId)
                  + " - "
                  + amount
                  + " Damage"
          );
        }

        buttons[i].setVisible(true);
        buttons[i].setManaged(true);
      } else {
        buttons[i].setUserData(null);
        buttons[i].setVisible(false);
        buttons[i].setManaged(false);
      }
    }
  }

  private void loadItemButtons() {

    Button[] itemButtons = {
        itemButton1,
        itemButton2,
        itemButton3,
        itemButton4,
        itemButton5,
        itemButton6
    };

    for (Button button : itemButtons) {
      button.setUserData(null);
      button.setText("");
      button.setVisible(false);
      button.setManaged(false);
    }

    /*
    // get player's owned items from Todd

    for (int i = 0; i < buttons.length; i++) {

        if (i < inventoryItems.size()) {

            PlayerInventoryItem item =
                inventoryItems.get(i);

            buttons[i].setUserData(item);

            buttons[i].setText(
                item.getItemName()
                    + " x"
                    + item.getQuantity()
            );

            buttons[i].setVisible(true);
            buttons[i].setManaged(true);

        } else {

            buttons[i].setUserData(null);
            buttons[i].setVisible(false);
            buttons[i].setManaged(false);
        }
    }
     */
  }

  /*
  private void useItem(PlayerInventoryItem item) {

    if (item.isPotion()) {
        useHealingItem(item);
        return;
    }

    if (item.isCatchingItem()) {
        useCatchingItem(item);
    }
}

private void useHealingItem(...) {

    // 1. verify quantity > 0

    // 2. get healing value

    battleEngine.heal(
        battleEngine.getPlayerCat(),
        healingAmount
    );

    // 3. consume one from inventory

    // 4. update HP display
    updateHealthLabels();

    // 5. close Bag
    bagMenu.setVisible(false);
    bagMenu.setManaged(false);

    // 6. show message, then opponent attacks
    showMessage(
        battleEngine.getPlayerCat().getName()
            + " recovered "
            + healingAmount
            + " HP!",
        this::performOpponentTurn
    );
}

private void useCatchingItem(...) {

    // 1. verify this is a Wild battle

    if (battleEngine.getBattleType() != BattleType.WILD) {
        return;
    }

    // 2. consume one catching item

    // 3. calculate capture success

    if (captured) {

        // Luke roster handoff here

        battleRecord.setStatus("CAPTURED");
        battleDAO.update(battleRecord);

        persistPlayerCat();

        showMessage(
            battleEngine.getOpponentCat().getName()
                + " was captured!",
            () -> SceneFactory.show(SceneType.MAIN)
        );

        return;
    }

    showMessage(
        "The cat broke free!",
        this::performOpponentTurn
    );
}
   */


  private String formatAbilityName(String abilityId) {
    String[] words = abilityId.toLowerCase().split("_");
    StringBuilder formatted = new StringBuilder();

    for (String word : words) {
      if (!formatted.isEmpty()) {
        formatted.append(' ');
      }

      formatted.append(Character.toUpperCase(word.charAt(0)))
          .append(word.substring(1));
    }

    return formatted.toString();
  }


  private void updateHealthLabels() {
    playerHealthLabel.setText(
        "HP: "
            + battleEngine.getPlayerCat().getCurrentHp()
            + "/"
            + battleEngine.getPlayerCat().getMaxHp()
    );

    opponentHealthLabel.setText(
        "HP: "
            + battleEngine.getOpponentCat().getCurrentHp()
            + "/"
            + battleEngine.getOpponentCat().getMaxHp()
    );
  }

  private void useAbility(Button button) {
    String abilityId = (String) button.getUserData();

    if (abilityId == null) {
      return;
    }

    String abilityDisplayName = formatAbilityName(abilityId);

    battleEngine.playerTurn(abilityId);
    updateHealthLabels();

    attackMenu.setVisible(false);
    attackMenu.setManaged(false);
    actionMenu.setVisible(false);
    actionMenu.setManaged(false);

    if (battleEngine.isBattleWon()) {
      updateHealthLabels();

      showMessage(
          battleEngine.getPlayerCat().getName()
              + " used "
              + abilityDisplayName
              + "!",
          this::handleVictory
      );

      return;
    }

    showMessage(
        battleEngine.getPlayerCat().getName()
            + " used "
            + abilityDisplayName
            + "!",
        this::performOpponentTurn
    );
  }

  private void performOpponentTurn() {
    String opponentAbilityId = battleEngine.opponentTurn();
    String opponentAbilityName = formatAbilityName(opponentAbilityId);

    updateHealthLabels();

    if (battleEngine.isBattleLost()) {
      showMessage(
          battleEngine.getOpponentCat().getName()
              + " used "
              + opponentAbilityName
              + "!",
          this::handleDefeat
      );
      return;
    }

    showMessage(
        battleEngine.getOpponentCat().getName()
            + " used "
            + opponentAbilityName
            + "!",
        this::showActionMenu
    );
  }

  private void showActionMenu() {
    actionMenu.setVisible(true);
    actionMenu.setManaged(true);
    showMessage("Choose an action.", null);
  }

  private void configureBattleActions() {
    boolean wildBattle =
        battleEngine.getBattleType() == BattleType.WILD;

    runButton.setVisible(wildBattle);
    runButton.setManaged(wildBattle);
  }

  private void showMessage(String message, Runnable advanceAction) {
    if (messageTimeline != null) {
      messageTimeline.stop();
    }

    fullMessage = message;
    messageCharacterIndex = 0;
    messageAdvanceAction = advanceAction;
    messageTyping = true;

    battleMessageLabel.setText("");
    messageAdvanceIndicator.setVisible(false);

    messageTimeline = new Timeline(
        new KeyFrame(
            LETTER_DELAY,
            event -> typeNextCharacter()
        )
    );

    messageTimeline.setCycleCount(message.length());
    messageTimeline.play();
  }

  private void typeNextCharacter() {
    if (messageCharacterIndex >= fullMessage.length()) {
      finishTyping();
      return;
    }

    messageCharacterIndex++;
    battleMessageLabel.setText(
        fullMessage.substring(0, messageCharacterIndex)
    );

    if (messageCharacterIndex >= fullMessage.length()) {
      finishTyping();
    }
  }

  private void finishTyping() {
    if (messageTimeline != null) {
      messageTimeline.stop();
    }

    battleMessageLabel.setText(fullMessage);
    messageCharacterIndex = fullMessage.length();
    messageTyping = false;

    if (messageAdvanceAction != null) {
      messageAdvanceIndicator.setVisible(true);
      startAdvanceIndicatorBlink();
    } else {
      messageAdvanceIndicator.setVisible(false);
    }
  }

  private void startAdvanceIndicatorBlink() {
    Timeline blinkTimeline = new Timeline(
        new KeyFrame(
            Duration.ZERO,
            event -> messageAdvanceIndicator.setOpacity(1.0)
        ),
        new KeyFrame(
            Duration.millis(450),
            event -> messageAdvanceIndicator.setOpacity(0.15)
        ),
        new KeyFrame(
            Duration.millis(900),
            event -> messageAdvanceIndicator.setOpacity(1.0)
        )
    );

    blinkTimeline.setCycleCount(Timeline.INDEFINITE);
    blinkTimeline.play();

    messageAdvanceIndicator.getProperties().put("blinkTimeline", blinkTimeline);
  }

  private void stopAdvanceIndicatorBlink() {
    Object value = messageAdvanceIndicator.getProperties().remove("blinkTimeline");

    if (value instanceof Timeline blinkTimeline) {
      blinkTimeline.stop();
    }

    messageAdvanceIndicator.setOpacity(1.0);
    messageAdvanceIndicator.setVisible(false);
  }

  @FXML
  private void handleMessageClick() {
    if (messageTyping) {
      finishTyping();
      return;
    }

    if (messageAdvanceAction == null) {
      return;
    }

    Runnable action = messageAdvanceAction;
    messageAdvanceAction = null;
    stopAdvanceIndicatorBlink();
    action.run();
  }

  @FXML
  private void handleAttack() {
    actionMenu.setVisible(false);
    actionMenu.setManaged(false);

    attackMenu.setVisible(true);
    attackMenu.setManaged(true);

    battleMessageLabel.setText("Choose an ability.");
  }

  @FXML
  private void handleRun() {
    int roll = (int) (Math.random() * 100);
    boolean escaped = battleEngine.attemptRun(roll);

    actionMenu.setVisible(false);
    actionMenu.setManaged(false);

    if (escaped) {
      persistPlayerCat();

      battleRecord.setStatus(
          BattleResult.ESCAPED.name()
      );
      battleDAO.update(battleRecord);

      showMessage("You escaped successfully.",
          () -> SceneFactory.show(SceneType.MAIN)
      );
      return;
    }

    showMessage(
        "You failed to escape.",
        this::performOpponentTurn
    );
  }

  @FXML
  private void handleBag() {
    actionMenu.setVisible(false);
    actionMenu.setManaged(false);

    loadItemButtons();

    bagMenu.setVisible(true);
    bagMenu.setManaged(true);

    showMessage("Choose an item.", null);
  }

  @FXML
  private void handleItem(ActionEvent event) {

    Button button =
        (Button) event.getSource();

    Object itemData =
        button.getUserData();

    if (itemData == null) {
      return;
    }

    // inventory integration goes here.
  }

  @FXML
  private void handleBagBack() {
    bagMenu.setVisible(false);
    bagMenu.setManaged(false);

    actionMenu.setVisible(true);
    actionMenu.setManaged(true);

    showMessage("Choose an action.", null);
  }

  @FXML
  private void handleAttackBack() {
    attackMenu.setVisible(false);
    attackMenu.setManaged(false);

    actionMenu.setVisible(true);
    actionMenu.setManaged(true);

    showMessage("Choose an action.", null);
  }

  @FXML
  private void handleAbilityOne() {
    useAbility(abilityButton1);
  }

  @FXML
  private void handleAbilityTwo() {
    useAbility(abilityButton2);
  }

  @FXML
  private void handleAbilityThree() {
    useAbility(abilityButton3);
  }

  @FXML
  private void handleAbilityFour() {
    useAbility(abilityButton4);
  }

  @FXML
  private void handleReturnToTown() {
    SceneFactory.show(SceneType.MAIN);
  }

  @FXML
  private void handleContinueWilds() {

    wildVictoryMenu.setVisible(false);
    wildVictoryMenu.setManaged(false);

    Cat playerCat =
        battleEngine.getPlayerCat();
    Cat opponentCat =
        new CatGenerator().generateCat();

    continuingWilds = true;

    startBattle(
        playerCat,
        opponentCat,
        BattleType.WILD
    );

  }

  private void handleVictory() {

    if (battleEngine.getBattleType() == BattleType.WILD) {
      handleWildVictory();
      return;
    }

    // Arena victory integration will go here later.
  }

  private void handleWildVictory() {
    persistPlayerCat();

    int currencyReward = 10;

    currentPlayer.setCurrencyBalance(
        currentPlayer.getCurrencyBalance()
            + currencyReward
    );

    try {
      boolean updated =
          playerDAO.update(currentPlayer);

      if (!updated) {
        throw new IllegalStateException(
            "Player rewards could not be saved."
        );
      }

    } catch (SQLException exception) {
      throw new IllegalStateException(
          "Player rewards could not be saved.",
          exception
      );
    }

    battleRecord.setStatus(
        BattleResult.VICTORY.name()
    );
    battleDAO.update(battleRecord);

    showMessage(
        "Victory! You earned 10 coins.",
        this::showWildVictoryMenu
    );
  }

  private void handleDefeat() {

    if (battleEngine.getBattleType() == BattleType.WILD) {
      handleWildDefeat();
      return;
    }

    // Arena defeat integration will go here later.
  }

  private void handleWildDefeat() {
    persistPlayerCat();

    int currentCurrency =
        currentPlayer.getCurrencyBalance();

    int penalty =
        currentCurrency / 10;

    int remainingCurrency =
        currentCurrency - penalty;

    currentPlayer.setCurrencyBalance(
        remainingCurrency
    );

    try {
      boolean updated =
          playerDAO.update(currentPlayer);

      if (!updated) {
        throw new IllegalStateException(
            "Player defeat penalty could not be saved."
        );
      }

    } catch (SQLException exception) {
      throw new IllegalStateException(
          "Player defeat penalty could not be saved.",
          exception
      );
    }

    battleRecord.setStatus(
        BattleResult.DEFEAT.name()
    );
    battleDAO.update(battleRecord);

    showMessage(
        "You were defeated. You lost "
            + penalty
            + " coins.",
        () -> SceneFactory.show(SceneType.MAIN)
    );
  }

  private void showWildVictoryMenu() {

    actionMenu.setVisible(false);
    actionMenu.setManaged(false);

    attackMenu.setVisible(false);
    attackMenu.setManaged(false);

    bagMenu.setVisible(false);
    bagMenu.setManaged(false);

    wildVictoryMenu.setVisible(true);
    wildVictoryMenu.setManaged(true);

    showMessage(
        "Would you like to continue exploring the Wilds?",
        null
    );
  }

}