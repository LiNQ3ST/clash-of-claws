package battle;

import creature.Cat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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
    playerAbilities.add("Scratch");
    playerAbilities.add("Healing Purr");

    ArrayList<String> opponentAbilities = new ArrayList<>();
    opponentAbilities.add("Pounce");
    opponentAbilities.add("Tail Whip");

    playerCat = new Cat(
        "Whiskers",
        "Tabby",
        100,
        playerAbilities,
        true
    );

    opponentCat = new Cat(
        "Clawdia",
        "Sphinx",
        100,
        opponentAbilities,
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
  void getPlayerCat() {
    assertSame(
        playerCat,
        wildBattleEngine.getPlayerCat()
    );
  }

  @Test
  void getOpponentCat() {
    assertSame(
        opponentCat,
        wildBattleEngine.getOpponentCat()
    );
  }

  @Test
  void scratchDamagesOpponent() {
    wildBattleEngine.ability(
        playerCat,
        opponentCat,
        "Scratch"
    );

    assertEquals(90, opponentCat.getHp());
  }

  @Test
  void pounceDamagesOpponent() {
    wildBattleEngine.ability(
        playerCat,
        opponentCat,
        "Pounce"
    );

    assertEquals(85, opponentCat.getHp());
  }

  @Test
  void opponentCanUseSameAbilityMethod() {
    wildBattleEngine.ability(
        opponentCat,
        playerCat,
        "Pounce"
    );

    assertEquals(85, playerCat.getHp());
  }

  @Test
  void healingPurrHealsAttacker() {
    playerCat.setHp(50);

    wildBattleEngine.ability(
        playerCat,
        opponentCat,
        "Healing Purr"
    );

    assertEquals(70, playerCat.getHp());
    assertEquals(100, opponentCat.getHp());
  }

  @Test
  void healIncreasesTargetHp() {
    playerCat.setHp(40);

    wildBattleEngine.heal(playerCat, 20);

    assertEquals(60, playerCat.getHp());
  }

  @Test
  void damageCannotReduceHpBelowZero() {
    opponentCat.setHp(5);

    wildBattleEngine.ability(
        playerCat,
        opponentCat,
        "Scratch"
    );

    assertEquals(0, opponentCat.getHp());
  }

  @Test
  void defeatingOpponentSetsBattleWon() {
    opponentCat.setHp(10);

    wildBattleEngine.ability(
        playerCat,
        opponentCat,
        "Scratch"
    );

    assertTrue(wildBattleEngine.isBattleWon());
  }

  @Test
  void battleIsNotWonWhenOpponentStillHasHp() {
    wildBattleEngine.ability(
        playerCat,
        opponentCat,
        "Scratch"
    );

    assertFalse(wildBattleEngine.isBattleWon());
  }

  @Test
  void defeatingPlayerSetsBattleLost() {
    playerCat.setHp(10);

    wildBattleEngine.ability(
        opponentCat,
        playerCat,
        "Scratch"
    );

    assertTrue(wildBattleEngine.isBattleLost());
  }

  @Test
  void battleIsNotLostWhenPlayerStillHasHp() {
    wildBattleEngine.ability(
        opponentCat,
        playerCat,
        "Scratch"
    );

    assertFalse(wildBattleEngine.isBattleLost());
  }

  @Test
  void unknownAbilityThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> wildBattleEngine.ability(
            playerCat,
            opponentCat,
            "Laser Eyes"
        )
    );
  }

  @Test
  void opponentTurnUsesOpponentAbility() {
    String usedAbility =
        wildBattleEngine.opponentTurn();

    assertEquals("Pounce", usedAbility);
    assertEquals(85, playerCat.getHp());
  }

  @Test
  void playerTurnUsesPlayerAbility() {
    String usedAbility =
        wildBattleEngine.playerTurn("Scratch");

    assertEquals("Scratch", usedAbility);
    assertEquals(90, opponentCat.getHp());
  }

  @Test
  void opponentCannotActAfterBattleEnds() {
    opponentCat.setHp(10);

    wildBattleEngine.ability(
        playerCat,
        opponentCat,
        "Scratch"
    );

    assertTrue(wildBattleEngine.isBattleWon());

    assertThrows(
        IllegalStateException.class,
        wildBattleEngine::opponentTurn
    );
  }

  @Test
  void playerCannotActAfterBattleEnds() {
    playerCat.setHp(10);

    wildBattleEngine.ability(
        opponentCat,
        playerCat,
        "Scratch"
    );

    assertTrue(wildBattleEngine.isBattleLost());

    assertThrows(
        IllegalStateException.class,
        () -> wildBattleEngine.playerTurn("Scratch")
    );
  }

  @Test
  void defeatedCatCannotUseAbility() {
    playerCat.setHp(0);

    assertThrows(
        IllegalStateException.class,
        () -> wildBattleEngine.ability(
            playerCat,
            opponentCat,
            "Scratch"
        )
    );
  }

  @Test
  void abilityCannotBeUsedAfterBattleEnds() {
    opponentCat.setHp(10);

    wildBattleEngine.ability(
        playerCat,
        opponentCat,
        "Scratch"
    );

    assertTrue(wildBattleEngine.isBattleWon());

    assertThrows(
        IllegalStateException.class,
        () -> wildBattleEngine.ability(
            playerCat,
            opponentCat,
            "Scratch"
        )
    );
  }

  @Test
  void playerCannotUseAbilityTheyDoNotHave() {
    assertThrows(
        IllegalArgumentException.class,
        () -> wildBattleEngine.playerTurn("Flame Paw")
    );
  }

  @Test
  void wildBattleHasWildBattleType() {
    assertEquals(
        BattleType.WILD,
        wildBattleEngine.getBattleType()
    );
  }

  @Test
  void arenaBattleHasArenaBattleType() {
    assertEquals(
        BattleType.ARENA,
        arenaBattleEngine.getBattleType()
    );
  }

  @Test
  void wildBattleCanRunSuccessfully() {
    boolean escaped =
        wildBattleEngine.attemptRun(75);

    assertTrue(escaped);

    assertEquals(
        BattleResult.ESCAPED,
        wildBattleEngine.getBattleResult()
    );
  }

  @Test
  void wildBattleCanFailToRun() {
    boolean escaped =
        wildBattleEngine.attemptRun(25);

    assertFalse(escaped);

    assertEquals(
        BattleResult.IN_PROGRESS,
        wildBattleEngine.getBattleResult()
    );
  }

  @Test
  void arenaBattleCannotRun() {
    assertThrows(
        IllegalStateException.class,
        () -> arenaBattleEngine.attemptRun(75)
    );
  }
}