package battle;

import java.util.Objects;

/**
 * Represents a battle record stored in the database.
 *
 * @author Quinton Nisonger
 * @version 0.1.0
 * @since 8/2/2026
 */
public class Battle {
  private int battleId;
  private int playerId;
  private String battleType;
  private Integer arenaId;  // Null for wild battles
  private String status;

  // Constructor for a new battle
  public Battle(
      int playerId,
      String battleType,
      Integer arenaId,
      String status
  ) {
    this.playerId = playerId;
    this.battleType = battleType;
    this.arenaId = arenaId;
    this.status = status;
  }

  public int getBattleId() {
    return battleId;
  }

  public void setBattleId(int battleId) {
    this.battleId = battleId;
  }

  public int getPlayerId() {
    return playerId;
  }

  public void setPlayerId(int playerId) {
    this.playerId = playerId;
  }

  public String getBattleType() {
    return battleType;
  }

  public void setBattleType(String battleType) {
    this.battleType = battleType;
  }

  public Integer getArenaId() {
    return arenaId;
  }

  public void setArenaId(Integer arenaId) {
    this.arenaId = arenaId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "Battle{" +
        "battleId=" + battleId +
        ", playerId=" + playerId +
        ", battleType='" + battleType + '\'' +
        ", arenaId=" + arenaId +
        ", status='" + status + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Battle battle)) {
      return false;
    }
    return getBattleId() == battle.getBattleId()
        && getPlayerId() == battle.getPlayerId()
        && Objects.equals(getBattleType(), battle.getBattleType())
        && Objects.equals(getArenaId(), battle.getArenaId())
        && Objects.equals(getStatus(), battle.getStatus());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getBattleId(),
        getPlayerId(),
        getBattleType(),
        getArenaId(),
        getStatus()
    );
  }
}

