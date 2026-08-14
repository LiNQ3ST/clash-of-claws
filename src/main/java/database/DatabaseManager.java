package database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
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
        }
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
