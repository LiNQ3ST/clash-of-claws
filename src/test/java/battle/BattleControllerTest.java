package battle;

import static org.junit.jupiter.api.Assertions.*;

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
  void arenaDaoCannotBeNull() {
    assertThrows(
        IllegalArgumentException.class,
        () -> controller.setArenaDAO(null)
    );
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
  void basicCatchingItemHasFiftyPercentChance()
      throws Exception {

    TraderItem item =
        new TraderItem(
            3,
            "Basic Catching Item",
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
  void strongCatchingItemHasSeventyFivePercentChance()
      throws Exception {

    TraderItem item =
        new TraderItem(
            4,
            "Strong Catching Item",
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
}