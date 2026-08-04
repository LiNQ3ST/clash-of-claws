package creature;

import database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * This class saves, reads, updates, and deletes Cat data in SQLite.
 * Each cat is stored as one long String in the cat_data column.
 */
public class CatDAO {


    //Creates the cats table if it does not already exist.
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS cats ("
                    + "cat_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "cat_data TEXT NOT NULL UNIQUE"
                    + ")";


    public void initializeTable() {
        try {
            Connection connection = DatabaseManager.getConnection();
            Statement statement = connection.createStatement();

            statement.execute(CREATE_TABLE_SQL);

            statement.close();
            connection.close();

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not create cats table",
                    exception
            );
        }
    }


      //CREATE: saves a cat and gives it its database ID.

    public Cat insert(Cat cat) {
        String sql =
                "INSERT INTO cats (cat_data) VALUES (?)";

        try {
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, cat.toStorageString());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                cat.setId(generatedKeys.getInt(1));
            }

            generatedKeys.close();
            statement.close();
            connection.close();

            return cat;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not insert cat",
                    exception
            );
        }
    }

    /**
     * READ: loads one cat by database ID.
     * Returns null when the ID is not found.
     */
    public Cat findById(int catId) {
        String sql =
                "SELECT cat_data FROM cats WHERE cat_id = ?";

        try {
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setInt(1, catId);

            ResultSet resultSet = statement.executeQuery();
            Cat cat = null;

            if (resultSet.next()) {
                String storedText = resultSet.getString("cat_data");
                cat = Cat.fromStorageString(catId, storedText);
            }

            resultSet.close();
            statement.close();
            connection.close();

            return cat;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not read cat",
                    exception
            );
        }
    }

    /**
     * READ: loads every stored cat.
     */
    public ArrayList<Cat> findAll() {
        String sql =
                "SELECT cat_id, cat_data FROM cats ORDER BY cat_id";

        ArrayList<Cat> cats = new ArrayList<Cat>();

        try {
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int catId = resultSet.getInt("cat_id");
                String storedText = resultSet.getString("cat_data");

                Cat cat = Cat.fromStorageString(catId, storedText);
                cats.add(cat);
            }

            resultSet.close();
            statement.close();
            connection.close();

            return cats;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not read cats",
                    exception
            );
        }
    }

    /**
     * UPDATE: replaces the stored String for an existing cat.
     */
    public boolean update(Cat cat) {
        String sql =
                "UPDATE cats SET cat_data = ? WHERE cat_id = ?";

        try {
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, cat.toStorageString());
            statement.setInt(2, cat.getId());

            int changedRows = statement.executeUpdate();

            statement.close();
            connection.close();

            return changedRows == 1;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not update cat",
                    exception
            );
        }
    }

    /**
     * DELETE: removes a cat using its database ID.
     */
    public boolean delete(int catId) {
        String sql =
                "DELETE FROM cats WHERE cat_id = ?";

        try {
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setInt(1, catId);

            int changedRows = statement.executeUpdate();

            statement.close();
            connection.close();

            return changedRows == 1;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not delete cat",
                    exception
            );
        }
    }

    /**
     * Checks whether the exact stored cat String already exists.
     */
    public boolean exists(Cat cat) {
        String sql =
                "SELECT cat_id FROM cats WHERE cat_data = ?";

        try {
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, cat.toStorageString());

            ResultSet resultSet = statement.executeQuery();
            boolean found = resultSet.next();

            resultSet.close();
            statement.close();
            connection.close();

            return found;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not check for duplicate cat",
                    exception
            );
        }
    }
}