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
    private ArrayList<String> abilities;
    private Random random;


    /**
     * Loads the name, type, and ability lists.
     */
    public CatGenerator() {

        names = loadTextFile("/creature/cat_names.txt");
        types = loadTextFile("/creature/cat_types.txt");
        abilities = loadTextFile("/creature/ability_ids.txt");

        random = new Random();
    }


    /**
     * Creates one random opponent cat.
     * This does not save the cat to the database.
     */
    public Cat generateCat() {

        String name =
                names.get(random.nextInt(names.size()));

        String type =
                types.get(random.nextInt(types.size()));

        int hp =
                getRandomHp(type);


        int largestAbilityCount = 4;

        if (abilities.size() < 4) {
            largestAbilityCount = abilities.size();
        }


        int abilityCount =
                random.nextInt(largestAbilityCount) + 1;


        ArrayList<String> chosenAbilities =
                chooseAbilities(abilityCount);


        return new Cat(
                name,
                type,
                hp,
                chosenAbilities,
                false,
                false
        );
    }


    /**
     * Creates a starter cat chosen by the player.
     *
     * The player supplies the name and type.
     * The HP and abilities are generated automatically.
     */
    public Cat createStarterCat(
            String name,
            String type
    ) {

        int hp =
                getRandomHp(type);


        int abilityCount = 2;


        ArrayList<String> chosenAbilities =
                chooseAbilities(abilityCount);


        return new Cat(
                name,
                type,
                hp,
                chosenAbilities,
                true,
                true
        );
    }


    /**
     * Generates a random opponent cat,
     * checks that it does not already exist,
     * saves it to the database,
     * and returns the saved Cat.
     */
    public Cat generateAndSaveCat(
            CatDAO catDAO,
            int playerId
    ) {

        for (int attempt = 0; attempt < 1000; attempt++) {

            Cat cat =
                    generateCat();


            if (!catDAO.exists(
                    cat,
                    playerId
            )) {

                return catDAO.insert(
                        cat,
                        playerId
                );
            }
        }


        throw new IllegalStateException(
                "Could not generate a new unique cat"
        );
    }


    /**
     * Creates and saves a player-owned cat.
     *
     * This could be used for a starter cat,
     * caught cat, or another player-created cat.
     */
    public Cat createAndSavePlayerCat(
            String name,
            String type,
            int hp,
            ArrayList<String> chosenAbilities,
            CatDAO catDAO,
            int playerId
    ) {

        Cat playerCat =
                new Cat(
                        name,
                        type,
                        hp,
                        chosenAbilities,
                        true,
                        true
                );


        return catDAO.insert(
                playerCat,
                playerId
        );
    }


    /**
     * Selects the requested number of abilities.
     * Each chosen ability can only be selected once.
     */
    private ArrayList<String> chooseAbilities(
            int amount
    ) {

        ArrayList<String> availableAbilities =
                new ArrayList<String>(abilities);


        ArrayList<String> chosenAbilities =
                new ArrayList<String>();


        for (int i = 0; i < amount; i++) {

            int randomIndex =
                    random.nextInt(
                            availableAbilities.size()
                    );


            String chosenAbility =
                    availableAbilities.get(
                            randomIndex
                    );


            chosenAbilities.add(
                    chosenAbility
            );


            availableAbilities.remove(
                    randomIndex
            );
        }


        return chosenAbilities;
    }


    /**
     * Reads a text file from the resources folder.
     * Each usable line becomes one String in an ArrayList.
     */
    private ArrayList<String> loadTextFile(
            String resourcePath
    ) {

        ArrayList<String> values =
                new ArrayList<String>();


        InputStream inputStream =
                CatGenerator.class
                        .getResourceAsStream(
                                resourcePath
                        );


        if (inputStream == null) {

            throw new IllegalStateException(
                    "Could not find resource: "
                            + resourcePath
            );
        }


        try {

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    inputStream
                            )
                    );


            String line =
                    reader.readLine();


            while (line != null) {

                line =
                        line.trim();


                if (!line.isEmpty()
                        && !line.startsWith("#")) {

                    values.add(line);
                }


                line =
                        reader.readLine();
            }


            reader.close();


        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Could not read resource: "
                            + resourcePath,
                    exception
            );
        }


        if (values.isEmpty()) {

            throw new IllegalStateException(
                    "Resource has no usable values: "
                            + resourcePath
            );
        }


        return values;
    }


    /**
     * Generates random HP based on the cat's type.
     */
    private int getRandomHp(
            String type
    ) {

        int minimumHp;
        int maximumHp;


        switch (type) {

            case "Tabby":
                minimumHp = 80;
                maximumHp = 110;
                break;


            case "Sphynx":
                minimumHp = 65;
                maximumHp = 95;
                break;


            case "Maine Coon":
                minimumHp = 110;
                maximumHp = 150;
                break;


            case "Siamese":
                minimumHp = 75;
                maximumHp = 105;
                break;


            case "Calico":
                minimumHp = 85;
                maximumHp = 120;
                break;


            default:
                minimumHp = 80;
                maximumHp = 100;
                break;
        }


        return random.nextInt(
                maximumHp - minimumHp + 1
        ) + minimumHp;
    }
}