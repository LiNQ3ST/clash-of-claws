package creature;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatDAOTest {

    private CatDAO catDAO;
    private final ArrayList<Integer> testCatIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        catDAO = new CatDAO();
        catDAO.initializeTable();
    }

    @AfterEach
    void tearDown() {
        for (int catId : testCatIds) {
            catDAO.delete(catId);
        }

        testCatIds.clear();
    }

    /*
     * CREATE
     *
     * Verifies that a Cat can be inserted into the database
     * and receives a valid database ID.
     */
    @Test
    void insertCat() {
        Cat testCat = createTestCat("Create Test Cat");

        Cat savedCat = catDAO.insert(testCat);
        testCatIds.add(savedCat.getId());

        assertNotNull(savedCat);
        assertTrue(savedCat.getId() > 0);
    }

    /*
     * READ
     *
     * Verifies that an inserted Cat can be retrieved by its ID
     * without its data being changed.
     */
    @Test
    void findCatById() {
        Cat testCat = createTestCat("Read Test Cat");

        Cat savedCat = catDAO.insert(testCat);
        testCatIds.add(savedCat.getId());

        Cat loadedCat = catDAO.findById(savedCat.getId());

        assertNotNull(loadedCat);
        assertEquals(savedCat.getId(), loadedCat.getId());
        assertEquals(
                savedCat.toStorageString(),
                loadedCat.toStorageString()
        );
    }

    /*
     * UPDATE
     *
     * Verifies that an existing Cat can be changed and that
     * the updated information is stored in the database.
     */
    @Test
    void updateCat() {
        Cat testCat = createTestCat("Original Cat");

        Cat savedCat = catDAO.insert(testCat);
        testCatIds.add(savedCat.getId());

        ArrayList<String> updatedAbilities = new ArrayList<>();
        updatedAbilities.add("BITE");
        updatedAbilities.add("CLIMB");

        savedCat.setName("Updated Cat");
        savedCat.setType("Siamese");
        savedCat.setHp(150);
        savedCat.setAbilities(updatedAbilities);

        catDAO.update(savedCat);

        Cat updatedCat = catDAO.findById(savedCat.getId());

        assertNotNull(updatedCat);
        assertEquals(savedCat.getId(), updatedCat.getId());
        assertEquals(
                savedCat.toStorageString(),
                updatedCat.toStorageString()
        );
    }

    /*
     * DELETE
     *
     * Verifies that a Cat is no longer present after it
     * has been deleted from the database.
     */
    @Test
    void deleteCat() {
        Cat testCat = createTestCat("Delete Test Cat");

        Cat savedCat = catDAO.insert(testCat);
        int savedCatId = savedCat.getId();

        assertNotNull(catDAO.findById(savedCatId));

        catDAO.delete(savedCatId);

        Cat deletedCat = catDAO.findById(savedCatId);

        assertNull(deletedCat);
    }

    /*
     * Helper method used to create a unique Cat for each test.
     */
    private Cat createTestCat(String name) {
        ArrayList<String> abilities = new ArrayList<>();
        abilities.add("SCRATCH");
        abilities.add("POUNCE");

        return new Cat(
                name + " " + System.nanoTime(),
                "Tabby",
                100,
                abilities,
                false
        );
    }
}