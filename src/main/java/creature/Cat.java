package creature;

import java.util.ArrayList;

/**
 * Represents one cat in the program.
 *
 * A Cat object exists in Java memory.
 * CatDAO can turn it into one String and save that String in the database.
 *
 * Example stored cat:
 * Mittens|Tabby|100|SCRATCH;TAIL_WAG|true|true
 *
 * The last two values mean:
 * playerCat = true
 * inParty = true
 */
public class Cat {

    private int id;
    private String name;
    private String type;
    private int hp;
    private ArrayList abilities;

    // true if the player owns this cat
    private boolean playerCat;

    // true if this cat is currently in the player's active party
    private boolean inParty;


    /**
     * Constructor for a new cat that has not been saved yet.
     * Its database ID starts at 0.
     */
    public Cat(
            String name,
            String type,
            int hp,
            ArrayList abilities,
            boolean playerCat,
            boolean inParty
    ) {
        this.id = 0;
        this.name = name;
        this.type = type;
        this.hp = hp;
        this.abilities = new ArrayList(abilities);
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
            ArrayList abilities,
            boolean playerCat,
            boolean inParty
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.hp = hp;
        this.abilities = new ArrayList(abilities);
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


    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }


    public ArrayList getAbilities() {
        return new ArrayList(abilities);
    }

    public void setAbilities(ArrayList abilities) {
        this.abilities = new ArrayList(abilities);
    }


    public boolean isPlayerCat() {
        return playerCat;
    }

    public void setPlayerCat(boolean playerCat) {
        this.playerCat = playerCat;
    }


    public boolean isInParty() {
        return inParty;
    }

    public void setInParty(boolean inParty) {
        this.inParty = inParty;
    }


    /**
     * Converts this Cat into one String for the database.
     *
     * Example:
     * Mochi|Tabby|95|SCRATCH;POUNCE|true|true
     */
    public String toStorageString() {

        String abilityText = "";

        for (int i = 0; i < abilities.size(); i++) {

            abilityText = abilityText + abilities.get(i);

            if (i < abilities.size() - 1) {
                abilityText = abilityText + ";";
            }
        }

        return name
                + "|" + type
                + "|" + hp
                + "|" + abilityText
                + "|" + playerCat
                + "|" + inParty;
    }


    /**
     * Converts a stored database String back into a Cat object.
     */
    public static Cat fromStorageString(
            int id,
            String storedText
    ) {

        String[] parts = storedText.split("\\|", -1);

        if (parts.length != 6) {
            throw new IllegalArgumentException(
                    "Stored cat data is not in the expected format"
            );
        }

        String name = parts[0];

        String type = parts[1];

        int hp =
                Integer.parseInt(parts[2]);


        ArrayList abilities =
                new ArrayList();

        if (!parts[3].isEmpty()) {

            String[] abilityParts =
                    parts[3].split(";");

            for (String ability : abilityParts) {
                abilities.add(ability);
            }
        }


        boolean playerCat =
                Boolean.parseBoolean(parts[4]);

        boolean inParty =
                Boolean.parseBoolean(parts[5]);


        return new Cat(
                id,
                name,
                type,
                hp,
                abilities,
                playerCat,
                inParty
        );
    }


    /**
     * Controls how a Cat appears when printed.
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

        for (int i = 0; i < abilities.size(); i++) {

            abilitiesText =
                    abilitiesText + abilities.get(i);

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
                + " | "
                + hp
                + " HP"
                + " | Abilities: "
                + abilitiesText;
    }
}