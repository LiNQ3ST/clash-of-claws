package battle;

import static org.junit.jupiter.api.Assertions.*;

import database.DatabaseManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests CRUD operations performed by BattleDAO.
 *
 * @author Quinton Nisonger
 * @version 0.1.0
 * @since 8/3/2026
 */
class BattleDAOTest {

  private BattleDAO battleDAO;

  @BeforeEach
  void setUp() throws SQLException {
    DatabaseManager.getInstance()
        .initializeDatabase();

    battleDAO = new BattleDAO();

    clearBattleTable();
  }

  @AfterEach
  void tearDown() throws SQLException {
    clearBattleTable();
  }

  @Test
  void insertWildBattleAssignsIdAndPersistsFields() {
    Battle battle =
        new Battle(
            1,
            BattleType.WILD.name(),
            null,
            BattleResult.IN_PROGRESS.name()
        );

    battleDAO.insert(battle);

    assertTrue(
        battle.getBattleId() > 0,
        "The database should assign a positive battle ID."
    );

    Battle savedBattle =
        battleDAO.findById(
            battle.getBattleId()
        );

    assertNotNull(savedBattle);
    assertEquals(1, savedBattle.getPlayerId());
    assertEquals(
        BattleType.WILD.name(),
        savedBattle.getBattleType()
    );
    assertNull(savedBattle.getArenaId());
    assertEquals(
        BattleResult.IN_PROGRESS.name(),
        savedBattle.getStatus()
    );
  }

  @Test
  void findByIdReturnsStoredArenaBattle() {
    Battle battle =
        new Battle(
            2,
            BattleType.ARENA.name(),
            10,
            BattleResult.IN_PROGRESS.name()
        );

    battleDAO.insert(battle);

    Battle foundBattle =
        battleDAO.findById(
            battle.getBattleId()
        );

    assertNotNull(foundBattle);
    assertEquals(
        battle,
        foundBattle
    );
  }

  @Test
  void findByIdReturnsNullForMissingBattle() {
    assertNull(
        battleDAO.findById(
            Integer.MAX_VALUE
        )
    );
  }

  @Test
  void updatePersistsVictoryStatus() {
    Battle battle =
        new Battle(
            3,
            BattleType.WILD.name(),
            null,
            BattleResult.IN_PROGRESS.name()
        );

    battleDAO.insert(battle);

    battle.setStatus(
        BattleResult.VICTORY.name()
    );

    boolean updated =
        battleDAO.update(battle);

    Battle savedBattle =
        battleDAO.findById(
            battle.getBattleId()
        );

    assertTrue(updated);
    assertNotNull(savedBattle);
    assertEquals(
        BattleResult.VICTORY.name(),
        savedBattle.getStatus()
    );
  }

  @Test
  void updatePersistsEscapedStatus() {
    Battle battle =
        new Battle(
            4,
            BattleType.WILD.name(),
            null,
            BattleResult.IN_PROGRESS.name()
        );

    battleDAO.insert(battle);

    battle.setStatus(
        BattleResult.ESCAPED.name()
    );

    assertTrue(
        battleDAO.update(battle)
    );

    Battle savedBattle =
        battleDAO.findById(
            battle.getBattleId()
        );

    assertNotNull(savedBattle);
    assertEquals(
        BattleResult.ESCAPED.name(),
        savedBattle.getStatus()
    );
  }

  @Test
  void updateReturnsFalseForMissingBattle() {
    Battle battle =
        new Battle(
            5,
            BattleType.WILD.name(),
            null,
            BattleResult.DEFEAT.name()
        );

    battle.setBattleId(
        Integer.MAX_VALUE
    );

    assertFalse(
        battleDAO.update(battle)
    );
  }

  @Test
  void deleteRemovesBattle() {
    Battle battle =
        new Battle(
            6,
            BattleType.WILD.name(),
            null,
            BattleResult.IN_PROGRESS.name()
        );

    battleDAO.insert(battle);

    int battleId =
        battle.getBattleId();

    assertTrue(
        battleDAO.delete(battleId)
    );

    assertNull(
        battleDAO.findById(battleId)
    );
  }

  @Test
  void deletingMissingBattleReturnsFalse() {
    assertFalse(
        battleDAO.delete(
            Integer.MAX_VALUE
        )
    );
  }

  private void clearBattleTable()
      throws SQLException {

    try (
        Connection connection =
            DatabaseManager.getInstance()
                .getConnection();

        Statement statement =
            connection.createStatement()
    ) {
      statement.executeUpdate(
          "DELETE FROM battle"
      );
    }
  }
}