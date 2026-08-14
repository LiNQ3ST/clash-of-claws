package account;

import java.time.LocalDateTime;

/**
 * Represents a player account and the player's persisted game progress.
 */
public class Player {

    private Integer playerId;
    private String username;
    private String passwordHash;
    private int currencyBalance;
    private Integer activeCatId;
    private LocalDateTime createdAt;

    public Player(String username,  String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.currencyBalance = 100;
        this.activeCatId = null;
    }

    public Player(Integer playerId, String username, String passwordHash, int currencyBalance, Integer activeCatId, LocalDateTime createdAt) {
        this.playerId = playerId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.currencyBalance = currencyBalance;
        this.activeCatId = activeCatId;
        this.createdAt = createdAt;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getCurrencyBalance() {
        return currencyBalance;
    }

    public void setCurrencyBalance(int currencyBalance) {
        this.currencyBalance = currencyBalance;
    }

    public Integer getActiveCatId() {
        return activeCatId;
    }

    public void setActiveCatId(Integer activeCatId) {
        this.activeCatId = activeCatId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
