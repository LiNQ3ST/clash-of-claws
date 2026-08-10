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
  private boolean battleWon;
  private boolean battleLost;

  public BattleEngine(Cat playerCat, Cat opponentCat) {
    this.playerCat = playerCat;
    this.opponentCat = opponentCat;
    this.battleWon = false;
    this.battleLost = false;
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

  public boolean isBattleLost() {
    return battleLost;
  }

  protected void ability(Cat attacker, Cat target, String abilityName) {

    if (battleWon || battleLost) {
      throw new IllegalStateException("Battle has already ended.");
    }

    if (attacker.getHp() <= 0) {
      throw new IllegalStateException("A defeated cat cannot act.");
    }

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
      } else if(target == playerCat) {
        battleLost = true;
      }
    } else {
      target.setHp(newHp);
    }
  }

  public String opponentTurn() {
    if (battleWon || battleLost) {
      throw new IllegalStateException("Battle has already ended.");
    }

    ArrayList<String> abilities = opponentCat.getAbilities();

    if (abilities.isEmpty()) {
      throw new IllegalArgumentException("No abilities found");
    }

    String abilityName = abilities.getFirst();
    ability(opponentCat, playerCat, abilityName);

    return abilityName;
  }

  public String playerTurn(String abilityName) {
    if (battleWon || battleLost) {
      throw new IllegalStateException("Battle has already ended.");
    }

    if (!playerCat.getAbilities().contains(abilityName)) {
      throw new IllegalArgumentException(
          "Player cat does not have ability: " + abilityName
      );
    }

    ability(playerCat, opponentCat, abilityName);

    return abilityName;
  }


}

