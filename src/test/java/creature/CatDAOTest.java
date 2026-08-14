package creature;

import account.Player;
import account.PlayerDAO;
import database.DatabaseManager;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the CatDAO database operations.
 *
 * A temporary player is created before each test because
 * every cat must belong to a valid player_id.
 *
 * Test cats and the temporary player are deleted afterward.
 */
class CatDAOTest {

    private CatDAO catDAO;
    private PlayerDAO playerDAO;

    private int playerId;


    /**
     * Creates the database tables and a temporary player
     * before each test.
     */
    @BeforeEach
    void setUp() throws SQLException {

        DatabaseManager
                .getInstance()
                .initializeDatabase();


        catDAO =
                new CatDAO();

        playerDAO =
                new PlayerDAO();


        Player testPlayer =
                new Player(
                        "cat-dao-test-"
                                + System.nanoTime(),
                        "test-password-hash"
                );


        playerDAO.create(
                testPlayer
        );


        playerId =
                testPlayer.getPlayerId();
    }


    /**
     * Deletes all cats created for the test player,
     * then deletes the temporary player.
     */
    @AfterEach
    void tearDown() throws SQLException {

        if (playerId > 0) {

            ArrayList<Cat> cats =
                    catDAO.findAll(
                            playerId
                    );


            for (Cat cat : cats) {

                catDAO.delete(
                        cat.getId(),
                        playerId
                );
            }


            playerDAO.deleteAccount(
                    playerId
            );
        }
    }


    /**
     * Tests inserting a Cat and loading it again.
     */
    @Test
    void insertAndFindCat() {

        ArrayList<String> abilities =
                new ArrayList<String>();

        abilities.add(
                "SCRATCH"
        );

        abilities.add(
                "POUNCE"
        );


        Cat testCat =
                new Cat(
                        "Test Cat",
                        "Tabby",
                        100,
                        abilities,
                        true,
                        true
                );


        testCat.setCurrentHp(
                75
        );


        Cat savedCat =
                catDAO.insert(
                        testCat,
                        playerId
                );


        assertTrue(
                savedCat.getId() > 0
        );


        Cat loadedCat =
                catDAO.findById(
                        savedCat.getId(),
                        playerId
                );


        assertNotNull(
                loadedCat
        );


        assertEquals(
                "Test Cat",
                loadedCat.getName()
        );

        assertEquals(
                "Tabby",
                loadedCat.getType()
        );

        assertEquals(
                100,
                loadedCat.getMaxHp()
        );

        assertEquals(
                75,
                loadedCat.getCurrentHp()
        );

        assertEquals(
                abilities,
                loadedCat.getAbilities()
        );

        assertTrue(
                loadedCat.isPlayerCat()
        );

        assertTrue(
                loadedCat.isInParty()
        );
    }


    /**
     * Tests updating a stored Cat.
     */
    @Test
    void updateCat() {

        ArrayList<String> abilities =
                new ArrayList<String>();

        abilities.add(
                "SCRATCH"
        );


        Cat testCat =
                new Cat(
                        "Update Cat",
                        "Siamese",
                        100,
                        abilities,
                        true,
                        true
                );


        catDAO.insert(
                testCat,
                playerId
        );


        /*
         * Simulate the cat taking damage
         * and being moved into storage.
         */
        testCat.setCurrentHp(
                40
        );

        testCat.setInParty(
                false
        );


        boolean updated =
                catDAO.update(
                        testCat,
                        playerId
                );


        assertTrue(
                updated
        );


        Cat loadedCat =
                catDAO.findById(
                        testCat.getId(),
                        playerId
                );


        assertNotNull(
                loadedCat
        );

        assertEquals(
                40,
                loadedCat.getCurrentHp()
        );

        assertFalse(
                loadedCat.isInParty()
        );
    }


    /**
     * Tests that party cats and stored cats
     * are separated correctly.
     */
    @Test
    void findPartyAndStoredCats() {

        ArrayList<String> abilities =
                new ArrayList<String>();

        abilities.add(
                "SCRATCH"
        );


        Cat partyCat =
                new Cat(
                        "Party Cat",
                        "Tabby",
                        100,
                        abilities,
                        true,
                        true
                );


        Cat storedCat =
                new Cat(
                        "Stored Cat",
                        "Calico",
                        110,
                        abilities,
                        true,
                        false
                );


        Cat opponentCat =
                new Cat(
                        "Opponent Cat",
                        "Sphynx",
                        80,
                        abilities,
                        false,
                        false
                );


        catDAO.insert(
                partyCat,
                playerId
        );

        catDAO.insert(
                storedCat,
                playerId
        );

        catDAO.insert(
                opponentCat,
                playerId
        );


        ArrayList<Cat> partyCats =
                catDAO.findPartyCats(
                        playerId
                );


        ArrayList<Cat> storedCats =
                catDAO.findStoredCats(
                        playerId
                );


        assertEquals(
                1,
                partyCats.size()
        );

        assertEquals(
                "Party Cat",
                partyCats.get(0).getName()
        );


        assertEquals(
                1,
                storedCats.size()
        );

        assertEquals(
                "Stored Cat",
                storedCats.get(0).getName()
        );
    }


    /**
     * Tests deleting a Cat.
     */
    @Test
    void deleteCat() {

        ArrayList<String> abilities =
                new ArrayList<String>();

        abilities.add(
                "POUNCE"
        );


        Cat testCat =
                new Cat(
                        "Delete Cat",
                        "Maine Coon",
                        130,
                        abilities,
                        true,
                        false
                );


        catDAO.insert(
                testCat,
                playerId
        );


        boolean deleted =
                catDAO.delete(
                        testCat.getId(),
                        playerId
                );


        assertTrue(
                deleted
        );


        Cat loadedCat =
                catDAO.findById(
                        testCat.getId(),
                        playerId
                );


        assertNull(
                loadedCat
        );
    }
}