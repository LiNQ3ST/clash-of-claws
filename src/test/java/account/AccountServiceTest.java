package account;

import database.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private static final String USERNAME = "playerone";
    private static final String PASSWORD = "secret1";

    @TempDir
    Path tempDirectory;

    private PlayerDAO playerDAO;
    private AccountService accountService;

    @BeforeEach
    void setUp() throws SQLException {
        Path databasePath = tempDirectory.resolve("account-service-test.db");
        String databaseUrl = "jdbc:sqlite:" + databasePath;

        DatabaseManager.getInstance().initializeDatabase(databaseUrl);

        playerDAO = new PlayerDAO(databaseUrl);
        accountService = new AccountService(playerDAO);
    }

    @Test
    void register() throws SQLException {
        accountService.register(USERNAME, PASSWORD, PASSWORD);

        Optional<Player> result = playerDAO.findByUsername(USERNAME);

        assertTrue(result.isPresent());
        assertEquals(USERNAME, result.get().getUsername());
        assertNotEquals(PASSWORD, result.get().getPasswordHash());
    }

    @Test
    void blankUsername() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.register("   ", PASSWORD, PASSWORD)
        );

        assertEquals("Username is required.", exception.getMessage());
    }

    @Test
    void invalidUsername() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.register(
                        "player-one",
                        PASSWORD,
                        PASSWORD
                )
        );

        assertEquals(
                "Username may only contain letters, numbers, and underscores.",
                exception.getMessage()
        );
    }

    @Test
    void shortUsername() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.register("ab", PASSWORD, PASSWORD)
        );

        assertEquals(
                "Username must be between 3 and 20 characters.",
                exception.getMessage()
        );
    }

    @Test
    void longUsername() {
        String username = "abcdefghijklmnopqrstu";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.register(username, PASSWORD, PASSWORD)
        );

        assertEquals(
                "Username must be between 3 and 20 characters.",
                exception.getMessage()
        );
    }

    @Test
    void blankPassword() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.register(USERNAME, "   ", "   ")
        );

        assertEquals("Password is required.", exception.getMessage());
    }

    @Test
    void shortPassword() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.register(USERNAME, "12345", "12345")
        );

        assertEquals(
                "Password must be between 6 and 64 characters.",
                exception.getMessage()
        );
    }

    @Test
    void longPassword() {
        String password = "a".repeat(65);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.register(USERNAME, password, password)
        );

        assertEquals(
                "Password must be between 6 and 64 characters.",
                exception.getMessage()
        );
    }

    @Test
    void mismatchedPasswords() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.register(
                        USERNAME,
                        PASSWORD,
                        "different"
                )
        );

        assertEquals("Passwords do not match.", exception.getMessage());
    }

    @Test
    void duplicateUsername() throws SQLException {
        accountService.register(USERNAME, PASSWORD, PASSWORD);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.register(
                        USERNAME,
                        PASSWORD,
                        PASSWORD
                )
        );

        assertEquals(
                "That username is already taken.",
                exception.getMessage()
        );
    }

    @Test
    void usernameNormalization() throws SQLException {
        accountService.register(
                "  Player_One  ",
                PASSWORD,
                PASSWORD
        );

        Optional<Player> result =
                playerDAO.findByUsername("player_one");

        assertTrue(result.isPresent());
        assertEquals(
                "player_one",
                result.get().getUsername()
        );
    }

    @Test
    void authenticate() throws SQLException {
        accountService.register(USERNAME, PASSWORD, PASSWORD);

        Player authenticatedPlayer =
                accountService.authenticate(USERNAME, PASSWORD);

        assertNotNull(authenticatedPlayer);
        assertEquals(USERNAME, authenticatedPlayer.getUsername());
    }

    @Test
    void nonexistentUsername() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.authenticate(
                        "missingplayer",
                        PASSWORD
                )
        );

        assertEquals(
                "Invalid username or password. Please try again.",
                exception.getMessage()
        );
    }

    @Test
    void incorrectPassword() throws SQLException {
        accountService.register(USERNAME, PASSWORD, PASSWORD);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.authenticate(
                        USERNAME,
                        "wrongpassword"
                )
        );

        assertEquals(
                "Invalid username or password. Please try again.",
                exception.getMessage()
        );
    }

    @Test
    void blankLogin() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.authenticate("   ", "   ")
        );

        assertEquals(
                "Please enter credentials to continue.",
                exception.getMessage()
        );
    }

    @Test
    void currentPlayer() throws SQLException {
        accountService.register(USERNAME, PASSWORD, PASSWORD);

        assertTrue(accountService.getCurrentPlayer().isEmpty());

        Player authenticatedPlayer =
                accountService.authenticate(USERNAME, PASSWORD);

        Optional<Player> currentPlayer =
                accountService.getCurrentPlayer();

        assertTrue(currentPlayer.isPresent());
        assertEquals(
                authenticatedPlayer.getPlayerId(),
                currentPlayer.get().getPlayerId()
        );
    }

    @Test
    void logout() throws SQLException {
        accountService.register(USERNAME, PASSWORD, PASSWORD);
        accountService.authenticate(USERNAME, PASSWORD);

        assertTrue(accountService.getCurrentPlayer().isPresent());

        accountService.logout();

        assertTrue(accountService.getCurrentPlayer().isEmpty());
    }
}