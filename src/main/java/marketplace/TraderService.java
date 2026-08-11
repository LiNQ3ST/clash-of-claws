package marketplace;

import creature.Cat;
import database.DatabaseManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Business logic for buying trader items and selling player-owned creatures.
 *
 * All database changes for one purchase or sale use the same JDBC transaction.
 * This keeps failed operations from partially changing coins, stock, inventory,
 * or creature ownership.
 */
public class TraderService {

    private final String jdbcUrl;

    /**
     * Normal application constructor. Uses the team's shared SQLite database.
     */
    public TraderService() {
        this.jdbcUrl = null;
    }

    /**
     * Test constructor. Pass an H2 JDBC URL from TraderServiceTest.
     */
    public TraderService(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            throw new IllegalArgumentException("JDBC URL cannot be blank.");
        }
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * Purchases a quantity of one trader item.
     *
     * Rules:
     * - quantity must be positive
     * - item must exist
     * - enough stock must exist
     * - player must exist
     * - player must have enough coins
     * - inventory quantity increases
     * - trader stock decreases
     * - player coins decrease
     */
    public PurchaseResult purchaseItem(
            int playerId,
            int itemId,
            int quantity
    ) throws SQLException {

        requirePositiveId(playerId, "Player ID");
        requirePositiveId(itemId, "Item ID");

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);

            try {
                PlayerSnapshot player = loadPlayer(connection, playerId);
                ItemSnapshot item = loadItem(connection, itemId);

                if (item.stockQuantity() < quantity) {
                    throw new IllegalStateException(
                            "Not enough stock. Available: " + item.stockQuantity()
                    );
                }

                int totalCost;
                try {
                    totalCost = Math.multiplyExact(item.price(), quantity);
                } catch (ArithmeticException exception) {
                    throw new IllegalArgumentException("Purchase total is too large.", exception);
                }

                if (player.currencyBalance() < totalCost) {
                    int missing = totalCost - player.currencyBalance();
                    throw new IllegalStateException(
                            "Insufficient coins. You need " + missing + " more coins."
                    );
                }

                Integer existingQuantity = findInventoryQuantity(
                        connection,
                        playerId,
                        itemId
                );

                int oldInventoryQuantity = existingQuantity == null
                        ? 0
                        : existingQuantity;

                int newInventoryQuantity;
                try {
                    newInventoryQuantity = Math.addExact(oldInventoryQuantity, quantity);
                } catch (ArithmeticException exception) {
                    throw new IllegalArgumentException("Inventory quantity is too large.", exception);
                }

                int newBalance = player.currencyBalance() - totalCost;
                int newStock = item.stockQuantity() - quantity;

                updatePlayerBalance(connection, playerId, newBalance);
                updateTraderStock(connection, itemId, newStock);
                saveInventoryQuantity(
                        connection,
                        playerId,
                        itemId,
                        existingQuantity,
                        newInventoryQuantity
                );

                connection.commit();

                return new PurchaseResult(
                        totalCost,
                        newBalance,
                        newStock,
                        newInventoryQuantity
                );

            } catch (SQLException | RuntimeException exception) {
                rollbackQuietly(connection);
                throw exception;
            }
        }
    }

    /**
     * Sells one player-owned creature to the trader.
     *
     * The current sale rule is intentionally simple for the project:
     * one coin per point of maximum HP.
     *
     * Rules:
     * - player must exist
     * - cat must belong to this player's database records
     * - cat must be marked as player-owned
     * - active cat cannot be sold
     * - successful sale deletes the cat and credits the player's balance
     */
    public SaleResult sellCreature(
            int playerId,
            int catId
    ) throws SQLException {

        requirePositiveId(playerId, "Player ID");
        requirePositiveId(catId, "Cat ID");

        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);

            try {
                PlayerSnapshot player = loadPlayer(connection, playerId);
                Cat cat = loadOwnedCatRecord(connection, playerId, catId);

                if (!cat.isPlayerCat()) {
                    throw new IllegalStateException(
                            "This creature is not owned by the player and cannot be sold."
                    );
                }

                if (player.activeCatId() != null
                        && player.activeCatId() == catId) {
                    throw new IllegalStateException(
                            "The active creature cannot be sold. Select another active creature first."
                    );
                }

                int saleValue = calculateSaleValue(cat);

                int newBalance;
                try {
                    newBalance = Math.addExact(player.currencyBalance(), saleValue);
                } catch (ArithmeticException exception) {
                    throw new IllegalStateException("Player coin balance is too large.", exception);
                }

                deleteCat(connection, playerId, catId);
                updatePlayerBalance(connection, playerId, newBalance);

                connection.commit();

                return new SaleResult(saleValue, newBalance);

            } catch (SQLException | RuntimeException exception) {
                rollbackQuietly(connection);
                throw exception;
            }
        }
    }

    /**
     * Final Issue #11 sale-price rule used by both the UI and the service.
     * Keeping the rule here prevents the controller and service from disagreeing.
     */
    public int calculateSaleValue(Cat cat) {
        if (cat == null) {
            throw new IllegalArgumentException("Cat cannot be null.");
        }
        return cat.getMaxHp();
    }

    /**
     * Useful for tests and future inventory displays.
     */
    public int getInventoryQuantity(int playerId, int itemId) throws SQLException {
        requirePositiveId(playerId, "Player ID");
        requirePositiveId(itemId, "Item ID");

        try (Connection connection = openConnection()) {
            Integer quantity = findInventoryQuantity(connection, playerId, itemId);
            return quantity == null ? 0 : quantity;
        }
    }

    private PlayerSnapshot loadPlayer(Connection connection, int playerId)
            throws SQLException {

        String sql = """
                SELECT currency_balance, active_cat_id
                FROM player
                WHERE player_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new IllegalStateException("Player was not found.");
                }

                int activeCatValue = resultSet.getInt("active_cat_id");
                Integer activeCatId = resultSet.wasNull()
                        ? null
                        : activeCatValue;

                return new PlayerSnapshot(
                        resultSet.getInt("currency_balance"),
                        activeCatId
                );
            }
        }
    }

    private ItemSnapshot loadItem(Connection connection, int itemId)
            throws SQLException {

        String sql = """
                SELECT price, stock_quantity
                FROM trader_items
                WHERE item_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, itemId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new IllegalStateException("Trader item was not found.");
                }

                return new ItemSnapshot(
                        resultSet.getInt("price"),
                        resultSet.getInt("stock_quantity")
                );
            }
        }
    }

    private Cat loadOwnedCatRecord(
            Connection connection,
            int playerId,
            int catId
    ) throws SQLException {

        String sql = """
                SELECT cat_data
                FROM cats
                WHERE cat_id = ?
                  AND player_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, catId);
            statement.setInt(2, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new IllegalStateException(
                            "You do not own the selected creature."
                    );
                }

                return Cat.fromStorageString(
                        catId,
                        resultSet.getString("cat_data")
                );
            }
        }
    }

    private Integer findInventoryQuantity(
            Connection connection,
            int playerId,
            int itemId
    ) throws SQLException {

        String sql = """
                SELECT quantity
                FROM player_inventory
                WHERE player_id = ?
                  AND item_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, playerId);
            statement.setInt(2, itemId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return resultSet.getInt("quantity");
            }
        }
    }

    private void saveInventoryQuantity(
            Connection connection,
            int playerId,
            int itemId,
            Integer existingQuantity,
            int newQuantity
    ) throws SQLException {

        if (existingQuantity == null) {
            String insertSql = """
                    INSERT INTO player_inventory (player_id, item_id, quantity)
                    VALUES (?, ?, ?)
                    """;

            try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                statement.setInt(1, playerId);
                statement.setInt(2, itemId);
                statement.setInt(3, newQuantity);

                if (statement.executeUpdate() != 1) {
                    throw new SQLException("Could not add the item to player inventory.");
                }
            }
            return;
        }

        String updateSql = """
                UPDATE player_inventory
                SET quantity = ?
                WHERE player_id = ?
                  AND item_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setInt(1, newQuantity);
            statement.setInt(2, playerId);
            statement.setInt(3, itemId);

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Could not update player inventory.");
            }
        }
    }

    private void updatePlayerBalance(
            Connection connection,
            int playerId,
            int newBalance
    ) throws SQLException {

        String sql = """
                UPDATE player
                SET currency_balance = ?
                WHERE player_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newBalance);
            statement.setInt(2, playerId);

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Could not update the player coin balance.");
            }
        }
    }

    private void updateTraderStock(
            Connection connection,
            int itemId,
            int newStock
    ) throws SQLException {

        String sql = """
                UPDATE trader_items
                SET stock_quantity = ?
                WHERE item_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newStock);
            statement.setInt(2, itemId);

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Could not update trader stock.");
            }
        }
    }

    private void deleteCat(
            Connection connection,
            int playerId,
            int catId
    ) throws SQLException {

        String sql = """
                DELETE FROM cats
                WHERE cat_id = ?
                  AND player_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, catId);
            statement.setInt(2, playerId);

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Could not remove the sold creature.");
            }
        }
    }

    private Connection openConnection() throws SQLException {
        if (jdbcUrl == null) {
            return DatabaseManager.getInstance().getConnection();
        }
        return DriverManager.getConnection(jdbcUrl);
    }

    private static void requirePositiveId(int id, String label) {
        if (id <= 0) {
            throw new IllegalArgumentException(label + " must be greater than zero.");
        }
    }

    private static void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // Preserve the original exception that caused the transaction to fail.
        }
    }

    private record PlayerSnapshot(int currencyBalance, Integer activeCatId) {
    }

    private record ItemSnapshot(int price, int stockQuantity) {
    }

    public record PurchaseResult(
            int totalCost,
            int newBalance,
            int remainingStock,
            int inventoryQuantity
    ) {
    }

    public record SaleResult(
            int saleValue,
            int newBalance
    ) {
    }
}
