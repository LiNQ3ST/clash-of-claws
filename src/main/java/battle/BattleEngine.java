package battle;

import creature.Cat;

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
  private boolean battleWon;

  public BattleEngine(Cat playerCat, Cat opponentCat) {
    this.playerCat = playerCat;
    this.opponentCat = opponentCat;
    this.battleWon = false;
  }

  public Cat getPlayerCat() {
    return playerCat;
  }

  public Cat getOpponentCat() {
    return opponentCat;
  }

  public boolean isBattleWon() {
    return battleWon;
  }

  public void ability(Cat attacker, Cat target, String abilityName) {
    switch (abilityName) {
      case "Scratch" -> attack(target, 10); //TODO damage over time (bleed)
      case "Pounce" -> attack(target, 15);
      case "Hairball" -> attack(target, 8);
      case "Healing Purr" -> heal(attacker, 20);
      case "Night Claw" -> attack(target, 18);
      case "Flame Paw" -> attack(target, 20); //TODO damage over time (burn)
      case "Tail Whip" -> attack(target, 12);
      case "Zoomies" -> attack(target, 4); //TODO hits 2-3 times
      default -> throw new IllegalArgumentException(
          "Unknown ability: " + abilityName);
    }
  }
  public void heal(Cat target, int amount) {
    target.setHp(target.getHp() + amount);
  }

  private void attack(Cat target, int amount) {
    int newHp = target.getHp() - amount;

    if (newHp <= 0) {
      target.setHp(0);

      if (target == opponentCat) {
        battleWon = true;
      }
    } else {
      target.setHp(newHp);
    }
  }
}

