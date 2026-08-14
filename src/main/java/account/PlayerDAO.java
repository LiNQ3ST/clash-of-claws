package account;

import database.DatabaseManager;
import java.sql.DriverManager;

import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Provides database operations for player accounts.
 */
public class PlayerDAO {

    private final String databaseUrl;

    public PlayerDAO() {
        this.databaseUrl = null;
    }

    public PlayerDAO(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public Player create(Player player) throws SQLException {
        String sql = """
                INSERT INTO player (
                username,
                password_hash,
                currency_balance,
                active_cat_id
                )
                VALUES (?, ?, ?, ?)
                """;
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            statement.setString(1, player.getUsername());
            statement.setString(2, player.getPasswordHash());
            statement.setInt(3, player.getCurrencyBalance());

            if (player.getActiveCatId() == null) {
                statement.setNull(4, java.sql.Types.INTEGER);
            } else {
                statement.setInt(4, player.getActiveCatId());
            }

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Creating player failed.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    player.setPlayerId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException(
                            "Creating player failed; no ID was generated."
                    );
                }
            }
        }
        return player;
    }

    public Optional<Player> findById(Integer playerId) throws SQLException {
        String sql = """
                SELECT
                    player_id,
                    username,
                    password_hash,
                    currency_balance,
                    active_cat_id,
                    created_at
                FROM player
                WHERE player_id = ?
        """;
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public Optional<Player> findByUsername(String username) throws SQLException {
        String sql = """
                SELECT
                    player_id,
                    username,
                    password_hash,
                    currency_balance,
                    active_cat_id,
                    created_at
                FROM player
                WHERE username = ?
        """;
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public boolean update(Player player) throws SQLException {
        if (player.getPlayerId() == null) {
            throw new IllegalArgumentException(
                    "Cannot update a player without an ID."
            );
        }

        String sql = """
            UPDATE player
            SET username = ?,
                password_hash = ?,
                currency_balance = ?,
                active_cat_id = ?
            WHERE player_id = ?
            """;

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, player.getUsername());
            statement.setString(2, player.getPasswordHash());
            statement.setInt(3, player.getCurrencyBalance());

            if (player.getActiveCatId() == null) {
                statement.setNull(4, java.sql.Types.INTEGER);
            } else {
                statement.setInt(4, player.getActiveCatId());
            }

            statement.setInt(5, player.getPlayerId());

            return statement.executeUpdate() == 1;
        }
    }

    public boolean deleteAccount(int playerId) throws SQLException {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try {
                deletePlayerData(
                        connection,
                        "DELETE FROM player_inventory WHERE player_id = ?",
                        playerId
                );

                deletePlayerData(
                        connection,
                        "DELETE FROM cats WHERE player_id = ?",
                        playerId
                );

                deletePlayerData(
                        connection,
                        "DELETE FROM battle WHERE player_id = ?",
                        playerId
                );

                String sql = """
                    DELETE FROM player
                    WHERE player_id = ?
                    """;

                boolean deleted;

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setInt(1, playerId);
                    deleted = statement.executeUpdate() == 1;
                }

                if (!deleted) {
                    connection.rollback();
                    return false;
                }

                connection.commit();
                return true;

            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    private void deletePlayerData(
            Connection connection,
            String sql,
            int playerId
    ) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, playerId);
            statement.executeUpdate();
        }
    }

    private Player mapRow(ResultSet resultSet) throws SQLException {
        int storedActiveCatId = resultSet.getInt("active_cat_id");

        Integer activeCatId = resultSet.wasNull()
                ? null
                : storedActiveCatId;

        String createdAtText = resultSet.getString("created_at");

        LocalDateTime createdAt = createdAtText == null
                ? null
                : LocalDateTime.parse(
                createdAtText.replace(" ", "T")
        );

        return new Player(
                resultSet.getInt("player_id"),
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                resultSet.getInt("currency_balance"),
                activeCatId,
                createdAt
        );
    }

    private Connection getConnection() throws SQLException {
        if (databaseUrl == null) {
            return DatabaseManager.getInstance().getConnection();
        }

        return DriverManager.getConnection(databaseUrl);
    }
}