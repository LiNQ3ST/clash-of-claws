package account;

import database.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDAOTest {

    private static final String USERNAME = "PlayerOne";
    private static final String PASSWORD_HASH = "hashed-password";

    @TempDir
    Path tempDirectory;

    private PlayerDAO playerDAO;
    private Player player;
    private String databaseUrl;

    @BeforeEach
    void setUp() throws SQLException {
        Path databasePath = tempDirectory.resolve("player-test.db");
        databaseUrl = "jdbc:sqlite:" + databasePath;

        DatabaseManager.getInstance().initializeDatabase(databaseUrl);

        playerDAO = new PlayerDAO(databaseUrl);
        player = new Player(USERNAME, PASSWORD_HASH);
    }

    @Test
    void create() throws SQLException {
        Player createdPlayer = playerDAO.create(player);

        assertNotNull(createdPlayer.getPlayerId());
        assertEquals(USERNAME, createdPlayer.getUsername());
        assertEquals(PASSWORD_HASH, createdPlayer.getPasswordHash());
        assertEquals(0, createdPlayer.getCurrencyBalance());
        assertEquals(0, createdPlayer.getExperience());
        assertNull(createdPlayer.getActiveCatId());
    }

    @Test
    void findById() throws SQLException {
        Player createdPlayer = playerDAO.create(player);

        Optional<Player> result = playerDAO.findById(createdPlayer.getPlayerId());

        assertTrue(result.isPresent());
        assertEquals(createdPlayer.getPlayerId(), result.get().getPlayerId());
        assertEquals(USERNAME, result.get().getUsername());
    }

    @Test
    void findByUsername() throws SQLException {
        Player createdPlayer = playerDAO.create(player);

        Optional<Player> result = playerDAO.findByUsername(USERNAME);

        assertTrue(result.isPresent());
        assertEquals(createdPlayer.getPlayerId(), result.get().getPlayerId());
        assertEquals(USERNAME, result.get().getUsername());
    }

    @Test
    void update() throws SQLException {
        Player createdPlayer = playerDAO.create(player);

        createdPlayer.setUsername("UpdatedUsername");
        createdPlayer.setPasswordHash("updated-hash");
        createdPlayer.setCurrencyBalance(250);
        createdPlayer.setExperience(75);
        createdPlayer.setActiveCatId(12);

        boolean updated = playerDAO.update(createdPlayer);

        assertTrue(updated);

        Player storedPlayer = playerDAO.findById(createdPlayer.getPlayerId()).orElseThrow();

        assertEquals("UpdatedUsername", storedPlayer.getUsername());
        assertEquals("updated-hash", storedPlayer.getPasswordHash());
        assertEquals(250, storedPlayer.getCurrencyBalance());
        assertEquals(75, storedPlayer.getExperience());
        assertEquals(12, storedPlayer.getActiveCatId());
    }

    @Test
    void delete() throws SQLException {
        Player createdPlayer = playerDAO.create(player);

        boolean deleted = playerDAO.delete(createdPlayer.getPlayerId());

        assertTrue(deleted);
        assertTrue(playerDAO.findById(createdPlayer.getPlayerId()).isEmpty());
    }

    @Test
    void duplicateUsername() throws SQLException {
        playerDAO.create(player);

        Player duplicatePlayer = new Player(USERNAME, "different-hash");

        assertThrows(SQLException.class, () -> playerDAO.create(duplicatePlayer));
    }

    @Test
    void nullUsername() {
        Player invalidPlayer = new Player(null, PASSWORD_HASH);

        assertThrows(SQLException.class, () -> playerDAO.create(invalidPlayer));
    }

    @Test
    void persistence() throws SQLException {
        Player createdPlayer = playerDAO.create(player);

        PlayerDAO reconnectedDAO = new PlayerDAO(databaseUrl);

        Optional<Player> result = reconnectedDAO.findById(createdPlayer.getPlayerId());

        assertTrue(result.isPresent());
        assertEquals(USERNAME, result.get().getUsername());
    }
}