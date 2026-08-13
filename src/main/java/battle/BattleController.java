package battle;

import creature.Cat;
import creature.CatSpriteRenderer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;

/**
 * Controls the basic Battle Engine scene.
 *
 * @author Quinton Nisonger
 * @version 0.1.0
 * @since 8/3/2026
 */
public class BattleController {

  @FXML
  private Label battleMessageLabel;

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

  private BattleEngine battleEngine;

  public void startBattle(Cat playerCat, Cat opponentCat) {
    battleEngine = new BattleEngine(playerCat, opponentCat);

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

    battleMessageLabel.setText(
        playerCat.getName()
            + " is battling "
            + opponentCat.getName()
            + "!"
    );
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
        buttons[i].setText(abilities.get(i));
        buttons[i].setVisible(true);
        buttons[i].setManaged(true);
      } else {
        buttons[i].setVisible(false);
        buttons[i].setManaged(false);
      }
    }
  }

  private void updateHealthLabels() {
    playerHealthLabel.setText(
        "HP: " + battleEngine.getPlayerCat().getHp()
    );

    opponentHealthLabel.setText(
        "HP: " + battleEngine.getOpponentCat().getHp()
    );
  }

  private void useAbility(Button button) {
    String abilityName = button.getText();

    battleEngine.playerTurn(abilityName);

    updateHealthLabels();

    if (battleEngine.isBattleWon()) {
      battleMessageLabel.setText(
          battleEngine.getPlayerCat().getName()
              + " used "
              + abilityName
              + ". Victory!"
      );

      attackMenu.setVisible(false);
      attackMenu.setManaged(false);
      return;
    }

    String opponentAbility = battleEngine.opponentTurn();

    updateHealthLabels();

    if (battleEngine.isBattleLost()) {
      battleMessageLabel.setText(
          battleEngine.getOpponentCat().getName()
              + " used "
              + opponentAbility
              + ". You were defeated."
      );

      attackMenu.setVisible(false);
      attackMenu.setManaged(false);
      return;
    }

    battleMessageLabel.setText(
        battleEngine.getPlayerCat().getName()
            + " used "
            + abilityName
            + ". "
            + battleEngine.getOpponentCat().getName()
            + " used "
            + opponentAbility
            + "."
    );

    attackMenu.setVisible(false);
    attackMenu.setManaged(false);

    actionMenu.setVisible(true);
    actionMenu.setManaged(true);
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
    battleMessageLabel.setText("The player ran away!");
  }

  @FXML
  private void handleBag() {
    actionMenu.setVisible(false);
    actionMenu.setManaged(false);

    bagMenu.setVisible(true);
    bagMenu.setManaged(true);

    battleMessageLabel.setText("Choose an item.");
  }

  @FXML
  private void handleBagBack() {
    bagMenu.setVisible(false);
    bagMenu.setManaged(false);

    actionMenu.setVisible(true);
    actionMenu.setManaged(true);

    battleMessageLabel.setText("Choose an action.");
  }

  @FXML
  private void handleAttackBack() {
    attackMenu.setVisible(false);
    attackMenu.setManaged(false);

    actionMenu.setVisible(true);
    actionMenu.setManaged(true);

    battleMessageLabel.setText("Choose an action.");
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

}