package creature;

import java.util.ArrayList;

/**
 * Represents one cat in the program.
 *
 * Cat data is stored as one String in the database.
 *
 * format:
 * name|type|maxHp|currentHp|abilities|playerCat|inParty
 *
 * Example:
 * Mittens|Tabby|100|75|SCRATCH;POUNCE|true|true
 */
public class Cat {

    private int id;
    private String name;
    private String type;

    // hp is the cat's maximum HP.
    private int hp;

    // currentHp can change during battles.
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
            int hp,
            ArrayList<String> abilities,
            boolean playerCat,
            boolean inParty
    ) {
        this.id = 0;
        this.name = name;
        this.type = type;
        this.hp = hp;
        this.currentHp = hp;
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
            int hp,
            int currentHp,
            ArrayList<String> abilities,
            boolean playerCat,
            boolean inParty
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.hp = hp;
        this.currentHp = currentHp;
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
     * Returns maximum HP.
     */
    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;

        if (currentHp > hp) {
            currentHp = hp;
        }
    }


    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {

        if (currentHp < 0) {
            this.currentHp = 0;

        } else if (currentHp > hp) {
            this.currentHp = hp;

        } else {
            this.currentHp = currentHp;
        }
    }


    public ArrayList<String> getAbilities() {
        return new ArrayList<String>(abilities);
    }

    public void setAbilities(
            ArrayList<String> abilities
    ) {
        this.abilities =
                new ArrayList<String>(abilities);
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
     * Example:
     * Mochi|Tabby|100|75|SCRATCH;POUNCE|true|true
     */
    public String toStorageString() {

        String abilityText = "";

        for (int i = 0;
             i < abilities.size();
             i++) {

            abilityText =
                    abilityText + abilities.get(i);

            if (i < abilities.size() - 1) {
                abilityText =
                        abilityText + ";";
            }
        }


        return name
                + "|" + type
                + "|" + hp
                + "|" + currentHp
                + "|" + abilityText
                + "|" + playerCat
                + "|" + inParty;
    }


    /**
     * Converts database text back into a Cat.
     *
     * This also supports the older 6-part format.
     * Old cats are loaded at full HP.
     */
    public static Cat fromStorageString(
            int id,
            String storedText
    ) {

        String[] parts =
                storedText.split("\\|", -1);


        if (parts.length != 6
                && parts.length != 7) {

            throw new IllegalArgumentException(
                    "Stored cat data is not in the expected format"
            );
        }


        String name =
                parts[0];

        String type =
                parts[1];

        int hp =
                Integer.parseInt(parts[2]);


        int currentHp;

        int abilityIndex;
        int playerCatIndex;
        int inPartyIndex;


        /*
         * Older saved cats did not have current HP.
         * They are treated as being at full health.
         */
        if (parts.length == 6) {

            currentHp = hp;

            abilityIndex = 3;
            playerCatIndex = 4;
            inPartyIndex = 5;

        } else {

            currentHp =
                    Integer.parseInt(parts[3]);

            abilityIndex = 4;
            playerCatIndex = 5;
            inPartyIndex = 6;
        }


        ArrayList<String> abilities =
                new ArrayList<String>();


        if (!parts[abilityIndex].isEmpty()) {

            String[] abilityParts =
                    parts[abilityIndex].split(";");

            for (String ability
                    : abilityParts) {

                abilities.add(ability);
            }
        }


        boolean playerCat =
                Boolean.parseBoolean(
                        parts[playerCatIndex]
                );

        boolean inParty =
                Boolean.parseBoolean(
                        parts[inPartyIndex]
                );


        return new Cat(
                id,
                name,
                type,
                hp,
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
                category = "Party Cat";
            } else {
                category = "Stored Cat";
            }

        } else {
            category = "Opponent";
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
                + hp
                + " | Abilities: "
                + abilitiesText;
    }
}