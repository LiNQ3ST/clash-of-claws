package marketplace;

import creature.Cat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraderServiceTest {

    private String jdbcUrl;
    private TraderService traderService;

    @BeforeEach
    void setUp() throws SQLException {
        jdbcUrl = "jdbc:h2:mem:trader_service_"
                + UUID.randomUUID()
                + ";DB_CLOSE_DELAY=-1";

        createTestSchema();
        traderService = new TraderService(jdbcUrl);
    }

    @Test
    void purchaseItemUpdatesCoinsInventoryAndStock() throws SQLException {
        int playerId = insertPlayer("buyer", 200);
        int itemId = insertTraderItem("Small Potion", "HEALING", 25, 10);

        TraderService.PurchaseResult result = traderService.purchaseItem(
                playerId,
                itemId,
                2
        );

        assertEquals(50, result.totalCost());
        assertEquals(150, result.newBalance());
        assertEquals(8, result.remainingStock());
        assertEquals(2, result.inventoryQuantity());

        assertEquals(150, readPlayerBalance(playerId));
        assertEquals(8, readItemStock(itemId));
        assertEquals(2, traderService.getInventoryQuantity(playerId, itemId));
    }

    @Test
    void purchasingSameItemAgainIncreasesInventoryQuantity() throws SQLException {
        int playerId = insertPlayer("buyer", 300);
        int itemId = insertTraderItem("Small Potion", "HEALING", 25, 10);

        traderService.purchaseItem(playerId, itemId, 2);
        traderService.purchaseItem(playerId, itemId, 3);

        assertEquals(5, traderService.getInventoryQuantity(playerId, itemId));
        assertEquals(5, readItemStock(itemId));
        assertEquals(175, readPlayerBalance(playerId));
    }

    @Test
    void insufficientCoinsDoesNotChangeData() throws SQLException {
        int playerId = insertPlayer("poor_player", 20);
        int itemId = insertTraderItem("Small Potion", "HEALING", 25, 10);

        assertThrows(
                IllegalStateException.class,
                () -> traderService.purchaseItem(playerId, itemId, 1)
        );

        assertEquals(20, readPlayerBalance(playerId));
        assertEquals(10, readItemStock(itemId));
        assertEquals(0, traderService.getInventoryQuantity(playerId, itemId));
    }

    @Test
    void cannotBuyMoreThanAvailableStock() throws SQLException {
        int playerId = insertPlayer("buyer", 500);
        int itemId = insertTraderItem("Strong Catching Item", "CATCHING", 90, 2);

        assertThrows(
                IllegalStateException.class,
                () -> traderService.purchaseItem(playerId, itemId, 3)
        );

        assertEquals(500, readPlayerBalance(playerId));
        assertEquals(2, readItemStock(itemId));
        assertEquals(0, traderService.getInventoryQuantity(playerId, itemId));
    }

    @Test
    void zeroAndNegativeQuantitiesAreRejected() throws SQLException {
        int playerId = insertPlayer("buyer", 500);
        int itemId = insertTraderItem("Potion", "HEALING", 20, 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> traderService.purchaseItem(playerId, itemId, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> traderService.purchaseItem(playerId, itemId, -1)
        );

        assertEquals(500, readPlayerBalance(playerId));
        assertEquals(10, readItemStock(itemId));
    }

    @Test
    void ownedCreatureCanBeSold() throws SQLException {
        int playerId = insertPlayer("seller", 100);
        int catId = insertCat(playerId, true, false, 80);

        TraderService.SaleResult result = traderService.sellCreature(
                playerId,
                catId
        );

        assertEquals(80, result.saleValue());
        assertEquals(180, result.newBalance());
        assertEquals(180, readPlayerBalance(playerId));
        assertFalse(catExists(catId));
    }

    @Test
    void playerCannotSellCreatureOwnedByAnotherPlayer() throws SQLException {
        int playerOne = insertPlayer("player_one", 100);
        int playerTwo = insertPlayer("player_two", 200);
        int playerTwoCat = insertCat(playerTwo, true, false, 70);

        assertThrows(
                IllegalStateException.class,
                () -> traderService.sellCreature(playerOne, playerTwoCat)
        );

        assertEquals(100, readPlayerBalance(playerOne));
        assertEquals(200, readPlayerBalance(playerTwo));
        assertTrue(catExists(playerTwoCat));
    }

    @Test
    void opponentCatCannotBeSold() throws SQLException {
        int playerId = insertPlayer("player", 100);
        int opponentCat = insertCat(playerId, false, false, 90);

        assertThrows(
                IllegalStateException.class,
                () -> traderService.sellCreature(playerId, opponentCat)
        );

        assertEquals(100, readPlayerBalance(playerId));
        assertTrue(catExists(opponentCat));
    }

    @Test
    void activeCreatureCannotBeSoldAndDataDoesNotChange() throws SQLException {
        int playerId = insertPlayer("player", 100);
        int activeCatId = insertCat(playerId, true, true, 120);
        setActiveCat(playerId, activeCatId);

        assertThrows(
                IllegalStateException.class,
                () -> traderService.sellCreature(playerId, activeCatId)
        );

        assertEquals(100, readPlayerBalance(playerId));
        assertTrue(catExists(activeCatId));
    }

    @Test
    void saleValueComesFromTraderService() {
        Cat cat = new Cat(
                "Whiskers",
                "Tabby",
                95,
                new ArrayList<>(List.of("SCRATCH")),
                true,
                false
        );

        assertEquals(95, traderService.calculateSaleValue(cat));
    }

    private void createTestSchema() throws SQLException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             Statement statement = connection.createStatement()) {

            statement.execute("""
                    CREATE TABLE player (
                        player_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                        username VARCHAR(100) NOT NULL UNIQUE,
                        password_hash VARCHAR(255) NOT NULL,
                        currency_balance INTEGER NOT NULL DEFAULT 0,
                        experience INTEGER NOT NULL DEFAULT 0,
                        active_cat_id INTEGER,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);

            statement.execute("""
                    CREATE TABLE trader_items (
                        item_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                        item_name VARCHAR(100) NOT NULL,
                        item_type VARCHAR(50) NOT NULL,
                        description VARCHAR(255) NOT NULL,
                        price INTEGER NOT NULL,
                        stock_quantity INTEGER NOT NULL
                    )
                    """);

            statement.execute("""
                    CREATE TABLE cats (
                        cat_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                        player_id INTEGER NOT NULL,
                        cat_data VARCHAR(1000) NOT NULL
                    )
                    """);

            statement.execute("""
                    CREATE TABLE player_inventory (
                        inventory_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                        player_id INTEGER NOT NULL,
                        item_id INTEGER NOT NULL,
                        quantity INTEGER NOT NULL DEFAULT 0,
                        UNIQUE (player_id, item_id)
                    )
                    """);
        }
    }

    private int insertPlayer(String username, int balance) throws SQLException {
        String sql = """
                INSERT INTO player (
                    username,
                    password_hash,
                    currency_balance,
                    experience,
                    active_cat_id
                )
                VALUES (?, 'test-hash', ?, 0, NULL)
                """;

        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            statement.setString(1, username);
            statement.setInt(2, balance);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private int insertTraderItem(
            String name,
            String type,
            int price,
            int stock
    ) throws SQLException {

        String sql = """
                INSERT INTO trader_items (
                    item_name,
                    item_type,
                    description,
                    price,
                    stock_quantity
                )
                VALUES (?, ?, 'Test item', ?, ?)
                """;

        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            statement.setString(1, name);
            statement.setString(2, type);
            statement.setInt(3, price);
            statement.setInt(4, stock);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private int insertCat(
            int playerId,
            boolean playerCat,
            boolean inParty,
            int maxHp
    ) throws SQLException {

        Cat cat = new Cat(
                "TestCat",
                "Tabby",
                maxHp,
                new ArrayList<>(List.of("SCRATCH")),
                playerCat,
                inParty
        );

        String sql = "INSERT INTO cats (player_id, cat_data) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            statement.setInt(1, playerId);
            statement.setString(2, cat.toStorageString());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private void setActiveCat(int playerId, int catId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE player SET active_cat_id = ? WHERE player_id = ?"
             )) {
            statement.setInt(1, catId);
            statement.setInt(2, playerId);
            statement.executeUpdate();
        }
    }

    private int readPlayerBalance(int playerId) throws SQLException {
        return readInt(
                "SELECT currency_balance FROM player WHERE player_id = ?",
                playerId
        );
    }

    private int readItemStock(int itemId) throws SQLException {
        return readInt(
                "SELECT stock_quantity FROM trader_items WHERE item_id = ?",
                itemId
        );
    }

    private int readInt(String sql, int id) throws SQLException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    private boolean catExists(int catId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT cat_id FROM cats WHERE cat_id = ?"
             )) {
            statement.setInt(1, catId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}

