package database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public final class DatabaseManager {

    public static final String DATABASE_URL_PROPERTY = "clashofclaws.database.url";
    private static final String DEFAULT_DATABASE_URL = "jdbc:sqlite:clash-of-claws.db";

    private static final DatabaseManager INSTANCE = new DatabaseManager();

    private DatabaseManager() {
        // Prevents additional DatabaseManager instances.
    }

    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return getConnection(currentDatabaseUrl());
    }

    public Connection getConnection(String databaseUrl) throws SQLException {
        Connection connection = DriverManager.getConnection(databaseUrl);

        if (databaseUrl.startsWith("jdbc:sqlite:")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            }
        }

        return connection;
    }

    public void initializeDatabase() throws SQLException {
        initializeDatabase(currentDatabaseUrl());
    }

    public void initializeDatabase(String databaseUrl) throws SQLException {
        String schema = loadSchema();
        String executableSchema = schema.lines().filter(line ->
                !line.stripLeading().startsWith("--")
        ).collect(
                Collectors.joining(System.lineSeparator())
        );

        try (Connection connection = getConnection(databaseUrl);
             Statement statement = connection.createStatement()) {
            for (String sql : executableSchema.split(";")) {
                String trimmedSql = sql.trim();
                if (!trimmedSql.isEmpty()) {
                    statement.execute(trimmedSql);
                }
            }
            migrateLegacySchema(connection);
        }
    }

    private void migrateLegacySchema(Connection connection) throws SQLException {
        migrateLegacyCats(connection);
        migrateStartingCurrency(connection);
    }

    private void migrateLegacyCats(Connection connection) throws SQLException {
        if (tableExists(connection, "cats")
                && !columnExists(connection, "cats", "player_id")) {

            try (Statement statement = connection.createStatement()) {
                statement.execute(
                        "ALTER TABLE cats ADD COLUMN player_id INTEGER"
                );
            }
        }
    }

    private void migrateStartingCurrency(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {

            statement.execute("""
                CREATE TABLE IF NOT EXISTS schema_migrations (
                    migration_name TEXT PRIMARY KEY
                )
                """);

            try (ResultSet resultSet = statement.executeQuery("""
                SELECT migration_name
                FROM schema_migrations
                WHERE migration_name = 'starting_currency_100'
                """)) {

                if (resultSet.next()) {
                    return;
                }
            }

            statement.executeUpdate("""
                UPDATE player
                SET currency_balance = currency_balance + 100
                """);

            statement.executeUpdate("""
                INSERT INTO schema_migrations (migration_name)
                VALUES ('starting_currency_100')
                """);
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet tables = metaData.getTables(null, null, tableName, null)) {
            return tables.next();
        }
    }

    private boolean columnExists(Connection connection, String tableName, String columnName)
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
            while (columns.next()) {
                if (columnName.equalsIgnoreCase(columns.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String currentDatabaseUrl() {
        return System.getProperty(
                DATABASE_URL_PROPERTY,
                DEFAULT_DATABASE_URL
        );
    }

    private String loadSchema() {
        try (InputStream inputStream =
                     DatabaseManager.class.getResourceAsStream(
                             "/database/schema.sql")
        ) {
            if (inputStream == null) {
                throw new IllegalStateException("Could not find /database/schema.sql");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load database schema.", exception);
        }
    }
}
