package battle;

import creature.Cat;
import org.junit.jupiter.api.AfterEach;
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
  private BattleEngine battleEngine;

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

    battleEngine = new BattleEngine(playerCat, opponentCat);
  }

  @AfterEach
  void tearDown() {
    playerCat = null;
    opponentCat = null;
    battleEngine = null;
  }

  @Test
  void getPlayerCat() {
    assertSame(playerCat, battleEngine.getPlayerCat());
  }

  @Test
  void getOpponentCat() {
    assertSame(opponentCat, battleEngine.getOpponentCat());
  }

  @Test
  void scratchDamagesOpponent() {
    battleEngine.ability(
        playerCat,
        opponentCat,
        "Scratch"
    );

    assertEquals(90, opponentCat.getHp());
  }

  @Test
  void pounceDamagesOpponent() {
    battleEngine.ability(
        playerCat,
        opponentCat,
        "Pounce"
    );

    assertEquals(85, opponentCat.getHp());
  }

  @Test
  void opponentCanUseSameAbilityMethod() {
    battleEngine.ability(
        opponentCat,
        playerCat,
        "Pounce"
    );

    assertEquals(85, playerCat.getHp());
  }

  @Test
  void healingPurrHealsAttacker() {
    playerCat.setHp(50);

    battleEngine.ability(
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

    battleEngine.heal(playerCat, 20);

    assertEquals(60, playerCat.getHp());
  }

  @Test
  void damageCannotReduceHpBelowZero() {
    opponentCat.setHp(5);

    battleEngine.ability(
        playerCat,
        opponentCat,
        "Scratch"
    );

    assertEquals(0, opponentCat.getHp());
  }

  @Test
  void defeatingOpponentSetsBattleWon() {
    opponentCat.setHp(10);

    battleEngine.ability(
        playerCat,
        opponentCat,
        "Scratch"
    );

    assertTrue(battleEngine.isBattleWon());
  }

  @Test
  void battleIsNotWonWhenOpponentStillHasHp() {
    battleEngine.ability(
        playerCat,
        opponentCat,
        "Scratch"
    );

    assertFalse(battleEngine.isBattleWon());
  }

  @Test
  void defeatingPlayerSetsBattleLost() {
    playerCat.setHp(10);

    battleEngine.ability(
        opponentCat,
        playerCat,
        "Scratch"
    );

    assertTrue(battleEngine.isBattleLost());
  }

  @Test
  void battleIsNotLostWhenPlayerStillHasHp() {
    battleEngine.ability(
        opponentCat,
        playerCat,
        "Scratch"
    );

    assertFalse(battleEngine.isBattleLost());
  }

  @Test
  void unknownAbilityThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> battleEngine.ability(
            playerCat,
            opponentCat,
            "Laser Eyes"
        )
    );
  }

  @Test
  void opponentTurnUsesOpponentAbility() {
    String usedAbility = battleEngine.opponentTurn();

    assertEquals("Pounce", usedAbility);
    assertEquals(85, playerCat.getHp());
  }

  @Test
  void playerTurnUsesPlayerAbility() {
    String usedAbility = battleEngine.playerTurn("Scratch");

    assertEquals("Scratch", usedAbility);
    assertEquals(90, opponentCat.getHp());
  }

  @Test
  void opponentCannotActAfterBattleEnds() {
    opponentCat.setHp(10);

    battleEngine.ability(
        playerCat,
        opponentCat,
        "Scratch"
    );

    assertTrue(battleEngine.isBattleWon());

    assertThrows(
        IllegalStateException.class,
        () -> battleEngine.opponentTurn()
    );
  }

  @Test
  void playerCannotActAfterBattleEnds() {
    playerCat.setHp(10);

    battleEngine.ability(
        opponentCat,
        playerCat,
        "Scratch"
    );

    assertTrue(battleEngine.isBattleLost());

    assertThrows(
        IllegalStateException.class,
        () -> battleEngine.playerTurn("Scratch")
    );
  }

  @Test
  void defeatedCatCannotUseAbility() {
    playerCat.setHp(0);

    assertThrows(
        IllegalStateException.class,
        () -> battleEngine.ability(
            playerCat,
            opponentCat,
            "Scratch"
        )
    );
  }

  @Test
  void abilityCannotBeUsedAfterBattleEnds() {
    opponentCat.setHp(10);

    battleEngine.ability(
        playerCat,
        opponentCat,
        "Scratch"
    );

    assertTrue(battleEngine.isBattleWon());

    assertThrows(
        IllegalStateException.class,
        () -> battleEngine.ability(
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
        () -> battleEngine.playerTurn("Flame Paw")
    );
  }
}