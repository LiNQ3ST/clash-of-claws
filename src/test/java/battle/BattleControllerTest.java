package battle;

import static org.junit.jupiter.api.Assertions.*;

import creature.Cat;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for BattleController behavior that does not require
 * launching the complete JavaFX scene.
 * A separate TestFX scene test should still be added for the final
 * UI acceptance requirement.
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
    controller =
        new BattleController();

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

    assertEquals(
        "Scratch",
        formatted
    );
  }

  @Test
  void formatsMultiWordAbilityForDisplay()
      throws Exception {

    String formatted =
        (String) formatAbilityName.invoke(
            controller,
            "HEALING_PURR"
        );

    assertEquals(
        "Healing Purr",
        formatted
    );
  }

  @Test
  void formatsAdditionalMultiWordAbility()
      throws Exception {

    String formatted =
        (String) formatAbilityName.invoke(
            controller,
            "NIGHT_CLAW"
        );

    assertEquals(
        "Night Claw",
        formatted
    );
  }

  @Test
  void arenaVictoryDoesNotRunWildVictoryPersistence()
      throws Exception {

    ArrayList<String> playerAbilities =
        new ArrayList<>();

    playerAbilities.add(
        "SCRATCH"
    );

    ArrayList<String> opponentAbilities =
        new ArrayList<>();

    opponentAbilities.add(
        "POUNCE"
    );

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
            "Arena Cat",
            "Sphinx",
            10,
            opponentAbilities,
            false,
            false
        );

    BattleEngine arenaEngine =
        new BattleEngine(
            playerCat,
            opponentCat,
            BattleType.ARENA
        );

    arenaEngine.playerTurn(
        "SCRATCH"
    );

    assertEquals(
        BattleResult.VICTORY,
        arenaEngine.getBattleResult()
    );

    Field battleEngineField =
        BattleController.class
            .getDeclaredField(
                "battleEngine"
            );

    battleEngineField.setAccessible(true);
    battleEngineField.set(
        controller,
        arenaEngine
    );

    Method handleVictory =
        BattleController.class
            .getDeclaredMethod(
                "handleVictory"
            );

    handleVictory.setAccessible(true);

    assertDoesNotThrow(
        () -> handleVictory.invoke(
            controller
        )
    );
  }
}
