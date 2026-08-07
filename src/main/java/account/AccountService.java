package account;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Handles player registration and authentication.
 *
 * @author Sahtra Green
 * @version 0.1.0
 * @since 8/7/2026
 */

public class AccountService {

    private static final AccountService INSTANCE = new AccountService(new PlayerDAO());

    private final PlayerDAO playerDAO;
    private Player currentPlayer;

    public AccountService(PlayerDAO playerDAO) {
        if (playerDAO == null) {
            throw new IllegalArgumentException("PlayerDAO cannot be null.");
        }

        this.playerDAO = playerDAO;
    }

    public static AccountService getInstance() {
        return INSTANCE;
    }

    public void register(String username, String password, String confirmation)
            throws SQLException {

        String cleanUsername = normalizeUsername(username);

        validateRegistration(cleanUsername, password, confirmation);

        if (playerDAO.findByUsername(cleanUsername).isPresent()) {
            throw new IllegalArgumentException("That username is already taken.");
        }

        String passwordHash = PasswordHasher.hash(password);

        Player player = new Player(cleanUsername, passwordHash);

        playerDAO.create(player);
    }

    public Player authenticate(String username, String password) throws SQLException {

        String cleanUsername = normalizeUsername(username);

        validateLogin(cleanUsername, password);

        Player player = playerDAO.findByUsername(cleanUsername).orElseThrow(
                () -> new IllegalArgumentException(
                        "Invalid username or password. Please try again."
                )
        );

        if (!PasswordHasher.matches(password, player.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "Invalid username or password. Please try again."
            );
        }

        currentPlayer = player;

        return currentPlayer;
    }

    public Optional<Player> getCurrentPlayer() {
        return Optional.ofNullable(currentPlayer);
    }

    public void logout() {
        currentPlayer = null;
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    private void validateRegistration(String username, String password, String confirmation) {
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }

        if (!username.matches("[a-z0-9_]+")) {
            throw new IllegalArgumentException(
                    "Username may only contain letters, numbers, and underscores."
            );
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new IllegalArgumentException(
                    "Username must be between 3 and 20 characters."
            );
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }

        if (password.length() < 6 || password.length() > 64) {
            throw new IllegalArgumentException(
                    "Password must be between 6 and 64 characters."
            );
        }

        if (!password.equals(confirmation)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
    }

    private void validateLogin(String username, String password) {
        if (username.isBlank() || password == null || password.isBlank()) {

            throw new IllegalArgumentException("Please enter credentials to continue.");
        }
    }
}