package battle;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import creature.Cat;
import java.util.ArrayList;
import java.lang.reflect.Method;
import marketplace.TraderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for BattleController behavior that does not require
 * launching the complete JavaFX scene.
 *
 * @author Quinton Nisonger
 * @version 0.1.0
 * @since 8/10/2026
 */
class BattleControllerTest {

  private BattleController controller;
  private Method formatAbilityName;

  @BeforeEach
  void setUp() throws Exception {
    controller = new BattleController();

    formatAbilityName =
        BattleController.class
            .getDeclaredMethod(
                "formatAbilityName",
                String.class
            );

    formatAbilityName.setAccessible(true);
  }

  @Test
  void formatsSingleWordAbilityForDisplay()
      throws Exception {

    String formatted =
        (String) formatAbilityName.invoke(
            controller,
            "SCRATCH"
        );

    assertEquals("Scratch", formatted);
  }

  @Test
  void formatsMultiWordAbilityForDisplay()
      throws Exception {

    String formatted =
        (String) formatAbilityName.invoke(
            controller,
            "HEALING_PURR"
        );

    assertEquals("Healing Purr", formatted);
  }

  @Test
  void formatsAdditionalMultiWordAbility()
      throws Exception {

    String formatted =
        (String) formatAbilityName.invoke(
            controller,
            "NIGHT_CLAW"
        );

    assertEquals("Night Claw", formatted);
  }

  @Test
  void smallPotionHealsTenHp()
      throws Exception {

    TraderItem item =
        new TraderItem(
            1,
            "Small Potion",
            "HEALING",
            "Restores a small amount of health.",
            10,
            5
        );

    Method getHealingAmount =
        BattleController.class
            .getDeclaredMethod(
                "getHealingAmount",
                TraderItem.class
            );

    getHealingAmount.setAccessible(true);

    int amount =
        (int) getHealingAmount.invoke(
            controller,
            item
        );

    assertEquals(10, amount);
  }

  @Test
  void largePotionHealsTwentyHp()
      throws Exception {

    TraderItem item =
        new TraderItem(
            2,
            "Large Potion",
            "HEALING",
            "Restores a large amount of health.",
            20,
            5
        );

    Method getHealingAmount =
        BattleController.class
            .getDeclaredMethod(
                "getHealingAmount",
                TraderItem.class
            );

    getHealingAmount.setAccessible(true);

    int amount =
        (int) getHealingAmount.invoke(
            controller,
            item
        );

    assertEquals(20, amount);
  }

  @Test
  void unknownHealingItemIsRejected()
      throws Exception {

    TraderItem item =
        new TraderItem(
            3,
            "Mystery Potion",
            "HEALING",
            "Unknown.",
            10,
            1
        );

    Method getHealingAmount =
        BattleController.class
            .getDeclaredMethod(
                "getHealingAmount",
                TraderItem.class
            );

    getHealingAmount.setAccessible(true);

    Exception exception =
        assertThrows(
            Exception.class,
            () -> getHealingAmount.invoke(
                controller,
                item
            )
        );

    assertTrue(
        exception.getCause()
            instanceof IllegalArgumentException
    );
  }

  @Test
  void toyMouseHasFiftyPercentBaseCatchChance()
      throws Exception {

    TraderItem item =
        new TraderItem(
            3,
            "Toy Mouse",
            "CATCHING",
            "A basic catching item.",
            25,
            5
        );

    Method getCatchChance =
        BattleController.class
            .getDeclaredMethod(
                "getCatchChance",
                TraderItem.class
            );

    getCatchChance.setAccessible(true);

    int chance =
        (int) getCatchChance.invoke(
            controller,
            item
        );

    assertEquals(50, chance);
  }

  @Test
  void tunaCanHasSeventyFivePercentBaseCatchChance()
      throws Exception {

    TraderItem item =
        new TraderItem(
            4,
            "Tuna Can",
            "CATCHING",
            "A stronger catching item.",
            50,
            5
        );

    Method getCatchChance =
        BattleController.class
            .getDeclaredMethod(
                "getCatchChance",
                TraderItem.class
            );

    getCatchChance.setAccessible(true);

    int chance =
        (int) getCatchChance.invoke(
            controller,
            item
        );

    assertEquals(75, chance);
  }

  @Test
  void basicCatchSucceedsAtRollFortyNine()
      throws Exception {

    assertCaptureResult(50, 49, true);
  }

  @Test
  void basicCatchFailsAtRollFifty()
      throws Exception {

    assertCaptureResult(50, 50, false);
  }

  @Test
  void strongCatchSucceedsAtRollSeventyFour()
      throws Exception {

    assertCaptureResult(75, 74, true);
  }

  @Test
  void strongCatchFailsAtRollSeventyFive()
      throws Exception {

    assertCaptureResult(75, 75, false);
  }

  private void assertCaptureResult(
      int catchChance,
      int roll,
      boolean expected)
      throws Exception {

    Method isCaptureSuccessful =
        BattleController.class
            .getDeclaredMethod(
                "isCaptureSuccessful",
                int.class,
                int.class
            );

    isCaptureSuccessful.setAccessible(true);

    boolean captured =
        (boolean) isCaptureSuccessful.invoke(
            controller,
            catchChance,
            roll
        );

    assertEquals(expected, captured);
  }

  @Test
  void arenaAllowsHealingItems()
      throws Exception {

    setBattleType(BattleType.ARENA);

    TraderItem healingItem =
        new TraderItem(
            1,
            "Small Potion",
            "HEALING",
            "Restores health.",
            10,
            5
        );

    Method isItemAllowedInBattle =
        BattleController.class
            .getDeclaredMethod(
                "isItemAllowedInBattle",
                TraderItem.class
            );

    isItemAllowedInBattle.setAccessible(true);

    boolean allowed =
        (boolean) isItemAllowedInBattle.invoke(
            controller,
            healingItem
        );

    assertTrue(allowed);
  }

  @Test
  void arenaRejectsCatchingItems()
      throws Exception {

    setBattleType(BattleType.ARENA);

    TraderItem catchingItem =
        new TraderItem(
            2,
            "Basic Catching Item",
            "CATCHING",
            "Used to catch wild cats.",
            25,
            5
        );

    Method isItemAllowedInBattle =
        BattleController.class
            .getDeclaredMethod(
                "isItemAllowedInBattle",
                TraderItem.class
            );

    isItemAllowedInBattle.setAccessible(true);

    boolean allowed =
        (boolean) isItemAllowedInBattle.invoke(
            controller,
            catchingItem
        );

    assertFalse(allowed);
  }

  private void setBattleType(BattleType battleType)
      throws Exception {

    ArrayList<String> playerAbilities =
        new ArrayList<>();

    playerAbilities.add("SCRATCH");
    playerAbilities.add("HEALING_PURR");

    ArrayList<String> opponentAbilities =
        new ArrayList<>();

    opponentAbilities.add("POUNCE");

    Cat playerCat =
        new Cat(
            "Whiskers",
            "Tabby",
            100,
            playerAbilities,
            true,
            true
        );

    Cat opponentCat =
        new Cat(
            "Clawdia",
            "Sphynx",
            100,
            opponentAbilities,
            false,
            false
        );

    BattleEngine engine =
        new BattleEngine(
            playerCat,
            opponentCat,
            battleType
        );

    Field battleEngineField =
        BattleController.class
            .getDeclaredField(
                "battleEngine"
            );

    battleEngineField.setAccessible(true);

    battleEngineField.set(
        controller,
        engine
    );
  }

  @Test
  void wildAllowsCatchingItems()
      throws Exception {

    setBattleType(BattleType.WILD);

    TraderItem catchingItem =
        new TraderItem(
            2,
            "Basic Catching Item",
            "CATCHING",
            "Used to catch wild cats.",
            25,
            5
        );

    Method isItemAllowedInBattle =
        BattleController.class
            .getDeclaredMethod(
                "isItemAllowedInBattle",
                TraderItem.class
            );

    isItemAllowedInBattle.setAccessible(true);

    boolean allowed =
        (boolean) isItemAllowedInBattle.invoke(
            controller,
            catchingItem
        );

    assertTrue(allowed);
  }

  @Test
  void debugItemsTriggerOnTenthOpponentClick()
      throws Exception {

    Field clickCountField =
        BattleController.class
            .getDeclaredField(
                "opponentDebugClickCount"
            );

    clickCountField.setAccessible(true);

    Method rewardReady =
        BattleController.class
            .getDeclaredMethod(
                "isDebugItemRewardReady"
            );

    rewardReady.setAccessible(true);

    for (int i = 1; i < 10; i++) {

      clickCountField.setInt(
          controller,
          i
      );

      boolean ready =
          (boolean) rewardReady.invoke(
              controller
          );

      assertFalse(
          ready,
          "Reward should not trigger at "
              + i
              + " clicks."
      );
    }

    clickCountField.setInt(
        controller,
        10
    );

    boolean ready =
        (boolean) rewardReady.invoke(
            controller
        );

    assertTrue(
        ready,
        "Reward should trigger on click 10."
    );
  }

  @Test
  void debugItemsDoNotTriggerAtNineClicks()
      throws Exception {

    Field clickCountField =
        BattleController.class
            .getDeclaredField(
                "opponentDebugClickCount"
            );

    clickCountField.setAccessible(true);

    clickCountField.setInt(
        controller,
        9
    );

    Method rewardReady =
        BattleController.class
            .getDeclaredMethod(
                "isDebugItemRewardReady"
            );

    rewardReady.setAccessible(true);

    boolean ready =
        (boolean) rewardReady.invoke(
            controller
        );

    assertFalse(ready);
  }

  @Test
  void toyMouseCatchChanceIncreasesBelowHalfHealth()
      throws Exception {

    TraderItem item =
        new TraderItem(
            3,
            "Toy Mouse",
            "CATCHING",
            "A basic catching item.",
            25,
            5
        );

    ArrayList<String> abilities =
        new ArrayList<>();

    abilities.add("SCRATCH");

    Cat playerCat =
        new Cat(
            "Player",
            "Tabby",
            100,
            abilities,
            true,
            true
        );

    Cat opponentCat =
        new Cat(
            "Opponent",
            "Siamese",
            100,
            abilities,
            false,
            false
        );

    opponentCat.setCurrentHp(50);

    BattleEngine engine =
        new BattleEngine(
            playerCat,
            opponentCat,
            BattleType.WILD
        );

    setBattleEngine(engine);

    Method method =
        BattleController.class
            .getDeclaredMethod(
                "getAdjustedCatchChance",
                TraderItem.class
            );

    method.setAccessible(true);

    int chance =
        (int) method.invoke(
            controller,
            item
        );

    assertEquals(
        60,
        chance
    );
  }

  @Test
  void toyMouseCatchChanceIncreasesAtQuarterHealth()
      throws Exception {

    TraderItem item =
        new TraderItem(
            3,
            "Toy Mouse",
            "CATCHING",
            "A basic catching item.",
            25,
            5
        );

    ArrayList<String> abilities =
        new ArrayList<>();

    abilities.add("SCRATCH");

    Cat playerCat =
        new Cat(
            "Player",
            "Tabby",
            100,
            abilities,
            true,
            true
        );

    Cat opponentCat =
        new Cat(
            "Opponent",
            "Siamese",
            100,
            abilities,
            false,
            false
        );

    opponentCat.setCurrentHp(25);

    BattleEngine engine =
        new BattleEngine(
            playerCat,
            opponentCat,
            BattleType.WILD
        );

    setBattleEngine(engine);

    Method method =
        BattleController.class
            .getDeclaredMethod(
                "getAdjustedCatchChance",
                TraderItem.class
            );

    method.setAccessible(true);

    int chance =
        (int) method.invoke(
            controller,
            item
        );

    assertEquals(
        70,
        chance
    );
  }

  @Test
  void tunaCanCatchChanceCapsAtNinetyFive()
      throws Exception {

    TraderItem item =
        new TraderItem(
            4,
            "Tuna Can",
            "CATCHING",
            "A stronger catching item.",
            50,
            5
        );

    ArrayList<String> abilities =
        new ArrayList<>();

    abilities.add("SCRATCH");

    Cat playerCat =
        new Cat(
            "Player",
            "Tabby",
            100,
            abilities,
            true,
            true
        );

    Cat opponentCat =
        new Cat(
            "Opponent",
            "Siamese",
            100,
            abilities,
            false,
            false
        );

    opponentCat.setCurrentHp(10);

    BattleEngine engine =
        new BattleEngine(
            playerCat,
            opponentCat,
            BattleType.WILD
        );

    setBattleEngine(engine);

    Method method =
        BattleController.class
            .getDeclaredMethod(
                "getAdjustedCatchChance",
                TraderItem.class
            );

    method.setAccessible(true);

    int chance =
        (int) method.invoke(
            controller,
            item
        );

    assertEquals(
        95,
        chance
    );
  }

  private void setBattleEngine(
      BattleEngine engine)
      throws Exception {

    Field field =
        BattleController.class
            .getDeclaredField(
                "battleEngine"
            );

    field.setAccessible(true);

    field.set(
        controller,
        engine
    );
  }
}