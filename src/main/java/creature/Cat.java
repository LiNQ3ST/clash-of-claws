package creature;

import java.util.ArrayList;

/**
 * Represents one cat in the program.
 *
 * Cat data is stored as one String in the database.
 *
 * Current format:
 * name|type|maxHp|currentHp|abilities|playerCat|inParty
 *
 * Example:
 * Mittens|Tabby|100|75|SCRATCH;POUNCE|true|true
 */
public class Cat {

    private int id;
    private String name;
    private String type;

    // The cat's maximum HP.
    private int maxHp;

    // The cat's current HP.
    // This can change during battles.
    private int currentHp;

    private ArrayList<String> abilities;

    // true if the player owns this cat
    private boolean playerCat;

    // true if the cat is currently in the player's party
    private boolean inParty;


    /**
     * Constructor for a new cat.
     * New cats start at full HP.
     */
    public Cat(
            String name,
            String type,
            int maxHp,
            ArrayList<String> abilities,
            boolean playerCat,
            boolean inParty
    ) {

        this.id = 0;
        this.name = name;
        this.type = type;

        setMaxHp(maxHp);
        setCurrentHp(maxHp);

        this.abilities =
                new ArrayList<String>(abilities);

        this.playerCat = playerCat;
        this.inParty = inParty;
    }


    /**
     * Constructor for a cat loaded from the database.
     */
    public Cat(
            int id,
            String name,
            String type,
            int maxHp,
            int currentHp,
            ArrayList<String> abilities,
            boolean playerCat,
            boolean inParty
    ) {

        this.id = id;
        this.name = name;
        this.type = type;

        setMaxHp(maxHp);
        setCurrentHp(currentHp);

        this.abilities =
                new ArrayList<String>(abilities);

        this.playerCat = playerCat;
        this.inParty = inParty;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns file path of each cat spritesheet
     */
    public String getSpriteSheetPath() {

        return switch (type) {
            case "Tabby" ->
                    "/images/tabby-spritesheet.png";

            case "Sphynx" ->
                    "/images/sphynx-spritesheet.png";

            case "Maine Coon" ->
                    "/images/mainecoon-spritesheet.png";

            case "Siamese" ->
                    "/images/siamese-spritesheet.png";

            case "Calico" ->
                    "/images/calico-spritesheet.png";

            default ->
                    throw new IllegalStateException(
                            "No sprite sheet for cat type: " + type
                    );
        };
    }


    /**
     * Returns the cat's maximum HP.
     *
     * This should be preferred over getHp().
     */
    public int getMaxHp() {
        return maxHp;
    }


    /**
     * Sets the cat's maximum HP.
     *
     * Maximum HP must always be greater than 0.
     * If the new maximum is below the current HP,
     * current HP is lowered to match the new maximum.
     */
    public void setMaxHp(int maxHp) {

        if (maxHp <= 0) {

            throw new IllegalArgumentException(
                    "Max HP must be greater than 0"
            );
        }


        this.maxHp = maxHp;


        if (currentHp > maxHp) {
            currentHp = maxHp;
        }
    }


    /**
     * Older method name kept so existing code
     * does not immediately break.
     *
     * New code should use getMaxHp().
     */
    @Deprecated
    public int getHp() {
        return getMaxHp();
    }


    /**
     * Older method name kept so existing code
     * does not immediately break.
     *
     * New code should use setMaxHp().
     */
    @Deprecated
    public void setHp(int hp) {
        setMaxHp(hp);
    }


    /**
     * Returns the cat's current battle HP.
     */
    public int getCurrentHp() {
        return currentHp;
    }


    /**
     * Sets the cat's current HP.
     *
     * Current HP cannot go below 0
     * or above maximum HP.
     */
    public void setCurrentHp(int currentHp) {

        if (currentHp < 0) {

            this.currentHp = 0;

        } else if (currentHp > maxHp) {

            this.currentHp = maxHp;

        } else {

            this.currentHp = currentHp;
        }
    }


    public ArrayList<String> getAbilities() {

        return new ArrayList<String>(
                abilities
        );
    }


    public void setAbilities(
            ArrayList<String> abilities
    ) {

        this.abilities =
                new ArrayList<String>(
                        abilities
                );
    }


    public boolean isPlayerCat() {
        return playerCat;
    }


    public void setPlayerCat(
            boolean playerCat
    ) {
        this.playerCat = playerCat;
    }


    public boolean isInParty() {
        return inParty;
    }


    public void setInParty(
            boolean inParty
    ) {
        this.inParty = inParty;
    }


    /**
     * Converts this Cat into one String for the database.
     *
     * Current format:
     * name|type|maxHp|currentHp|abilities|playerCat|inParty
     *
     * Example:
     * Mochi|Tabby|100|75|SCRATCH;POUNCE|true|true
     */
    public String toStorageString() {

        String abilityText = "";


        for (int i = 0;
             i < abilities.size();
             i++) {

            abilityText =
                    abilityText
                            + abilities.get(i);


            if (i < abilities.size() - 1) {

                abilityText =
                        abilityText + ";";
            }
        }


        return name
                + "|" + type
                + "|" + maxHp
                + "|" + currentHp
                + "|" + abilityText
                + "|" + playerCat
                + "|" + inParty;
    }


    /**
     * Converts database text back into a Cat.
     *
     * Supports all three formats used during development.
     *
     * 5 parts:
     * name|type|maxHp|abilities|playerCat
     *
     * 6 parts:
     * name|type|maxHp|abilities|playerCat|inParty
     *
     * 7 parts:
     * name|type|maxHp|currentHp|abilities|playerCat|inParty
     */
    public static Cat fromStorageString(
            int id,
            String storedText
    ) {

        String[] parts =
                storedText.split("\\|", -1);


        if (parts.length != 5
                && parts.length != 6
                && parts.length != 7) {

            throw new IllegalArgumentException(
                    "Stored cat data is not in the expected format"
            );
        }


        String name =
                parts[0];


        String type =
                parts[1];


        int maxHp =
                Integer.parseInt(
                        parts[2]
                );


        int currentHp;

        int abilityIndex;
        int playerCatIndex;

        boolean inParty;


        /*
         * Oldest format:
         *
         * name|type|maxHp|abilities|playerCat
         *
         * These records did not store current HP
         * or party status.
         */
        if (parts.length == 5) {

            currentHp = maxHp;

            abilityIndex = 3;
            playerCatIndex = 4;

            /*
             * Since the old data does not tell us
             * whether the cat was in the party,
             * it defaults to not being in the party.
             */
            inParty = false;
        }


        /*
         * Previous format:
         *
         * name|type|maxHp|abilities|playerCat|inParty
         *
         * These records did not store current HP.
         */
        else if (parts.length == 6) {

            currentHp = maxHp;

            abilityIndex = 3;
            playerCatIndex = 4;

            inParty =
                    Boolean.parseBoolean(
                            parts[5]
                    );
        }


        /*
         * Current format:
         *
         * name|type|maxHp|currentHp|abilities|playerCat|inParty
         */
        else {

            currentHp =
                    Integer.parseInt(
                            parts[3]
                    );

            abilityIndex = 4;
            playerCatIndex = 5;

            inParty =
                    Boolean.parseBoolean(
                            parts[6]
                    );
        }


        ArrayList<String> abilities =
                new ArrayList<String>();


        if (!parts[abilityIndex].isEmpty()) {

            String[] abilityParts =
                    parts[abilityIndex]
                            .split(";");


            for (String ability : abilityParts) {

                abilities.add(
                        ability
                );
            }
        }


        boolean playerCat =
                Boolean.parseBoolean(
                        parts[playerCatIndex]
                );


        return new Cat(
                id,
                name,
                type,
                maxHp,
                currentHp,
                abilities,
                playerCat,
                inParty
        );
    }


    /**
     * Controls how the cat appears in lists.
     */
    @Override
    public String toString() {

        String category;


        if (playerCat) {

            if (inParty) {

                category =
                        "Party Cat";

            } else {

                category =
                        "Stored Cat";
            }

        } else {

            category =
                    "Opponent";
        }


        String abilitiesText = "";


        for (int i = 0;
             i < abilities.size();
             i++) {

            abilitiesText =
                    abilitiesText
                            + abilities.get(i);


            if (i < abilities.size() - 1) {

                abilitiesText =
                        abilitiesText + ", ";
            }
        }


        return category
                + " | "
                + name
                + " | "
                + type
                + " | HP: "
                + currentHp
                + "/"
                + maxHp
                + " | Abilities: "
                + abilitiesText;
    }
}