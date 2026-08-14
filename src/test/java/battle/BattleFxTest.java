package battle;

import static org.junit.jupiter.api.Assertions.*;

import creature.Cat;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Timeline;
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
import marketplace.TraderItem;

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
  public void start(Stage stage) throws Exception {
    URL resource =
        BattleFxTest.class.getResource(
            SceneType.BATTLE.getFxmlPath()
        );

    assertNotNull(resource);

    FXMLLoader loader =
        new FXMLLoader(resource);

    Parent root = loader.load();
    controller = loader.getController();

    stage.setScene(
        new Scene(root, 960, 600)
    );

    stage.show();
  }

  @Test
  void battleSceneLoads() {
    assertNotNull(lookup("#battleMessageLabel").query());
    assertNotNull(lookup("#playerNameLabel").query());
    assertNotNull(lookup("#playerHealthLabel").query());
    assertNotNull(lookup("#opponentNameLabel").query());
    assertNotNull(lookup("#opponentHealthLabel").query());
  }

  @Test
  void clickingAttackOpensAbilityMenu()
      throws Exception {

    initializeWildBattle(
        createPlayerCat(100, "SCRATCH"),
        createOpponentCat(100)
    );

    HBox actionMenu =
        lookup("#actionMenu")
            .queryAs(HBox.class);

    VBox attackMenu =
        lookup("#attackMenu")
            .queryAs(VBox.class);

    clickOn("#attackButton");

    assertFalse(actionMenu.isVisible());
    assertTrue(attackMenu.isVisible());

    sleep(600);

    assertEquals(
        "Choose an ability.",
        lookup("#battleMessageLabel")
            .queryAs(Label.class)
            .getText()
    );
  }

  @Test
  void attackBackReturnsToActionMenu()
      throws Exception {

    initializeWildBattle(
        createPlayerCat(100, "SCRATCH"),
        createOpponentCat(100)
    );

    clickOn("#attackButton");
    clickOn("#attackBackButton");

    finishMessageAnimation();

    assertTrue(
        lookup("#actionMenu")
            .queryAs(HBox.class)
            .isVisible()
    );

    assertFalse(
        lookup("#attackMenu")
            .queryAs(VBox.class)
            .isVisible()
    );

    assertEquals(
        "Choose an action.",
        lookup("#battleMessageLabel")
            .queryAs(Label.class)
            .getText()
    );
  }

  @Test
  void wildBattleDisplaysAbilityButtons()
      throws Exception {

    initializeWildBattle(
        createPlayerCat(
            100,
            "SCRATCH",
            "HEALING_PURR"
        ),
        createOpponentCat(100)
    );

    clickOn("#attackButton");

    Button abilityButton1 =
        lookup("#abilityButton1")
            .queryAs(Button.class);

    Button abilityButton2 =
        lookup("#abilityButton2")
            .queryAs(Button.class);

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

    initializeWildBattle(
        createPlayerCat(100, "SCRATCH"),
        createOpponentCat(100)
    );

    clickOn("#attackButton");
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

    initializeWildBattle(
        createPlayerCat(
            50,
            "SCRATCH",
            "HEALING_PURR"
        ),
        createOpponentCat(100)
    );

    clickOn("#attackButton");
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

    initializeWildBattle(
        createPlayerCat(100, "SCRATCH"),
        createOpponentCat(100)
    );

    Button runButton =
        lookup("#runButton")
            .queryAs(Button.class);

    assertTrue(runButton.isVisible());
    assertTrue(runButton.isManaged());
  }

  @Test
  void arenaBattleDisplaysHealAction()
      throws Exception {

    initializeArenaBattle(
        createPlayerCat(60, "SCRATCH"),
        createOpponentCat(100)
    );

    Button itemActionButton =
        lookup("#itemActionButton")
            .queryAs(Button.class);

    Button runButton =
        lookup("#runButton")
            .queryAs(Button.class);

    assertEquals(
        "Heal",
        itemActionButton.getText()
    );

    assertTrue(itemActionButton.isVisible());
    assertFalse(runButton.isVisible());
  }

  @Test
  void wildBattleAllowsCatchingItemInBattleUi()
      throws Exception {

    initializeWildBattle(
        createPlayerCat(100, "SCRATCH"),
        createOpponentCat(100)
    );

    TraderItem catchingItem =
        new TraderItem(
            1,
            "Basic Catching Item",
            "CATCHING",
            "Used to catch wild cats.",
            25,
            5
        );

    assertTrue(
        isItemAllowedInBattle(
            catchingItem
        )
    );

    Button itemButton1 =
        lookup("#itemButton1")
            .queryAs(Button.class);

    interact(() -> {
      itemButton1.setText(
          "Basic Catching Item x1"
      );

      itemButton1.setUserData(
          catchingItem
      );

      itemButton1.setVisible(true);
      itemButton1.setManaged(true);
    });

    assertTrue(itemButton1.isVisible());

    assertEquals(
        "Basic Catching Item x1",
        itemButton1.getText()
    );

    assertSame(
        catchingItem,
        itemButton1.getUserData()
    );
  }

  @Test
  void arenaRejectsCatchingItem()
      throws Exception {

    initializeArenaBattle(
        createPlayerCat(100, "SCRATCH"),
        createOpponentCat(100)
    );

    TraderItem catchingItem =
        new TraderItem(
            1,
            "Basic Catching Item",
            "CATCHING",
            "Used to catch wild cats.",
            25,
            5
        );

    assertFalse(
        isItemAllowedInBattle(
            catchingItem
        )
    );
  }

  @Test
  void battleSceneIncludesSwitchControls()
      throws Exception {

    initializeWildBattle(
        createPlayerCat(100, "SCRATCH"),
        createOpponentCat(100)
    );

    Button switchButton =
        lookup("#switchButton")
            .queryAs(Button.class);

    VBox switchMenu =
        lookup("#switchMenu")
            .queryAs(VBox.class);

    assertEquals(
        "Switch",
        switchButton.getText()
    );

    assertTrue(switchButton.isVisible());
    assertFalse(switchMenu.isVisible());

    assertNotNull(
        lookup("#switchCatButton1")
            .queryAs(Button.class)
    );

    assertNotNull(
        lookup("#switchBackButton")
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
            new ArrayList<>(
                List.of(abilities)
            ),
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
            new ArrayList<>(
                List.of("ZOOMIES")
            ),
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

    return initializeBattle(
        playerCat,
        opponentCat,
        BattleType.WILD
    );
  }

  private BattleEngine initializeArenaBattle(
      Cat playerCat,
      Cat opponentCat)
      throws Exception {

    return initializeBattle(
        playerCat,
        opponentCat,
        BattleType.ARENA
    );
  }

  private BattleEngine initializeBattle(
      Cat playerCat,
      Cat opponentCat,
      BattleType battleType)
      throws Exception {

    BattleEngine engine =
        new BattleEngine(
            playerCat,
            opponentCat,
            battleType
        );

    setControllerField(
        "battleEngine",
        engine
    );

    interact(() -> {
      try {
        invokeControllerMethodNow(
            "updateHealthLabels"
        );

        invokeControllerMethodNow(
            "loadAbilityButtons"
        );

        invokeControllerMethodNow(
            "configureBattleActions"
        );

        HBox actionMenu =
            lookup("#actionMenu")
                .queryAs(HBox.class);

        VBox attackMenu =
            lookup("#attackMenu")
                .queryAs(VBox.class);

        VBox bagMenu =
            lookup("#bagMenu")
                .queryAs(VBox.class);

        actionMenu.setVisible(true);
        actionMenu.setManaged(true);

        attackMenu.setVisible(false);
        attackMenu.setManaged(false);

        bagMenu.setVisible(false);
        bagMenu.setManaged(false);

        lookup("#battleMessageLabel")
            .queryAs(Label.class)
            .setText(
                "Choose an action."
            );

      } catch (Exception exception) {
        throw new RuntimeException(
            exception
        );
      }
    });

    stopMessageTimeline();

    return engine;
  }

  private boolean isItemAllowedInBattle(
      TraderItem item)
      throws Exception {

    Method method =
        BattleController.class
            .getDeclaredMethod(
                "isItemAllowedInBattle",
                TraderItem.class
            );

    method.setAccessible(true);

    return (boolean) method.invoke(
        controller,
        item
    );
  }

  private void setControllerField(
      String fieldName,
      Object value)
      throws Exception {

    Field field =
        BattleController.class
            .getDeclaredField(
                fieldName
            );

    field.setAccessible(true);
    field.set(
        controller,
        value
    );
  }

  private void invokeControllerMethodNow(
      String methodName)
      throws Exception {

    Method method =
        BattleController.class
            .getDeclaredMethod(
                methodName
            );

    method.setAccessible(true);
    method.invoke(controller);
  }

  private void stopMessageTimeline()
      throws Exception {

    Field timelineField =
        BattleController.class
            .getDeclaredField(
                "messageTimeline"
            );

    timelineField.setAccessible(true);

    Timeline timeline =
        (Timeline) timelineField.get(
            controller
        );

    if (timeline != null) {
      interact(timeline::stop);
    }
  }

  private void finishMessageAnimation() {
    sleep(550);
  }
}
