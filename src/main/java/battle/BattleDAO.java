package battle;

import database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Uses CRUD to turn Battle objects into database rows,
 * and database rows back into Battle objects
 *
 * @author Quinton Nisonger
 * @version 0.1.0
 * @since 8/2/2026
 */
public class BattleDAO {

  public void insert(Battle battle) {
    String sql = """
        INSERT INTO battle (
            player_id,
            battle_type,
            arena_id,
            status
        )
        VALUES (?, ?, ?, ?)
        """;

    try (
        Connection connection =
            DatabaseManager.getInstance().getConnection();
        PreparedStatement statement =
            connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS
            )

    ) {
      statement.setInt(1, battle.getPlayerId());
      statement.setString(2, battle.getBattleType());

      if (battle.getArenaId() == null) {
        statement.setNull(3, java.sql.Types.INTEGER);
      } else {
        statement.setInt(3, battle.getArenaId());
      }

      statement.setString(4, battle.getStatus());

      int rowsInserted = statement.executeUpdate();

      if (rowsInserted == 0) {
        throw new SQLException("Inserting battle failed; no row was added.");
      }

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          battle.setBattleId(generatedKeys.getInt(1));
        } else {
          throw new SQLException(
              "Inserting battle failed; no generated ID was returned."
          );
        }
      }

    } catch (SQLException e) {
      throw new IllegalStateException(
          "Could not insert battle.",
          e
      );
    }
  }

  public Battle findById(int battleId) {
    String sql = """
        SELECT *
        FROM battle
        WHERE battle_id = ?
        """;

    try (
        Connection connection = DatabaseManager.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, battleId);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          int playerId = resultSet.getInt("player_id");
          String battleType = resultSet.getString("battle_type");
          int storedArenaId = resultSet.getInt("arena_id");
          Integer arenaId = resultSet.wasNull() ? null : storedArenaId;
          String status = resultSet.getString("status");

          Battle battle = new Battle(
              playerId,
              battleType,
              arenaId,
              status
          );

          battle.setBattleId(
              resultSet.getInt("battle_id")
          );

          return battle;
        }
      }

      return null;

    } catch (SQLException e) {
      throw new IllegalStateException(
          "Could not find battle by ID.",
          e
      );
    }
  }

  public boolean update(Battle battle) {
    String sql = """
        UPDATE battle
        SET player_id = ?,
            battle_type = ?,
            arena_id = ?,
            status = ?
        WHERE battle_id = ?
        """;

    try (
        Connection connection = DatabaseManager.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, battle.getPlayerId());
      statement.setString(2, battle.getBattleType());

      if (battle.getArenaId() == null) {
        statement.setNull(3, java.sql.Types.INTEGER);
      } else {
        statement.setInt(3, battle.getArenaId());
      }

      statement.setString(4, battle.getStatus());
      statement.setInt(5, battle.getBattleId());

      int rowsUpdated = statement.executeUpdate();

      return rowsUpdated > 0;

    } catch (SQLException e) {
      throw new IllegalStateException(
          "Could not update battle.",
          e
      );
    }
  }

  public boolean delete(int battleId) {
    String sql = """
        DELETE FROM battle
        WHERE battle_id = ?
        """;

    try (
        Connection connection = DatabaseManager.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, battleId);

      int rowsDeleted = statement.executeUpdate();

      return rowsDeleted > 0;

    } catch (SQLException e) {
      throw new IllegalStateException(
          "Could not delete battle.",
          e
      );
    }
  }
}
