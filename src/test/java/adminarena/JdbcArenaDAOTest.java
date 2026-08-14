/**
 * @author Nabiha Fatima
 * @version 0.1.0
 * @since 8/4/2026
 */
package adminarena;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcArenaDAOTest {

    @TempDir
    Path tempDirectory;

    private ArenaDAO arenaDAO;

    @BeforeEach
    void setUp() throws SQLException {
        SQLiteDataSource dataSource = new SQLiteDataSource();

        Path databaseFile = tempDirectory.resolve("arena-test.db");

        dataSource.setUrl(
                "jdbc:sqlite:" + databaseFile.toAbsolutePath()
        );

        recreateTable(dataSource);

        arenaDAO = new JdbcArenaDAO(dataSource);
    }

    @Test
    void arenaCrudLifecycleWorks() throws SQLException {
        Arena arena = new Arena(
                null,
                "Claw Pit",
                "Whiskerton",
                "MEDIUM",
                300,
                true
        );

        Arena inserted = arenaDAO.insert(arena);

        assertNotNull(inserted.getArenaId());

        Arena found = arenaDAO
                .findById(inserted.getArenaId())
                .orElseThrow();

        assertEquals("Claw Pit", found.getArenaName());
        assertEquals(300, found.getRewardAmount());

        found.setRewardAmount(500);
        found.setActive(false);

        assertTrue(arenaDAO.update(found));

        Arena updated = arenaDAO
                .findById(found.getArenaId())
                .orElseThrow();

        assertEquals(500, updated.getRewardAmount());
        assertFalse(updated.isActive());

        assertTrue(arenaDAO.delete(updated.getArenaId()));

        assertTrue(
                arenaDAO.findById(updated.getArenaId()).isEmpty()
        );
    }

    private void recreateTable(DataSource dataSource)
            throws SQLException {

        String dropSql = """
                DROP TABLE IF EXISTS arenas
                """;

        String createSql = """
                CREATE TABLE arenas (
                    arena_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    arena_name TEXT NOT NULL,
                    town_name TEXT NOT NULL,
                    difficulty TEXT NOT NULL,
                    reward_amount INTEGER NOT NULL
                        CHECK (reward_amount >= 0),
                    active INTEGER NOT NULL DEFAULT 1
                )
                """;

        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.execute(dropSql);
            statement.execute(createSql);
        }
    }
}