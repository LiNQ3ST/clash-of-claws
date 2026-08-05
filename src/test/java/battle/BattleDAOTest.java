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
    DatabaseManager.getInstance().initializeDatabase();
    battleDAO = new BattleDAO();

    clearBattleTable();
  }

  @AfterEach
  void tearDown() throws SQLException {
    clearBattleTable();
  }

  @Test
  void insert() {
    Battle battle = new Battle(
        1,
        "WILD",
        null,
        "IN_PROGRESS"
    );

    battleDAO.insert(battle);

    assertTrue(
        battle.getBattleId() > 0,
        "The database should assign a positive battle ID."
    );

    Battle savedBattle =
        battleDAO.findById(battle.getBattleId());

    assertNotNull(savedBattle);
    assertEquals(1, savedBattle.getPlayerId());
    assertEquals("WILD", savedBattle.getBattleType());
    assertNull(savedBattle.getArenaId());
    assertEquals("IN_PROGRESS", savedBattle.getStatus());
  }

  @Test
  void findById() {
    Battle battle = new Battle(
        2,
        "ARENA",
        10,
        "IN_PROGRESS"
    );

    battleDAO.insert(battle);

    Battle foundBattle =
        battleDAO.findById(battle.getBattleId());

    assertNotNull(foundBattle);
    assertEquals(battle, foundBattle);

    assertNull(
        battleDAO.findById(Integer.MAX_VALUE),
        "A nonexistent battle ID should return null."
    );
  }

  @Test
  void update() {
    Battle battle = new Battle(
        3,
        "WILD",
        null,
        "IN_PROGRESS"
    );

    battleDAO.insert(battle);

    battle.setStatus("WON");

    boolean wasUpdated = battleDAO.update(battle);
    Battle updatedBattle =
        battleDAO.findById(battle.getBattleId());

    assertTrue(wasUpdated);
    assertNotNull(updatedBattle);
    assertEquals("WON", updatedBattle.getStatus());
  }

  @Test
  void delete() {
    Battle battle = new Battle(
        4,
        "WILD",
        null,
        "IN_PROGRESS"
    );

    battleDAO.insert(battle);

    int battleId = battle.getBattleId();

    boolean wasDeleted = battleDAO.delete(battleId);

    assertTrue(wasDeleted);
    assertNull(battleDAO.findById(battleId));
    assertFalse(
        battleDAO.delete(battleId),
        "Deleting the same battle twice should return false."
    );
  }

  private void clearBattleTable() throws SQLException {
    try (
        Connection connection =
            DatabaseManager.getInstance().getConnection();
        Statement statement = connection.createStatement()
    ) {
      statement.executeUpdate("DELETE FROM battle");
    }
  }
}