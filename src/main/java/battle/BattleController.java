package battle;

import account.AccountService;
import account.Player;
import account.PlayerDAO;
import adminarena.Arena;
import app.SceneFactory;
import app.SceneType;
import creature.Cat;
import creature.CatDAO;
import creature.CatGenerator;
import creature.CatSpriteRenderer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import marketplace.TraderItem;
import marketplace.TraderItemDAO;
import marketplace.TraderService;
import database.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Controls the shared Wild and Arena battle scene.
 *
 * @author Quinton Nisonger
 * @version 0.1.0
 * @since 8/3/2026
 */
public class BattleController {

  private static final Duration LETTER_DELAY = Duration.millis(28);
  private static final int WILD_VICTORY_REWARD = 10;
  private int opponentDebugClickCount;

  @FXML
  private Label battleMessageLabel;

  @FXML
  private Label messageAdvanceIndicator;

  @FXML
  private Label playerNameLabel;

  @FXML
  private Label playerHealthLabel;

  @FXML
  private ImageView playerCatImage;

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
  private VBox wildVictoryMenu;

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
  private Button itemActionButton;

  @FXML
  private VBox switchMenu;

  @FXML
  private Button switchCatButton1;

  @FXML
  private Button switchCatButton2;

  @FXML
  private Button switchCatButton3;

  @FXML
  private Button switchCatButton4;

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
  private boolean continuingWilds;
  private Arena currentArena;

  public BattleController() {
  }

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
      Arena arena) {

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

    if (battleType == BattleType.ARENA && arena == null) {
      throw new IllegalArgumentException(
          "Arena battles require an Arena."
      );
    }

    currentArena = arena;

    hideAllMenus();

    battleEngine =
        new BattleEngine(
            playerCat,
            opponentCat,
            battleType
        );

    Integer arenaId =
        arena == null
            ? null
            : arena.getArenaId();

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

    if (battleType == BattleType.WILD) {
      showWildOpeningMessage(opponentCat);
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

  private void showWildOpeningMessage(Cat opponentCat) {
    String openingMessage = continuingWilds
        ? "You continue deeper into the Wilds..."
        : "Welcome to the Wilds!";

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
      Button button = buttons[i];

      if (i >= abilities.size()) {
        button.setUserData(null);
        button.setText("");
        button.setVisible(false);
        button.setManaged(false);
        continue;
      }

      String abilityId = abilities.get(i);
      int amount = battleEngine.getAbilityAmount(abilityId);

      button.setUserData(abilityId);

      if ("HEALING_PURR".equals(abilityId)) {
        button.setText(
            formatAbilityName(abilityId)
                + " - Heal "
                + amount
        );
      } else {
        button.setText(
            formatAbilityName(abilityId)
                + " - "
                + amount
                + " Damage"
        );
      }

      button.setVisible(true);
      button.setManaged(true);
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

    try {
      List<TraderItem> allItems = traderItemDAO.findAll();
      int buttonIndex = 0;

      for (TraderItem item : allItems) {
        if (!isItemAllowedInBattle(item)) {
          continue;
        }

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

        Button button = itemButtons[buttonIndex];
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

    if (wildBattle) {
      itemActionButton.setText("Bag");
    } else {
      itemActionButton.setText("Heal");
    }
  }

  private boolean isItemAllowedInBattle(
      TraderItem item) {

    if (battleEngine.getBattleType() == BattleType.ARENA) {
      return "HEALING".equalsIgnoreCase(
          item.getItemType()
      );
    }

    return true;
  }

  private void hideAllMenus() {
    actionMenu.setVisible(false);
    actionMenu.setManaged(false);

    attackMenu.setVisible(false);
    attackMenu.setManaged(false);

    bagMenu.setVisible(false);
    bagMenu.setManaged(false);

    wildVictoryMenu.setVisible(false);
    wildVictoryMenu.setManaged(false);

    switchMenu.setVisible(false);
    switchMenu.setManaged(false);
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

    messageAdvanceIndicator.getProperties().put(
        "blinkTimeline",
        blinkTimeline
    );
  }

  private void stopAdvanceIndicatorBlink() {
    Object value =
        messageAdvanceIndicator
            .getProperties()
            .remove("blinkTimeline");

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
    }

    if (messageAdvanceAction == null) {
      return;
    }

    Runnable action = messageAdvanceAction;
    messageAdvanceAction = null;

    stopAdvanceIndicatorBlink();

    action.run();
  }

  private void giveDebugItems() {

    if (currentPlayer == null
        || currentPlayer.getPlayerId() == null) {
      return;
    }

    String updateSql = """
      UPDATE player_inventory
      SET quantity = quantity + 2
      WHERE player_id = ?
        AND item_id = ?
      """;

    String insertSql = """
      INSERT INTO player_inventory (
          player_id,
          item_id,
          quantity
      )
      VALUES (?, ?, 2)
      """;

    try (
        Connection connection =
            DatabaseManager.getInstance()
                .getConnection()
    ) {

      List<TraderItem> items =
          traderItemDAO.findAll();

      for (TraderItem item : items) {

        int rowsUpdated;

        try (
            PreparedStatement updateStatement =
                connection.prepareStatement(
                    updateSql
                )
        ) {

          updateStatement.setInt(
              1,
              currentPlayer.getPlayerId()
          );

          updateStatement.setInt(
              2,
              item.getItemId()
          );

          rowsUpdated =
              updateStatement.executeUpdate();
        }

        if (rowsUpdated == 0) {

          try (
              PreparedStatement insertStatement =
                  connection.prepareStatement(
                      insertSql
                  )
          ) {

            insertStatement.setInt(
                1,
                currentPlayer.getPlayerId()
            );

            insertStatement.setInt(
                2,
                item.getItemId()
            );

            insertStatement.executeUpdate();
          }
        }
      }

      showMessage(
          "Debug items added: +2 of every item.",
          null
      );

    } catch (SQLException exception) {

      throw new IllegalStateException(
          "Could not add debug items.",
          exception
      );
    }
  }

  @FXML
  private void handleAttack() {
    actionMenu.setVisible(false);
    actionMenu.setManaged(false);

    attackMenu.setVisible(true);
    attackMenu.setManaged(true);

    showMessage("Choose an ability.", null);
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

      showMessage(
          "You escaped successfully.",
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

    if ("HEALING".equalsIgnoreCase(item.getItemType())) {
      useHealingItem(item);
      return;
    }

    if ("CATCHING".equalsIgnoreCase(item.getItemType())) {
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

    int hpBefore = battleEngine.getPlayerCat().getCurrentHp();

    battleEngine.heal(
        battleEngine.getPlayerCat(),
        healingAmount
    );

    int actualHealing =
        battleEngine.getPlayerCat().getCurrentHp()
            - hpBefore;

    persistPlayerCat();
    updateHealthLabels();

    bagMenu.setVisible(false);
    bagMenu.setManaged(false);

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
      catchChance =
          getAdjustedCatchChance(item);
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

    int roll = (int) (Math.random() * 100);
    boolean captured =
        isCaptureSuccessful(
            catchChance,
            roll
        );

    if (captured) {
      Cat capturedCat = battleEngine.getOpponentCat();
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

  @FXML
  private void handleReturnToTown() {
    SceneFactory.show(SceneType.MAIN);
  }

  @FXML
  private void handleContinueWilds() {
    wildVictoryMenu.setVisible(false);
    wildVictoryMenu.setManaged(false);

    Cat playerCat = battleEngine.getPlayerCat();
    Cat opponentCat = new CatGenerator().generateCat();

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

    handleArenaVictory();
  }

  private void handleWildVictory() {
    persistPlayerCat();

    currentPlayer.setCurrencyBalance(
        currentPlayer.getCurrencyBalance()
            + WILD_VICTORY_REWARD
    );

    updatePlayerOrThrow(
        "Player rewards could not be saved."
    );

    battleRecord.setStatus(
        BattleResult.VICTORY.name()
    );
    battleDAO.update(battleRecord);

    showMessage(
        "Victory! You earned "
            + WILD_VICTORY_REWARD
            + " coins.",
        this::showWildVictoryMenu
    );
  }

  private void handleArenaVictory() {

    persistPlayerCat();

    if (currentArena == null) {
      throw new IllegalStateException(
          "Arena battle is missing Arena information."
      );
    }

    int currencyReward =
        currentArena.getRewardAmount();

    currentPlayer.setCurrencyBalance(
        currentPlayer.getCurrencyBalance()
            + currencyReward
    );

    updatePlayerOrThrow(
        "Arena reward could not be saved."
    );

    battleRecord.setStatus(
        BattleResult.VICTORY.name()
    );

    battleDAO.update(battleRecord);

    showMessage(
        "Arena victory! You earned "
            + currencyReward
            + " coins.",
        () -> SceneFactory.show(SceneType.MAIN)
    );
  }

  private void handleDefeat() {
    if (battleEngine.getBattleType() == BattleType.WILD) {
      handleWildDefeat();
      return;
    }

    handleArenaDefeat();
  }

  private void handleWildDefeat() {
    persistPlayerCat();

    int currentCurrency = currentPlayer.getCurrencyBalance();
    int penalty = currentCurrency / 10;

    currentPlayer.setCurrencyBalance(
        currentCurrency - penalty
    );

    updatePlayerOrThrow(
        "Player defeat penalty could not be saved."
    );

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

  private void handleArenaDefeat() {
    persistPlayerCat();

    battleRecord.setStatus(
        BattleResult.DEFEAT.name()
    );
    battleDAO.update(battleRecord);

    showMessage(
        "You were defeated in the arena.",
        () -> SceneFactory.show(SceneType.MAIN)
    );
  }

  @FXML
  private void handleSwitch() {

    actionMenu.setVisible(false);
    actionMenu.setManaged(false);

    loadSwitchButtons();

    switchMenu.setVisible(true);
    switchMenu.setManaged(true);

    showMessage(
        "Choose a cat.",
        null
    );
  }

  @FXML
  private void handleSwitchCat(
      ActionEvent event) {

    Button button =
        (Button) event.getSource();

    Object data =
        button.getUserData();

    if (!(data instanceof Cat newCat)) {
      return;
    }

    Cat oldCat =
        battleEngine.getPlayerCat();

    persistPlayerCat();

    battleEngine.switchPlayerCat(
        newCat
    );

    playerNameLabel.setText(
        newCat.getName()
    );

    CatSpriteRenderer.setSprite(
        playerCatImage,
        newCat,
        CatSpriteRenderer.BATTLE_PLAYER
    );

    updateHealthLabels();
    loadAbilityButtons();

    switchMenu.setVisible(false);
    switchMenu.setManaged(false);

    showMessage(
        oldCat.getName()
            + ", come back! Go "
            + newCat.getName()
            + "!",
        this::performOpponentTurn
    );
  }

  @FXML
  private void handleSwitchBack() {

    switchMenu.setVisible(false);
    switchMenu.setManaged(false);

    actionMenu.setVisible(true);
    actionMenu.setManaged(true);

    showMessage(
        "Choose an action.",
        null
    );
  }

  private void loadSwitchButtons() {

    Button[] buttons = {
        switchCatButton1,
        switchCatButton2,
        switchCatButton3,
        switchCatButton4
    };

    for (Button button : buttons) {
      button.setText("");
      button.setUserData(null);
      button.setVisible(false);
      button.setManaged(false);
    }

    ArrayList<Cat> ownedCats =
        catDAO.findAll(
            currentPlayer.getPlayerId()
        );

    Cat currentCat =
        battleEngine.getPlayerCat();

    int buttonIndex = 0;

    for (Cat cat : ownedCats) {

      if (!cat.isPlayerCat()) {
        continue;
      }

      if (cat.getId() == currentCat.getId()) {
        continue;
      }

      if (cat.getCurrentHp() <= 0) {
        continue;
      }

      if (buttonIndex >= buttons.length) {
        break;
      }

      Button button =
          buttons[buttonIndex];

      button.setText(
          cat.getName()
              + " - HP: "
              + cat.getCurrentHp()
              + "/"
              + cat.getMaxHp()
      );

      button.setUserData(cat);
      button.setVisible(true);
      button.setManaged(true);

      buttonIndex++;
    }

    if (buttonIndex == 0) {
      showMessage(
          "No other healthy cats are available.",
          null
      );
    }
  }

  @FXML
  private void handleOpponentDebugClick() {

    opponentDebugClickCount++;

    if (opponentDebugClickCount < 10) {
      return;
    }

    opponentDebugClickCount = 0;

    giveDebugItems();
  }

  private boolean isDebugItemRewardReady() {
    return opponentDebugClickCount >= 10;
  }
  private void updatePlayerOrThrow(String errorMessage) {
    try {
      boolean updated = playerDAO.update(currentPlayer);

      if (!updated) {
        throw new IllegalStateException(errorMessage);
      }
    } catch (SQLException exception) {
      throw new IllegalStateException(
          errorMessage,
          exception
      );
    }
  }

  private void showWildVictoryMenu() {
    hideAllMenus();

    wildVictoryMenu.setVisible(true);
    wildVictoryMenu.setManaged(true);

    showMessage(
        "Would you like to continue exploring the Wilds?",
        null
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

    if ("Toy Mouse".equalsIgnoreCase(item.getItemName())) {
      return 50;
    }

    if ("Tuna Can".equalsIgnoreCase(item.getItemName())) {
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

  private int getAdjustedCatchChance(
      TraderItem item) {

    int baseChance =
        getCatchChance(item);

    Cat opponent =
        battleEngine.getOpponentCat();

    double healthPercent =
        (double) opponent.getCurrentHp()
            / opponent.getMaxHp();

    int bonus = 0;

    if (healthPercent <= 0.25) {
      bonus = 20;
    } else if (healthPercent <= 0.50) {
      bonus = 10;
    }

    return Math.min(
        95,
        baseChance + bonus
    );
  }
}