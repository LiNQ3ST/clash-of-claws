package creature;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatDAOTest {

    private CatDAO catDAO;
    private int testCatId;

    @BeforeEach
    void setUp() {
        catDAO = new CatDAO();
        catDAO.initializeTable();

        testCatId = 0;
    }

    @AfterEach
    void tearDown() {
        if (testCatId > 0) {
            catDAO.delete(testCatId);
        }
    }

    @Test
    void insertAndFindCat() {

        ArrayList<String> abilities =
                new ArrayList<String>();

        abilities.add("SCRATCH");
        abilities.add("POUNCE");

        Cat testCat = new Cat(
                "Test Cat " + System.nanoTime(),
                "Tabby",
                100,
                abilities,
                false
        );

        Cat savedCat = catDAO.insert(testCat);

        testCatId = savedCat.getId();

        assertTrue(savedCat.getId() > 0); // checks to ensure the numbering is correct

        Cat loadedCat =
                catDAO.findById(savedCat.getId());

        assertNotNull(loadedCat); // ensures that the read system from the db is working

        assertEquals(
                savedCat.toStorageString(),
                loadedCat.toStorageString() // ensures that data isnt warped by the db
        );
    }
}