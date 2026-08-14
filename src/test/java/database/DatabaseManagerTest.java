package database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getConnection() throws SQLException {
        try (Connection connection = DatabaseManager.getInstance().getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
        }
    }

    @Test
    void initializeDatabaseAddsMissingPlayerIdColumnToLegacyCatsTable() throws Exception {
        Path tempDb = Files.createTempFile("legacy-cats-db", ".db");
        String databaseUrl = "jdbc:sqlite:" + tempDb.toAbsolutePath();

        try (Connection connection = DriverManager.getConnection(databaseUrl);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE cats (cat_id INTEGER PRIMARY KEY AUTOINCREMENT, cat_data TEXT NOT NULL)");
        }

        DatabaseManager.getInstance().initializeDatabase(databaseUrl);

        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            List<String> columns = new ArrayList<>();
            try (ResultSet resultSet = connection.getMetaData().getColumns(null, null, "cats", null)) {
                while (resultSet.next()) {
                    columns.add(resultSet.getString("COLUMN_NAME"));
                }
            }
            assertTrue(columns.contains("player_id"));
        }
    }
}