package battle;

import static org.junit.jupiter.api.Assertions.*;

import battle.BattleEngine;
import battle.BattleType;
import creature.Cat;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import app.SceneType;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

/**
 * TestFX tests for the Battle scene.
 *
 * @author Quinton Nisonger
 * @version 0.1.0
 * @since 8/11/2026
 */

class BattleFxTest extends ApplicationTest {

  private BattleController controller;

  @Override
  public void start(Stage stage)
      throws Exception {

    URL resource =
        BattleFxTest.class.getResource(
            SceneType.BATTLE.getFxmlPath()
        );

    assertNotNull(
        resource,
        "Battle FXML should exist."
    );

    FXMLLoader loader =
        new FXMLLoader(resource);

    Parent root =
        loader.load();

    controller =
        loader.getController();

    stage.setScene(
        new Scene(root, 960, 600)
    );

    stage.show();
  }

  @Test
  void battleSceneLoads() {

    assertNotNull(
        lookup("#battleMessageLabel")
            .query()
    );

    assertNotNull(
        lookup("#playerNameLabel")
            .query()
    );

    assertNotNull(
        lookup("#playerHealthLabel")
            .query()
    );

    assertNotNull(
        lookup("#opponentNameLabel")
            .query()
    );

    assertNotNull(
        lookup("#opponentHealthLabel")
            .query()
    );
  }

  @Test
  void clickingAttackOpensAbilityMenu() {

    HBox actionMenu =
        lookup("#actionMenu")
            .queryAs(HBox.class);

    VBox attackMenu =
        lookup("#attackMenu")
            .queryAs(VBox.class);

    assertTrue(actionMenu.isVisible());
    assertFalse(attackMenu.isVisible());

    clickOn("Attack");

    assertFalse(actionMenu.isVisible());
    assertTrue(attackMenu.isVisible());

    assertEquals(
        "Choose an ability.",
        lookup("#battleMessageLabel")
            .queryAs(Label.class)
            .getText()
    );
  }

  @Test
  void attackBackReturnsToActionMenu() {

    HBox actionMenu =
        lookup("#actionMenu")
            .queryAs(HBox.class);

    VBox attackMenu =
        lookup("#attackMenu")
            .queryAs(VBox.class);

    clickOn("Attack");

    assertFalse(actionMenu.isVisible());
    assertTrue(attackMenu.isVisible());

    clickOn("#attackBackButton");

    assertTrue(actionMenu.isVisible());
    assertFalse(attackMenu.isVisible());

    sleep(600);

    assertEquals(
        "Choose an action.",
        lookup("#battleMessageLabel")
            .queryAs(Label.class)
            .getText()
    );
  }

  @Test
  void clickingBagOpensBagMenu() {

    HBox actionMenu =
        lookup("#actionMenu")
            .queryAs(HBox.class);

    VBox bagMenu =
        lookup("#bagMenu")
            .queryAs(VBox.class);

    assertTrue(actionMenu.isVisible());
    assertFalse(bagMenu.isVisible());

    clickOn("Bag");

    assertFalse(actionMenu.isVisible());
    assertTrue(bagMenu.isVisible());

    sleep(600);

    assertEquals(
        "Choose an item.",
        lookup("#battleMessageLabel")
            .queryAs(Label.class)
            .getText()
    );
  }

  @Test
  void bagBackReturnsToActionMenu() {

    HBox actionMenu =
        lookup("#actionMenu")
            .queryAs(HBox.class);

    VBox bagMenu =
        lookup("#bagMenu")
            .queryAs(VBox.class);

    clickOn("Bag");

    assertFalse(actionMenu.isVisible());
    assertTrue(bagMenu.isVisible());

    clickOn("#bagBackButton");

    assertTrue(actionMenu.isVisible());
    assertFalse(bagMenu.isVisible());

    sleep(600);

    assertEquals(
        "Choose an action.",
        lookup("#battleMessageLabel")
            .queryAs(Label.class)
            .getText()
    );
  }

  @Test
  void emptyBagHidesUnusedItemButtons() {

    clickOn("Bag");

    Button itemButton1 =
        lookup("#itemButton1")
            .queryAs(Button.class);

    Button itemButton2 =
        lookup("#itemButton2")
            .queryAs(Button.class);

    Button itemButton3 =
        lookup("#itemButton3")
            .queryAs(Button.class);

    assertFalse(itemButton1.isVisible());
    assertFalse(itemButton1.isManaged());

    assertFalse(itemButton2.isVisible());
    assertFalse(itemButton2.isManaged());

    assertFalse(itemButton3.isVisible());
    assertFalse(itemButton3.isManaged());
  }

  @Test
  void battleActionControlsExist() {

    assertNotNull(
        lookup("Attack")
            .queryAs(Button.class)
    );

    assertNotNull(
        lookup("Bag")
            .queryAs(Button.class)
    );

    assertNotNull(
        lookup("Run")
            .queryAs(Button.class)
    );
  }

  private Cat createPlayerCat(
      int currentHp,
      String... abilities) {

    Cat cat =
        new Cat(
            "Whiskers",
            "Tabby",
            100,
            new ArrayList<>(List.of(abilities)),
            true,
            true
        );

    cat.setCurrentHp(currentHp);

    return cat;
  }

  private Cat createOpponentCat(
      int currentHp) {

    Cat cat =
        new Cat(
            "Bandit",
            "Siamese",
            100,
            new ArrayList<>(List.of("ZOOMIES")),
            false,
            false
        );

    cat.setCurrentHp(currentHp);

    return cat;
  }

  private BattleEngine initializeWildBattle(
      Cat playerCat,
      Cat opponentCat)
      throws Exception {

    BattleEngine engine =
        new BattleEngine(
            playerCat,
            opponentCat,
            BattleType.WILD
        );

    Field engineField =
        BattleController.class
            .getDeclaredField("battleEngine");

    engineField.setAccessible(true);
    engineField.set(controller, engine);

    invokeControllerMethod("updateHealthLabels");
    invokeControllerMethod("loadAbilityButtons");
    invokeControllerMethod("configureBattleActions");
    invokeControllerMethod("showActionMenu");

    return engine;
  }

  private void invokeControllerMethod(
      String methodName)
      throws Exception {

    Method method =
        BattleController.class
            .getDeclaredMethod(methodName);

    method.setAccessible(true);

    interact(() -> {
      try {
        method.invoke(controller);
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      }
    });
  }

  @Test
  void wildBattleDisplaysAbilityButtons()
      throws Exception {

    Cat playerCat =
        createPlayerCat(
            100,
            "SCRATCH",
            "HEALING_PURR"
        );

    Cat opponentCat =
        createOpponentCat(100);

    initializeWildBattle(
        playerCat,
        opponentCat
    );

    clickOn("Attack");

    Button abilityButton1 =
        lookup("#abilityButton1")
            .queryAs(Button.class);

    Button abilityButton2 =
        lookup("#abilityButton2")
            .queryAs(Button.class);

    assertTrue(abilityButton1.isVisible());
    assertTrue(abilityButton1.isManaged());

    assertTrue(abilityButton2.isVisible());
    assertTrue(abilityButton2.isManaged());

    assertEquals(
        "Scratch - 10 Damage",
        abilityButton1.getText()
    );

    assertEquals(
        "Healing Purr - Heal 20",
        abilityButton2.getText()
    );
  }

  @Test
  void scratchAttackUpdatesOpponentHp()
      throws Exception {

    Cat playerCat =
        createPlayerCat(
            100,
            "SCRATCH"
        );

    Cat opponentCat =
        createOpponentCat(100);

    initializeWildBattle(
        playerCat,
        opponentCat
    );

    assertEquals(
        "HP: 100/100",
        lookup("#opponentHealthLabel")
            .queryAs(Label.class)
            .getText()
    );

    clickOn("Attack");
    clickOn("#abilityButton1");

    assertEquals(
        "HP: 90/100",
        lookup("#opponentHealthLabel")
            .queryAs(Label.class)
            .getText()
    );
  }

  @Test
  void healingPurrRestoresPlayerHp()
      throws Exception {

    Cat playerCat =
        createPlayerCat(
            50,
            "SCRATCH",
            "HEALING_PURR"
        );

    Cat opponentCat =
        createOpponentCat(100);

    initializeWildBattle(
        playerCat,
        opponentCat
    );

    assertEquals(
        "HP: 50/100",
        lookup("#playerHealthLabel")
            .queryAs(Label.class)
            .getText()
    );

    clickOn("Attack");
    clickOn("#abilityButton2");

    assertEquals(
        "HP: 70/100",
        lookup("#playerHealthLabel")
            .queryAs(Label.class)
            .getText()
    );
  }

  @Test
  void wildRunIsAvailable()
      throws Exception {

    Cat playerCat =
        createPlayerCat(
            100,
            "SCRATCH"
        );

    Cat opponentCat =
        createOpponentCat(100);

    initializeWildBattle(
        playerCat,
        opponentCat
    );

    Button runButton =
        lookup("#runButton")
            .queryAs(Button.class);

    assertTrue(runButton.isVisible());
    assertTrue(runButton.isManaged());
  }

  @Test
  void wildVictoryShowsVictoryMenu()
      throws Exception {

    Cat playerCat =
        createPlayerCat(
            100,
            "SCRATCH"
        );

    Cat opponentCat =
        createOpponentCat(100);

    initializeWildBattle(
        playerCat,
        opponentCat
    );

    invokeControllerMethod(
        "showWildVictoryMenu"
    );

    VBox victoryMenu =
        lookup("#wildVictoryMenu")
            .queryAs(VBox.class);

    assertTrue(victoryMenu.isVisible());
    assertTrue(victoryMenu.isManaged());

    assertNotNull(
        lookup("Continue Exploring")
            .queryAs(Button.class)
    );

    assertNotNull(
        lookup("Return to Town")
            .queryAs(Button.class)
    );

    assertFalse(
        lookup("#actionMenu")
            .queryAs(HBox.class)
            .isVisible()
    );
  }

  @Test
  void attackHidesMenuAfterAbilityIsUsed()
      throws Exception {

    Cat playerCat =
        createPlayerCat(
            100,
            "SCRATCH"
        );

    Cat opponentCat =
        createOpponentCat(100);

    initializeWildBattle(
        playerCat,
        opponentCat
    );

    VBox attackMenu =
        lookup("#attackMenu")
            .queryAs(VBox.class);

    HBox actionMenu =
        lookup("#actionMenu")
            .queryAs(HBox.class);

    clickOn("Attack");

    assertTrue(attackMenu.isVisible());

    clickOn("#abilityButton1");

    assertFalse(attackMenu.isVisible());
    assertFalse(actionMenu.isVisible());
  }
}

