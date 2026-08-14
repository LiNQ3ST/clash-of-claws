package battle;

import static org.junit.jupiter.api.Assertions.*;

import creature.Cat;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the shared BattleEngine logic.
 *
 * @author Quinton Nisonger
 * @version 0.1.0
 * @since 8/9/2026
 */
class BattleEngineTest {

  private Cat playerCat;
  private Cat opponentCat;
  private BattleEngine wildBattleEngine;
  private BattleEngine arenaBattleEngine;

  @BeforeEach
  void setUp() {
    ArrayList<String> playerAbilities = new ArrayList<>();
    playerAbilities.add("SCRATCH");
    playerAbilities.add("HEALING_PURR");

    ArrayList<String> opponentAbilities = new ArrayList<>();
    opponentAbilities.add("POUNCE");
    opponentAbilities.add("TAIL_WHIP");

    playerCat = new Cat(
        "Whiskers",
        "Tabby",
        100,
        playerAbilities,
        true,
        true
    );

    opponentCat = new Cat(
        "Clawdia",
        "Sphynx",
        100,
        opponentAbilities,
        false,
        false
    );

    wildBattleEngine = new BattleEngine(
        playerCat,
        opponentCat,
        BattleType.WILD
    );

    arenaBattleEngine = new BattleEngine(
        playerCat,
        opponentCat,
        BattleType.ARENA
    );
  }

  @Test
  void constructorRejectsNullPlayerCat() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new BattleEngine(null, opponentCat, BattleType.WILD)
    );
  }

  @Test
  void constructorRejectsNullOpponentCat() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new BattleEngine(playerCat, null, BattleType.WILD)
    );
  }

  @Test
  void constructorRejectsNullBattleType() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new BattleEngine(playerCat, opponentCat, null)
    );
  }

  @Test
  void newBattleStartsInProgress() {
    assertEquals(
        BattleResult.IN_PROGRESS,
        wildBattleEngine.getBattleResult()
    );
    assertFalse(wildBattleEngine.isBattleOver());
  }

  @Test
  void exposesBattleParticipants() {
    assertSame(playerCat, wildBattleEngine.getPlayerCat());
    assertSame(opponentCat, wildBattleEngine.getOpponentCat());
    assertEquals(100, playerCat.getCurrentHp());
    assertEquals(100, opponentCat.getCurrentHp());
  }

  @Test
  void scratchDamagesOpponentCurrentHp() {
    wildBattleEngine.ability(
        playerCat,
        opponentCat,
        "SCRATCH"
    );

    assertEquals(90, opponentCat.getCurrentHp());
  }

  @Test
  void pounceDamagesOpponentCurrentHp() {
    wildBattleEngine.ability(
        playerCat,
        opponentCat,
        "POUNCE"
    );

    assertEquals(85, opponentCat.getCurrentHp());
  }

  @Test
  void opponentCanUseSameAbilityMethod() {
    wildBattleEngine.ability(
        opponentCat,
        playerCat,
        "POUNCE"
    );

    assertEquals(85, playerCat.getCurrentHp());
  }

  @Test
  void healingPurrHealsAttacker() {
    playerCat.setCurrentHp(50);

    wildBattleEngine.ability(
        playerCat,
        opponentCat,
        "HEALING_PURR"
    );

    assertEquals(70, playerCat.getCurrentHp());
    assertEquals(100, opponentCat.getCurrentHp());
  }

  @Test
  void healDoesNotExceedMaximumHp() {
    playerCat.setCurrentHp(95);

    wildBattleEngine.heal(
        playerCat,
        20
    );

    assertEquals(100, playerCat.getCurrentHp());
  }

  @Test
  void damageCannotReduceHpBelowZero() {
    opponentCat.setCurrentHp(5);

    wildBattleEngine.ability(
        playerCat,
        opponentCat,
        "SCRATCH"
    );

    assertEquals(0, opponentCat.getCurrentHp());
  }

  @Test
  void defeatingOpponentSetsBattleWon() {
    opponentCat.setCurrentHp(10);

    wildBattleEngine.ability(
        playerCat,
        opponentCat,
        "SCRATCH"
    );

    assertTrue(wildBattleEngine.isBattleWon());
    assertTrue(wildBattleEngine.isBattleOver());
    assertEquals(
        BattleResult.VICTORY,
        wildBattleEngine.getBattleResult()
    );
  }

  @Test
  void defeatingPlayerSetsDefeat() {
    playerCat.setCurrentHp(10);

    wildBattleEngine.ability(
        opponentCat,
        playerCat,
        "SCRATCH"
    );

    assertTrue(wildBattleEngine.isBattleLost());
    assertTrue(wildBattleEngine.isBattleOver());
    assertEquals(
        BattleResult.DEFEAT,
        wildBattleEngine.getBattleResult()
    );
  }

  @Test
  void unknownAbilityThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> wildBattleEngine.ability(
            playerCat,
            opponentCat,
            "LASER_EYES"
        )
    );
  }

  @Test
  void opponentTurnUsesFirstOpponentAbility() {
    String usedAbility = wildBattleEngine.opponentTurn();

    assertEquals("POUNCE", usedAbility);
    assertEquals(85, playerCat.getCurrentHp());
  }

  @Test
  void playerTurnUsesOwnedAbility() {
    String usedAbility =
        wildBattleEngine.playerTurn("SCRATCH");

    assertEquals("SCRATCH", usedAbility);
    assertEquals(90, opponentCat.getCurrentHp());
  }

  @Test
  void playerCannotUseAbilityTheyDoNotHave() {
    assertThrows(
        IllegalArgumentException.class,
        () -> wildBattleEngine.playerTurn("FLAME_PAW")
    );
  }

  @Test
  void defeatedCatCannotAct() {
    playerCat.setCurrentHp(0);

    assertThrows(
        IllegalStateException.class,
        () -> wildBattleEngine.ability(
            playerCat,
            opponentCat,
            "SCRATCH"
        )
    );
  }

  @Test
  void noActionsAllowedAfterVictory() {
    opponentCat.setCurrentHp(10);
    wildBattleEngine.playerTurn("SCRATCH");

    assertEquals(
        BattleResult.VICTORY,
        wildBattleEngine.getBattleResult()
    );

    assertThrows(
        IllegalStateException.class,
        wildBattleEngine::opponentTurn
    );

    assertThrows(
        IllegalStateException.class,
        () -> wildBattleEngine.playerTurn("SCRATCH")
    );
  }

  @Test
  void battleTypesAreStoredCorrectly() {
    assertEquals(
        BattleType.WILD,
        wildBattleEngine.getBattleType()
    );

    assertEquals(
        BattleType.ARENA,
        arenaBattleEngine.getBattleType()
    );
  }

  @Test
  void wildBattleEscapesAtBoundaryRoll() {
    boolean escaped = wildBattleEngine.attemptRun(50);

    assertTrue(escaped);
    assertEquals(
        BattleResult.ESCAPED,
        wildBattleEngine.getBattleResult()
    );
  }

  @Test
  void wildBattleFailsEscapeBelowBoundary() {
    boolean escaped = wildBattleEngine.attemptRun(49);

    assertFalse(escaped);
    assertEquals(
        BattleResult.IN_PROGRESS,
        wildBattleEngine.getBattleResult()
    );
  }

  @Test
  void escapeRollMustBeBetweenZeroAndNinetyNine() {
    assertThrows(
        IllegalArgumentException.class,
        () -> wildBattleEngine.attemptRun(-1)
    );

    assertThrows(
        IllegalArgumentException.class,
        () -> wildBattleEngine.attemptRun(100)
    );
  }

  @Test
  void arenaBattleCannotRun() {
    assertThrows(
        IllegalStateException.class,
        () -> arenaBattleEngine.attemptRun(75)
    );
  }

  @Test
  void noActionsAllowedAfterSuccessfulEscape() {
    assertTrue(wildBattleEngine.attemptRun(75));

    assertThrows(
        IllegalStateException.class,
        () -> wildBattleEngine.playerTurn("SCRATCH")
    );

    assertThrows(
        IllegalStateException.class,
        wildBattleEngine::opponentTurn
    );

    assertThrows(
        IllegalStateException.class,
        () -> wildBattleEngine.attemptRun(75)
    );
  }

  @Test
  void returnsCorrectScratchAmount() {
    assertEquals(
        10,
        wildBattleEngine.getAbilityAmount("SCRATCH")
    );
  }

  @Test
  void returnsCorrectHealingPurrAmount() {
    assertEquals(
        20,
        wildBattleEngine.getAbilityAmount("HEALING_PURR")
    );
  }

  @Test
  void returnsCorrectAbilityAmounts() {
    assertEquals(15, wildBattleEngine.getAbilityAmount("POUNCE"));
    assertEquals(8, wildBattleEngine.getAbilityAmount("HAIRBALL"));
    assertEquals(18, wildBattleEngine.getAbilityAmount("NIGHT_CLAW"));
    assertEquals(20, wildBattleEngine.getAbilityAmount("FLAME_PAW"));
    assertEquals(12, wildBattleEngine.getAbilityAmount("TAIL_WHIP"));
    assertEquals(4, wildBattleEngine.getAbilityAmount("ZOOMIES"));
  }

  @Test
  void getAbilityAmountRejectsUnknownAbility() {
    assertThrows(
        IllegalArgumentException.class,
        () -> wildBattleEngine.getAbilityAmount("LASER_EYES")
    );
  }
}