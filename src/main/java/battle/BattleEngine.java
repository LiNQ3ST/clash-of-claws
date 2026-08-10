package battle;

import creature.Cat;
import java.util.ArrayList;

/**
 * Manages combat logic between a player's cat and an opponent cat.
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

  public BattleEngine(Cat playerCat, Cat opponentCat, BattleType battleType) {

    if (playerCat == null) {
      throw new IllegalArgumentException(
          "Player cat cannot be null.");
    }

    if (opponentCat == null) {
      throw new IllegalArgumentException(
          "Opponent cat cannot be null.");
    }

    if (battleType == null) {
      throw new IllegalArgumentException(
          "Battle type cannot be null.");
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

  // TODO delete
  public int getMaxHp(Cat cat) {
    return switch (cat.getType()) {
      case "Tabby" -> 100;
      case "Sphinx" -> 90;
      case "Maine Coon" -> 120;
      case "Siamese" -> 95;
      default -> 100;
    };
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

  protected void ability(Cat attacker, Cat target, String abilityName) {

    validateBattleActive();

    //TODO delete
    if (attacker.getHp() <= 0) {
      throw new IllegalStateException("A defeated cat cannot act.");
    }

    /* TODO
    if (attacker.getCurrentHp() <= 0) {
      throw new IllegalStateException(
          "A defeated cat cannot act.");
    }
     */

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
          "Unknown ability: " + abilityName);
    }
  }

  // TODO delete
  public void heal(Cat target, int amount) {
    int maxHp = getMaxHp(target);
    int healedHp = target.getHp() + amount;

    target.setHp(Math.min(healedHp, maxHp));
  }

  /* TODO
  private void heal(Cat target, int amount) {
  if (amount < 0) {
    throw new IllegalArgumentException(
        "Healing amount cannot be negative.");
  }

  target.setCurrentHp(
      target.getCurrentHp() + amount
  );
}
   */

  // TODO delete
  private void attack(Cat target, int amount) {
    int newHp = target.getHp() - amount;

    if (newHp <= 0) {
      target.setHp(0);

      if (target == opponentCat) {
        battleResult = BattleResult.VICTORY;
      } else if (target == playerCat) {
        battleResult = BattleResult.DEFEAT;
      }
    } else {
      target.setHp(newHp);
    }
  }

  // TODO opponent attack logic (NPC AI)
  /* TODO
  private void attack(Cat target, int amount) {
  if (amount < 0) {
    throw new IllegalArgumentException(
        "Damage amount cannot be negative.");
  }

  target.setCurrentHp(
      target.getCurrentHp() - amount
  );

  if (target.getCurrentHp() == 0) {
    if (target == opponentCat) {
      battleResult = BattleResult.VICTORY;
    } else if (target == playerCat) {
      battleResult = BattleResult.DEFEAT;
    }
  }
}
   */

  // TODO make sure run button only visible in wild battles
  public boolean attemptRun(int roll) {
    validateBattleActive();

    if (battleType != BattleType.WILD) {
      throw new IllegalStateException(
          "Run is only available during wild battles.");
    }

    if (roll < 0 || roll > 99) {
      throw new IllegalArgumentException(
          "Escape roll must be between 0 and 99.");
    }

    boolean escaped = roll >= 50;

    if (escaped) {
      battleResult = BattleResult.ESCAPED;
    }

    return escaped;
  }

  public String opponentTurn() {
    validateBattleActive();

    ArrayList<String> abilities = opponentCat.getAbilities();

    if (abilities.isEmpty()) {
      throw new IllegalArgumentException("No abilities found");
    }

    String abilityName = abilities.getFirst();
    ability(opponentCat, playerCat, abilityName);

    return abilityName;
  }

  public String playerTurn(String abilityName) {
    validateBattleActive();

    if (!playerCat.getAbilities().contains(abilityName)) {
      throw new IllegalArgumentException(
          "Player cat does not have ability: " + abilityName
      );
    }

    ability(playerCat, opponentCat, abilityName);

    return abilityName;
  }

  private void validateBattleActive() {
    if (isBattleOver()) {
      throw new IllegalStateException(
          "Battle has already ended.");
    }
  }

}

