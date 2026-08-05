/**
 * @author Nabiha Fatima
 * @version 0.1.0
 * @since 8/4/2026
 */
package adminarena;

public class Arena {

    private Integer arenaId;
    private String arenaName;
    private String townName;
    private Integer opponentCatId;
    private String difficulty;
    private int rewardAmount;
    private boolean active;

    public Arena() {
    }

    public Arena(
            Integer arenaId,
            String arenaName,
            String townName,
            Integer opponentCatId,
            String difficulty,
            int rewardAmount,
            boolean active
    ) {
        this.arenaId = arenaId;
        this.arenaName = arenaName;
        this.townName = townName;
        this.opponentCatId = opponentCatId;
        this.difficulty = difficulty;
        this.rewardAmount = rewardAmount;
        this.active = active;
    }

    public Integer getArenaId() {
        return arenaId;
    }

    public void setArenaId(Integer arenaId) {
        this.arenaId = arenaId;
    }

    public String getArenaName() {
        return arenaName;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public Integer getOpponentCatId() {
        return opponentCatId;
    }

    public void setOpponentCatId(Integer opponentCatId) {
        this.opponentCatId = opponentCatId;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(int rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}