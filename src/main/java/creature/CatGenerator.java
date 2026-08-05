package creature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Creates random cats using values stored in text files.
 */
public class CatGenerator {

    private ArrayList<String> names;
    private ArrayList<String> types;
    private ArrayList<Integer> hpValues;
    private ArrayList<String> abilities;
    private Random random;

    /**
     * Loads all generation lists from src/main/resources/app.
     */
    public CatGenerator() {
        names = loadTextFile("/app/cat_names.txt");
        types = loadTextFile("/app/cat_types.txt");
        hpValues = loadNumberFile("/app/cat_hp.txt"); // will most likely change, works for now as a placeholder
        abilities = loadTextFile("/app/ability_ids.txt");//holds the ID name for all the abilites
        random = new Random();
    }

    /**
     * Creates one random opponent cat in Java memory.
     * This method does not save the cat to the database.
     */
    public Cat generateCat() {
        String name = names.get(random.nextInt(names.size()));
        String type = types.get(random.nextInt(types.size()));
        int hp = hpValues.get(random.nextInt(hpValues.size()));

        int largestAbilityCount = 4;

        if (abilities.size() < 4) {
            largestAbilityCount = abilities.size();
        }

        int abilityCount = random.nextInt(largestAbilityCount) + 1;
        ArrayList<String> chosenAbilities = chooseAbilities(abilityCount);

        return new Cat(
                name,
                type,
                hp,
                chosenAbilities,
                false
        );
    }

    /**
     * Generates a cat, makes sure the exact stored String is new,
     * saves it, and returns it.
     */
    public Cat generateAndSaveCat(CatDAO catDAO) {
        for (int attempt = 0; attempt < 1000; attempt++) {
            Cat cat = generateCat();

            if (!catDAO.exists(cat)) {
                return catDAO.insert(cat);
            }
        }

        throw new IllegalStateException(
                "You already have defeated all the cats"
        );
    }

    /**
     * Creates and saves a player-made cat.
     */
    public Cat createAndSavePlayerCat(
            String name,
            String type,
            int hp,
            ArrayList<String> chosenAbilities,
            CatDAO catDAO
    ) {
        Cat playerCat = new Cat(
                name,
                type,
                hp,
                chosenAbilities,
                true
        );

        return catDAO.insert(playerCat);
    }

    /**
     * Selects the requested number of abilities without repeats.
     */
    private ArrayList<String> chooseAbilities(int amount) {
        ArrayList<String> availableAbilities =
                new ArrayList<String>(abilities);

        ArrayList<String> chosenAbilities =
                new ArrayList<String>();

        for (int i = 0; i < amount; i++) {
            int randomIndex = random.nextInt(availableAbilities.size());
            String chosenAbility = availableAbilities.get(randomIndex);

            chosenAbilities.add(chosenAbility);
            availableAbilities.remove(randomIndex);
        }

        return chosenAbilities;
    }

    /**
     * Reads a text file from resources and returns its usable lines.
     */
    private ArrayList<String> loadTextFile(String resourcePath) {
        ArrayList<String> values = new ArrayList<String>();

        InputStream inputStream =
                CatGenerator.class.getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IllegalStateException(
                    "Could not find resource: " + resourcePath
            );
        }

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream)
            );

            String line = reader.readLine();

            while (line != null) {
                line = line.trim();

                if (!line.isEmpty() && !line.startsWith("#")) {
                    values.add(line);
                }

                line = reader.readLine();
            }

            reader.close();

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not read resource: " + resourcePath,
                    exception
            );
        }

        if (values.isEmpty()) {
            throw new IllegalStateException(
                    "Resource has no usable values: " + resourcePath
            );
        }

        return values;
    }

    /**
     * Reads .txt file holding the premade random int for HP
     * will most likely change to be actual random gen
     */
    private ArrayList<Integer> loadNumberFile(String resourcePath) {
        ArrayList<String> textValues = loadTextFile(resourcePath);
        ArrayList<Integer> numbers = new ArrayList<Integer>();

        for (String textValue : textValues) {
            int number = Integer.parseInt(textValue);
            numbers.add(number);
        }

        return numbers;
    }
}