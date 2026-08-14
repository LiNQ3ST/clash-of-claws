package creature;

import database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Saves, reads, updates, and deletes Cat data in SQLite.
 *
 * The cats table is created by database/schema.sql.
 *
 * Each cat belongs to one player using player_id.
 * Most of the cat's information is stored as one String
 * in the cat_data column.
 */
public class CatDAO {


    /**
     * CREATE:
     * Saves a cat for a specific player
     * and gives the Cat its database ID.
     */
    public Cat insert(
            Cat cat,
            int playerId
    ) {

        String sql =
                "INSERT INTO cats (player_id, cat_data) VALUES (?, ?)";

        try {

            Connection connection =
                    DatabaseManager.getInstance().getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );


            statement.setInt(
                    1,
                    playerId
            );

            statement.setString(
                    2,
                    cat.toStorageString()
            );


            statement.executeUpdate();


            ResultSet generatedKeys =
                    statement.getGeneratedKeys();


            if (generatedKeys.next()) {

                cat.setId(
                        generatedKeys.getInt(1)
                );
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
     * READ:
     * Loads one cat by its database ID.
     *
     * The player ID is also checked so that one player
     * cannot load another player's cat.
     *
     * Returns null if the cat is not found.
     */
    public Cat findById(
            int catId,
            int playerId
    ) {

        String sql =
                "SELECT cat_data "
                        + "FROM cats "
                        + "WHERE cat_id = ? "
                        + "AND player_id = ?";


        try {

            Connection connection =
                    DatabaseManager.getInstance().getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);


            statement.setInt(
                    1,
                    catId
            );

            statement.setInt(
                    2,
                    playerId
            );


            ResultSet resultSet =
                    statement.executeQuery();


            Cat cat = null;


            if (resultSet.next()) {

                String storedText =
                        resultSet.getString(
                                "cat_data"
                        );


                cat =
                        Cat.fromStorageString(
                                catId,
                                storedText
                        );
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
     * READ:
     * Loads all cats belonging to one player.
     */
    public ArrayList<Cat> findAll(
            int playerId
    ) {

        String sql =
                "SELECT cat_id, cat_data "
                        + "FROM cats "
                        + "WHERE player_id = ? "
                        + "ORDER BY cat_id";


        ArrayList<Cat> cats =
                new ArrayList<Cat>();


        try {

            Connection connection =
                    DatabaseManager.getInstance().getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);


            statement.setInt(
                    1,
                    playerId
            );


            ResultSet resultSet =
                    statement.executeQuery();


            while (resultSet.next()) {

                int catId =
                        resultSet.getInt(
                                "cat_id"
                        );

                String storedText =
                        resultSet.getString(
                                "cat_data"
                        );


                Cat cat =
                        Cat.fromStorageString(
                                catId,
                                storedText
                        );


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
     * UPDATE:
     * Replaces the stored cat String for an existing cat.
     */
    public boolean update(
            Cat cat,
            int playerId
    ) {

        String sql =
                "UPDATE cats "
                        + "SET cat_data = ? "
                        + "WHERE cat_id = ? "
                        + "AND player_id = ?";


        try {

            Connection connection =
                    DatabaseManager.getInstance().getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);


            statement.setString(
                    1,
                    cat.toStorageString()
            );

            statement.setInt(
                    2,
                    cat.getId()
            );

            statement.setInt(
                    3,
                    playerId
            );


            int changedRows =
                    statement.executeUpdate();


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
     * DELETE:
     * Removes one cat belonging to a player.
     */
    public boolean delete(
            int catId,
            int playerId
    ) {

        String sql =
                "DELETE FROM cats "
                        + "WHERE cat_id = ? "
                        + "AND player_id = ?";


        try {

            Connection connection =
                    DatabaseManager.getInstance().getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);


            statement.setInt(
                    1,
                    catId
            );

            statement.setInt(
                    2,
                    playerId
            );


            int changedRows =
                    statement.executeUpdate();


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
     * Checks whether this exact cat already exists
     * for this player.
     */
    public boolean exists(
            Cat cat,
            int playerId
    ) {

        String sql =
                "SELECT cat_id "
                        + "FROM cats "
                        + "WHERE player_id = ? "
                        + "AND cat_data = ?";


        try {

            Connection connection =
                    DatabaseManager.getInstance().getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);


            statement.setInt(
                    1,
                    playerId
            );

            statement.setString(
                    2,
                    cat.toStorageString()
            );


            ResultSet resultSet =
                    statement.executeQuery();


            boolean found =
                    resultSet.next();


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


    /**
     * Returns the player's cats that are currently
     * in the active party.
     */
    public ArrayList<Cat> findPartyCats(
            int playerId
    ) {

        ArrayList<Cat> allCats =
                findAll(playerId);

        ArrayList<Cat> partyCats =
                new ArrayList<Cat>();


        for (Cat cat : allCats) {

            if (cat.isPlayerCat()
                    && cat.isInParty()) {

                partyCats.add(cat);
            }
        }


        return partyCats;
    }


    /**
     * Returns the player's owned cats that are
     * currently in storage.
     */
    public ArrayList<Cat> findStoredCats(
            int playerId
    ) {

        ArrayList<Cat> allCats =
                findAll(playerId);

        ArrayList<Cat> storedCats =
                new ArrayList<Cat>();


        for (Cat cat : allCats) {

            if (cat.isPlayerCat()
                    && !cat.isInParty()) {

                storedCats.add(cat);
            }
        }


        return storedCats;
    }
    /**
     * Returns the number of cats currently in the party.
     */
    public int countPartyCats(
            int playerId
    ) {

        return findPartyCats(
                playerId
        ).size();
    }


    /**
     * Moves a stored cat into the party.
     *
     * Returns false if the party already has 4 cats.
     */
    public boolean moveToParty(
            Cat cat,
            int playerId
    ) {

        if (!cat.isPlayerCat()) {
            return false;
        }


        if (cat.isInParty()) {
            return true;
        }


        if (countPartyCats(playerId) >= 4) {
            return false;
        }


        cat.setInParty(true);

        return update(
                cat,
                playerId
        );
    }


    /**
     * Moves a party cat into storage.
     */
    public boolean moveToStorage(
            Cat cat,
            int playerId
    ) {

        if (!cat.isPlayerCat()) {
            return false;
        }


        cat.setInParty(false);

        return update(
                cat,
                playerId
        );
    }


    /**
     * Swaps one party cat with one stored cat.
     */
    public boolean swapCats(
            Cat partyCat,
            Cat storedCat,
            int playerId
    ) {

        if (!partyCat.isPlayerCat()
                || !storedCat.isPlayerCat()) {

            return false;
        }


        if (!partyCat.isInParty()) {
            return false;
        }


        if (storedCat.isInParty()) {
            return false;
        }


        partyCat.setInParty(false);
        storedCat.setInParty(true);


        boolean firstUpdate =
                update(
                        partyCat,
                        playerId
                );

        boolean secondUpdate =
                update(
                        storedCat,
                        playerId
                );


        return firstUpdate
                && secondUpdate;
    }

    public boolean deleteAllForPlayer(int playerId) {

        String sql =
            "DELETE FROM cats "
                + "WHERE player_id = ?";


        try (
            Connection connection =
                DatabaseManager
                    .getInstance()
                    .getConnection();

            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {

            statement.setInt(
                1,
                playerId
            );


            statement.executeUpdate();

            return true;

        } catch (SQLException exception) {

            exception.printStackTrace();

            return false;
        }
    }
}