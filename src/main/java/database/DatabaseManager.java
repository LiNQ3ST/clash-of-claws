package database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {

    private static final String DATABASE_URL =
            "jdbc:sqlite:clash-of-claws.db";

    private DatabaseManager() {
        // Prevents this utility class from being instantiated.
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(DATABASE_URL);
    }

    public static Connection getConnection(String databaseUrl)
            throws SQLException {
        Connection connection =
                DriverManager.getConnection(databaseUrl);

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }

    public static void initializeDatabase() throws SQLException {
        initializeDatabase(DATABASE_URL);
    }

    public static void initializeDatabase(String databaseUrl)
            throws SQLException {
        String schema = loadSchema();

        try (
                Connection connection = getConnection(databaseUrl);
                Statement statement = connection.createStatement()
        ) {
            for (String sql : schema.split(";")) {
                String trimmedSql = sql.trim();

                if (!trimmedSql.isEmpty()) {
                    statement.execute(trimmedSql);
                }
            }
        }
    }

    private static String loadSchema() {
        try (InputStream inputStream =
                     DatabaseManager.class.getResourceAsStream(
                             "/database/schema.sql"
                     )) {
            if (inputStream == null) {
                throw new IllegalStateException(
                        "Could not find /database/schema.sql"
                );
            }

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not load database schema.",
                    exception
            );
        }
    }
}