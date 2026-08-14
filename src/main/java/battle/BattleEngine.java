package battle;

import creature.Cat;
import java.util.ArrayList;

/**
 * Manages combat logic shared by Wild and Arena battles.
 *
 * @author Quinton Nisonger
 * @version 0.1.0
 * @since 8/9/2026
 */
public class BattleEngine {

  private final Cat playerCat;
  private final Cat opponentCat;
  private final BattleType battleType;
  private BattleResult battleResult;

  public BattleEngine(
      Cat playerCat,
      Cat opponentCat,
      BattleType battleType) {

    if (playerCat == null) {
      throw new IllegalArgumentException(
          "Player cat cannot be null."
      );
    }

    if (opponentCat == null) {
      throw new IllegalArgumentException(
          "Opponent cat cannot be null."
      );
    }

    if (battleType == null) {
      throw new IllegalArgumentException(
          "Battle type cannot be null."
      );
    }

    this.playerCat = playerCat;
    this.opponentCat = opponentCat;
    this.battleType = battleType;
    this.battleResult = BattleResult.IN_PROGRESS;
  }

  public Cat getPlayerCat() {
    return playerCat;
  }

  public Cat getOpponentCat() {
    return opponentCat;
  }

  public BattleResult getBattleResult() {
    return battleResult;
  }

  public BattleType getBattleType() {
    return battleType;
  }

  public boolean isArenaBattle() {
    return BattleType.ARENA.equals(battleType);
  }

  public boolean isBattleOver() {
    return battleResult != BattleResult.IN_PROGRESS;
  }

  public boolean isBattleWon() {
    return battleResult == BattleResult.VICTORY;
  }

  public boolean isBattleLost() {
    return battleResult == BattleResult.DEFEAT;
  }

  protected void ability(
      Cat attacker,
      Cat target,
      String abilityName) {

    validateBattleActive();

    if (attacker == null || target == null) {
      throw new IllegalArgumentException(
          "Attacker and target cannot be null."
      );
    }

    if (attacker.getCurrentHp() <= 0) {
      throw new IllegalStateException(
          "A defeated cat cannot act."
      );
    }

    switch (abilityName) {
      case "SCRATCH" -> attack(target, 10);
      case "POUNCE" -> attack(target, 15);
      case "HAIRBALL" -> attack(target, 8);
      case "HEALING_PURR" -> heal(attacker, 20);
      case "NIGHT_CLAW" -> attack(target, 18);
      case "FLAME_PAW" -> attack(target, 20);
      case "TAIL_WHIP" -> attack(target, 12);
      case "ZOOMIES" -> attack(target, 4);
      default -> throw new IllegalArgumentException(
          "Unknown ability: " + abilityName
      );
    }
  }

  public void heal(Cat target, int amount) {
    validateBattleActive();

    if (target == null) {
      throw new IllegalArgumentException(
          "Healing target cannot be null."
      );
    }

    if (amount < 0) {
      throw new IllegalArgumentException(
          "Healing amount cannot be negative."
      );
    }

    target.setCurrentHp(
        target.getCurrentHp() + amount
    );
  }

  public boolean attemptRun(int roll) {
    validateBattleActive();

    if (battleType != BattleType.WILD) {
      throw new IllegalStateException(
          "Run is only available during wild battles."
      );
    }

    if (roll < 0 || roll > 99) {
      throw new IllegalArgumentException(
          "Run roll must be between 0 and 99."
      );
    }

    if (roll >= 50) {
      battleResult = BattleResult.ESCAPED;
      return true;
    }

    return false;
  }


  public String opponentTurn() {
    validateBattleActive();

    ArrayList<String> abilities = opponentCat.getAbilities();

    if (abilities == null || abilities.isEmpty()) {
      throw new IllegalStateException(
              "Opponent cat has no abilities."
      );
    }

    String abilityName = abilities.get(0);

    ability(
            opponentCat,
            playerCat,
            abilityName
    );

    return abilityName;
  }


  public String playerTurn(String abilityName) {
    validateBattleActive();

    ArrayList<String> abilities = playerCat.getAbilities();

    if (abilities == null || !abilities.contains(abilityName)) {
      throw new IllegalArgumentException(
              "Player cat does not know ability: " + abilityName
      );
    }

    ability(
            playerCat,
            opponentCat,
            abilityName
    );

    return abilityName;
  }



  public int getAbilityAmount(String abilityName) {
    return switch (abilityName) {
      case "SCRATCH" -> 10;
      case "POUNCE" -> 15;
      case "HAIRBALL" -> 8;
      case "HEALING_PURR" -> 20;
      case "NIGHT_CLAW" -> 18;
      case "FLAME_PAW" -> 20;
      case "TAIL_WHIP" -> 12;
      case "ZOOMIES" -> 4;
      default -> throw new IllegalArgumentException(
          "Unknown ability: " + abilityName
      );
    };
  }

  private void attack(Cat target, int amount) {
    int newHp = target.getCurrentHp() - amount;

    if (newHp <= 0) {
      target.setCurrentHp(0);

      if (target == opponentCat) {
        battleResult = BattleResult.VICTORY;
      } else if (target == playerCat) {
        battleResult = BattleResult.DEFEAT;
      }

      return;
    }

    target.setCurrentHp(newHp);
  }

  private void validateBattleActive() {
    if (isBattleOver()) {
      throw new IllegalStateException(
          "Battle is already over."
      );
    }
  }
}