package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Statement;

public final class DatabaseManager {

  private static final String DATABASE_URL =
      "jdbc:sqlite:clash-of-claws.db";

  private static final DatabaseManager INSTANCE =
      new DatabaseManager();

  private DatabaseManager() {
    // Prevent outside code from creating additional instances.
  }

  public static DatabaseManager getInstance() {
    return INSTANCE;
  }

  public Connection getConnection() throws SQLException {
    Connection connection =
        DriverManager.getConnection(DATABASE_URL);

    try (Statement statement = connection.createStatement()) {
      statement.execute("PRAGMA foreign_keys = ON");
    }

    return connection;
  }

  public void initializeDatabase() {
    try (
        InputStream inputStream = DatabaseManager.class.getResourceAsStream("/database/schema.sql")
    ) {
      if (inputStream == null) {
        throw new IllegalStateException("Could not find /database/schema.sql");
      }

      String schema = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      String executableSchema = schema.lines()
          .filter(line -> !line.trim().startsWith("--"))
          .reduce("", (result, line) -> result + line + System.lineSeparator());

      try (
          Connection connection = getConnection();
          Statement statement = connection.createStatement()
      ) {
        for (String sql : executableSchema.split(";")) {
          String trimmedSql = sql.trim();

          if (!trimmedSql.isEmpty()) {
            statement.execute(trimmedSql);
          }
        }
      }

    } catch (IOException | SQLException e) {
      throw new IllegalStateException("Could not initialize database", e);
    }
  }



}