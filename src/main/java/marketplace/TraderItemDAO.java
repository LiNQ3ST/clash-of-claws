package marketplace;
/*
 */
import database.DatabaseManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class TraderItemDAO {

    private final String jdbcUrl;

    public TraderItemDAO() {
        this.jdbcUrl = null;
    }

    /**
     * Constructor used by tests so the DAO can connect to an in-memory H2 database.
     */
    public TraderItemDAO(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            throw new IllegalArgumentException("JDBC URL cannot be blank.");
        }
        this.jdbcUrl = jdbcUrl;
    }

    public TraderItem createItem(TraderItem item) throws SQLException {
        validateItem(item);

        String sql = """
                INSERT INTO trader_items
                    (item_name, item_type, description, price, stock_quantity)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            setItemParameters(statement, item);
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted != 1) {
                throw new SQLException("The trader item was not inserted.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("The database did not return an item ID.");
                }
                item.setItemId(generatedKeys.getInt(1));
            }

            return item;
        }
    }

    public Optional<TraderItem> findById(int itemId) throws SQLException {
        String sql = """
                SELECT item_id, item_name, item_type, description, price, stock_quantity
                FROM trader_items
                WHERE item_id = ?
                """;

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, itemId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapItem(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    public List<TraderItem> findAll() throws SQLException {
        String sql = """
                SELECT item_id, item_name, item_type, description, price, stock_quantity
                FROM trader_items
                ORDER BY item_name
                """;
        return runListQuery(sql, null);
    }

    public List<TraderItem> findAllAvailableItems() throws SQLException {
        String sql = """
                SELECT item_id, item_name, item_type, description, price, stock_quantity
                FROM trader_items
                WHERE stock_quantity > 0
                ORDER BY item_name
                """;
        return runListQuery(sql, null);
    }

    public List<TraderItem> findByItemType(String itemType) throws SQLException {
        if (itemType == null || itemType.isBlank()) {
            throw new IllegalArgumentException("Item type cannot be blank.");
        }

        String sql = """
                SELECT item_id, item_name, item_type, description, price, stock_quantity
                FROM trader_items
                WHERE UPPER(item_type) = ?
                ORDER BY item_name
                """;

        return runListQuery(sql, itemType.trim().toUpperCase(Locale.ROOT));
    }

    public boolean updateItem(TraderItem item) throws SQLException {
        validateItem(item);
        if (item.getItemId() <= 0) {
            throw new IllegalArgumentException("Item ID must be greater than zero.");
        }

        String sql = """
                UPDATE trader_items
                SET item_name = ?,
                    item_type = ?,
                    description = ?,
                    price = ?,
                    stock_quantity = ?
                WHERE item_id = ?
                """;

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setItemParameters(statement, item);
            statement.setInt(6, item.getItemId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean deleteItem(int itemId) throws SQLException {
        String sql = "DELETE FROM trader_items WHERE item_id = ?";

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, itemId);
            return statement.executeUpdate() == 1;
        }
    }

    private List<TraderItem> runListQuery(String sql, String parameter) throws SQLException {
        List<TraderItem> items = new ArrayList<>();

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (parameter != null) {
                statement.setString(1, parameter);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapItem(resultSet));
                }
            }
        }

        return items;
    }

    private Connection openConnection() throws SQLException {
        if (jdbcUrl == null) {
            return DatabaseManager.getConnection();
        }
        return DriverManager.getConnection(jdbcUrl);
    }

    private static void setItemParameters(
            PreparedStatement statement,
            TraderItem item
    ) throws SQLException {
        statement.setString(1, item.getItemName().trim());
        statement.setString(2, item.getItemType().trim().toUpperCase(Locale.ROOT));
        statement.setString(3, item.getDescription().trim());
        statement.setInt(4, item.getPrice());
        statement.setInt(5, item.getStockQuantity());
    }

    private static TraderItem mapItem(ResultSet resultSet) throws SQLException {
        return new TraderItem(
                resultSet.getInt("item_id"),
                resultSet.getString("item_name"),
                resultSet.getString("item_type"),
                resultSet.getString("description"),
                resultSet.getInt("price"),
                resultSet.getInt("stock_quantity")
        );
    }

    private static void validateItem(TraderItem item) {
        Objects.requireNonNull(item, "Trader item cannot be null.");
        requireText(item.getItemName(), "Item name");
        requireText(item.getItemType(), "Item type");
        requireText(item.getDescription(), "Description");

        if (item.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (item.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank.");
        }
    }
}

