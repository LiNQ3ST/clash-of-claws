package battle;

import java.util.List;
import javafx.event.ActionEvent;
import account.AccountService;
import account.Player;
import account.PlayerDAO;
import creature.CatDAO;
import app.SceneFactory;
import app.SceneType;
import creature.Cat;
import java.sql.SQLException;
import creature.CatSpriteRenderer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import marketplace.TraderItem;
import marketplace.TraderItemDAO;
import marketplace.TraderService;

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
  @FXML
  private ImageView playerCatImage;

  // Opponent
  @FXML
  private Label opponentNameLabel;
  @FXML
  private Label opponentHealthLabel;
  @FXML
  private ImageView opponentCatImage;

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


  private BattleEngine battleEngine;
  private Player currentPlayer;
  private Battle battleRecord;

  private final BattleDAO battleDAO = new BattleDAO();
  private final PlayerDAO playerDAO = new PlayerDAO();
  private final CatDAO catDAO = new CatDAO();
  private final TraderItemDAO traderItemDAO = new TraderItemDAO();
  private final TraderService traderService = new TraderService();

  private Timeline messageTimeline;
  private String fullMessage = "";
  private int messageCharacterIndex;
  private Runnable messageAdvanceAction;
  private boolean messageTyping;

  public BattleController() {
  }

  public BattleController(Label battleMessageLabel, Label messageAdvanceIndicator,
      Label playerNameLabel, Label playerHealthLabel, ImageView playerCatImage,
      Label opponentNameLabel, Label opponentHealthLabel, ImageView opponentCatImage,
      HBox actionMenu,
      VBox attackMenu, VBox bagMenu, Button abilityButton1, Button abilityButton2,
      Button abilityButton3, Button abilityButton4, Button itemButton1, Button itemButton2,
      Button itemButton3, Button itemButton4, Button itemButton5, Button itemButton6,
      Button runButton, BattleEngine battleEngine, Player currentPlayer, Battle battleRecord,
      Timeline messageTimeline, String fullMessage, int messageCharacterIndex,
      Runnable messageAdvanceAction, boolean messageTyping) {
    this.battleMessageLabel = battleMessageLabel;
    this.messageAdvanceIndicator = messageAdvanceIndicator;
    this.playerNameLabel = playerNameLabel;
    this.playerHealthLabel = playerHealthLabel;
    this.playerCatImage = playerCatImage;
    this.opponentNameLabel = opponentNameLabel;
    this.opponentHealthLabel = opponentHealthLabel;
    this.opponentCatImage = opponentCatImage;
    this.actionMenu = actionMenu;
    this.attackMenu = attackMenu;
    this.bagMenu = bagMenu;
    this.abilityButton1 = abilityButton1;
    this.abilityButton2 = abilityButton2;
    this.abilityButton3 = abilityButton3;
    this.abilityButton4 = abilityButton4;
    this.itemButton1 = itemButton1;
    this.itemButton2 = itemButton2;
    this.itemButton3 = itemButton3;
    this.itemButton4 = itemButton4;
    this.itemButton5 = itemButton5;
    this.itemButton6 = itemButton6;
    this.runButton = runButton;
    this.battleEngine = battleEngine;
    this.currentPlayer = currentPlayer;
    this.battleRecord = battleRecord;
    this.messageTimeline = messageTimeline;
    this.fullMessage = fullMessage;
    this.messageCharacterIndex = messageCharacterIndex;
    this.messageAdvanceAction = messageAdvanceAction;
    this.messageTyping = messageTyping;
  }

  public void startBattle(
      Cat playerCat,
      Cat opponentCat,
      BattleType battleType) {

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
            null,
            BattleResult.IN_PROGRESS.name()
        );

    battleDAO.insert(battleRecord);

    playerNameLabel.setText(playerCat.getName());
    opponentNameLabel.setText(opponentCat.getName());

    CatSpriteRenderer.setSprite(
        playerCatImage,
        playerCat,
        CatSpriteRenderer.BATTLE_PLAYER
    );

    CatSpriteRenderer.setSprite(
        opponentCatImage,
        opponentCat,
        CatSpriteRenderer.BATTLE_OPPONENT
    );

    updateHealthLabels();
    loadAbilityButtons();
    configureBattleActions();

    showMessage(
        playerCat.getName()
            + " is battling "
            + opponentCat.getName()
            + "!",
        null
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

        buttons[i].setUserData(abilityId);
        buttons[i].setText(formatAbilityName(abilityId));
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
      button.setVisible(false);
      button.setManaged(false);
    }

    try {
      List<TraderItem> allItems =
          traderItemDAO.findAll();

      int buttonIndex = 0;

      for (TraderItem item : allItems) {

        int quantity =
            traderService.getInventoryQuantity(
                currentPlayer.getPlayerId(),
                item.getItemId()
            );

        if (quantity <= 0) {
          continue;
        }

        if (buttonIndex >= itemButtons.length) {
          break;
        }

        Button button =
            itemButtons[buttonIndex];

        button.setText(
            item.getItemName()
                + " x"
                + quantity
        );

        button.setUserData(item);
        button.setVisible(true);
        button.setManaged(true);

        buttonIndex++;
      }

    } catch (SQLException exception) {
      throw new IllegalStateException(
          "Could not load player inventory.",
          exception
      );
    }
  }

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

    Button button = (Button) event.getSource();

    Object itemData = button.getUserData();

    if (!(itemData instanceof TraderItem item)) {
      return;
    }

    String itemType = item.getItemType();

    if ("HEALING".equalsIgnoreCase(itemType)) {
      useHealingItem(item);
      return;
    }

    if ("CATCHING".equalsIgnoreCase(itemType)) {
      useCatchingItem(item);
    }
  }

  private void useHealingItem(TraderItem item) {

    int healingAmount;

    try {
      healingAmount = getHealingAmount(item);
    } catch (IllegalArgumentException exception) {
      showMessage(
          "That item cannot be used for healing.",
          null
      );
      return;
    }

    if (battleEngine.getPlayerCat().getCurrentHp()
        >= battleEngine.getPlayerCat().getMaxHp()) {

      showMessage(
          battleEngine.getPlayerCat().getName()
              + " is already at full health.",
          null
      );

      return;
    }

    try {
      traderService.consumeInventoryItem(
          currentPlayer.getPlayerId(),
          item.getItemId()
      );
    } catch (SQLException exception) {
      throw new IllegalStateException(
          "Could not consume healing item.",
          exception
      );
    }

    int hpBefore =
        battleEngine.getPlayerCat().getCurrentHp();

    battleEngine.heal(
        battleEngine.getPlayerCat(),
        healingAmount
    );

    int hpAfter =
        battleEngine.getPlayerCat().getCurrentHp();

    int actualHealing =
        hpAfter - hpBefore;

    persistPlayerCat();
    updateHealthLabels();

    bagMenu.setVisible(false);
    bagMenu.setManaged(false);

    actionMenu.setVisible(false);
    actionMenu.setManaged(false);

    showMessage(
        battleEngine.getPlayerCat().getName()
            + " recovered "
            + actualHealing
            + " HP!",
        this::performOpponentTurn
    );
  }

  private void useCatchingItem(TraderItem item) {

    if (battleEngine.getBattleType() != BattleType.WILD) {
      showMessage(
          "Catching items can only be used in Wild battles.",
          null
      );
      return;
    }

    int catchChance;

    try {
      catchChance = getCatchChance(item);
    } catch (IllegalArgumentException exception) {
      showMessage(
          "That item cannot be used for catching.",
          null
      );
      return;
    }

    try {
      traderService.consumeInventoryItem(
          currentPlayer.getPlayerId(),
          item.getItemId()
      );
    } catch (SQLException exception) {
      throw new IllegalStateException(
          "Could not consume catching item.",
          exception
      );
    }

    bagMenu.setVisible(false);
    bagMenu.setManaged(false);

    actionMenu.setVisible(false);
    actionMenu.setManaged(false);

    int roll = (int) (Math.random() * 100);

    boolean captured =
        isCaptureSuccessful(
            catchChance,
            roll
        );

    if (captured) {

      Cat capturedCat =
          battleEngine.getOpponentCat();

      capturedCat.setPlayerCat(true);
      capturedCat.setInParty(false);

      catDAO.insert(
          capturedCat,
          currentPlayer.getPlayerId()
      );

      battleRecord.setStatus("CAPTURED");
      battleDAO.update(battleRecord);

      persistPlayerCat();

      showMessage(
          capturedCat.getName()
              + " was captured and sent to storage!",
          () -> SceneFactory.show(SceneType.MAIN)
      );

      return;
    }

    showMessage(
        battleEngine.getOpponentCat().getName()
            + " broke free!",
        this::performOpponentTurn
    );
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
        "Victory! You earned rewards.",
        () -> SceneFactory.show(SceneType.MAIN)
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

  private int getHealingAmount(TraderItem item) {

    if ("Small Potion".equalsIgnoreCase(item.getItemName())) {
      return 10;
    }

    if ("Large Potion".equalsIgnoreCase(item.getItemName())) {
      return 20;
    }

    throw new IllegalArgumentException(
        "Unknown healing item: " + item.getItemName()
    );
  }

  private int getCatchChance(TraderItem item) {

    if ("Basic Catching Item".equalsIgnoreCase(item.getItemName())) {
      return 50;
    }

    if ("Strong Catching Item".equalsIgnoreCase(item.getItemName())) {
      return 75;
    }

    throw new IllegalArgumentException(
        "Unknown catching item: " + item.getItemName()
    );
  }

  private boolean isCaptureSuccessful(
      int catchChance,
      int roll) {

    return roll < catchChance;
  }


}