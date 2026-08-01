package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseManager {

    private static final String DATABASE_URL =
            "jdbc:sqlite:clash-of-claws.db";

    private DatabaseManager() {
        // Prevents this utility class from being instantiated.
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }
}