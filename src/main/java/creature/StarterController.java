package creature;

import account.AccountService;
import account.Player;
import account.PlayerDAO;

import app.SceneFactory;
import app.SceneType;

import java.sql.SQLException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;


/**
 * Controls the starter cat selection screen.
 *
 * Three random cats are generated.
 * The player chooses one and gives it a name.
 */
public class StarterController {

    @FXML
    private RadioButton catOneButton;

    @FXML
    private RadioButton catTwoButton;

    @FXML
    private RadioButton catThreeButton;


    @FXML
    private Label catOneDetails;

    @FXML
    private Label catTwoDetails;

    @FXML
    private Label catThreeDetails;


    @FXML
    private TextField catNameField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button chooseButton;

    @FXML
    private ImageView catOneImage;

    @FXML
    private ImageView catTwoImage;

    @FXML
    private ImageView catThreeImage;


    private Cat catOne;
    private Cat catTwo;
    private Cat catThree;

    private Player player;

    private CatGenerator catGenerator;
    private CatDAO catDAO;
    private PlayerDAO playerDAO;


    /**
     * Runs when the scene is opened.
     */
    @FXML
    private void initialize() {

        catGenerator =
                new CatGenerator();

        catDAO =
                new CatDAO();

        playerDAO =
                new PlayerDAO();


        player =
                AccountService
                        .getInstance()
                        .getCurrentPlayer()
                        .orElse(null);


        if (player == null
                || player.getPlayerId() == null) {

            statusLabel.setText(
                    "No player is logged in."
            );

            chooseButton.setDisable(
                    true
            );

            return;
        }


        /*
         * Only one starter can be selected.
         */
        ToggleGroup starterGroup =
                new ToggleGroup();


        catOneButton.setToggleGroup(
                starterGroup
        );

        catTwoButton.setToggleGroup(
                starterGroup
        );

        catThreeButton.setToggleGroup(
                starterGroup
        );


        generateStarterChoices();
    }


    /**
     * Generates three random cats.
     *
     * They are not saved yet.
     */
    private void generateStarterChoices() {

        catOne =
                catGenerator.generateCat();

        catTwo =
                catGenerator.generateCat();

        catThree =
                catGenerator.generateCat();


        catOneDetails.setText(
                formatCat(catOne)
        );

        catTwoDetails.setText(
                formatCat(catTwo)
        );

        catThreeDetails.setText(
                formatCat(catThree)
        );

        CatSpriteRenderer.setSprite(
                catOneImage,
                catOne,
                CatSpriteRenderer.IDLE
        );

        CatSpriteRenderer.setSprite(
                catTwoImage,
                catTwo,
                CatSpriteRenderer.IDLE
        );

        CatSpriteRenderer.setSprite(
                catThreeImage,
                catThree,
                CatSpriteRenderer.IDLE
        );
    }


    /**
     * Creates the text shown underneath
     * each starter choice.
     *
     * The generated name is intentionally
     * not displayed because the player will
     * name the chosen cat.
     */
    private String formatCat(
            Cat cat
    ) {

        ArrayList<String> abilities =
                cat.getAbilities();


        String abilityText = "";


        for (int i = 0;
             i < abilities.size();
             i++) {

            abilityText =
                    abilityText
                            + abilities.get(i);


            if (i < abilities.size() - 1) {

                abilityText =
                        abilityText + ", ";
            }
        }


        return "Type: "
                + cat.getType()
                + "\nHP: "
                + cat.getMaxHp()
                + "/"
                + cat.getMaxHp()
                + "\nAbilities: "
                + abilityText;
    }


    /**
     * Returns whichever starter the player selected.
     */
    private Cat getSelectedCat() {

        if (catOneButton.isSelected()) {

            return catOne;
        }


        if (catTwoButton.isSelected()) {

            return catTwo;
        }


        if (catThreeButton.isSelected()) {

            return catThree;
        }


        return null;
    }

    /**
     * Saves the selected starter cat.
     */
    @FXML
    private void handleChooseStarter() {

        statusLabel.setText("");


        Cat selectedCat =
                getSelectedCat();


        if (selectedCat == null) {

            statusLabel.setText(
                    "Please choose a cat."
            );

            return;
        }


        String catName =
                catNameField
                        .getText()
                        .trim();


        if (catName.isEmpty()) {

            statusLabel.setText(
                    "Please give your cat a name."
            );

            return;
        }


        /*
         * The | character is used to separate
         * fields in Cat.toStorageString().
         */
        if (catName.contains("|")) {

            statusLabel.setText(
                    "Cat names cannot contain |"
            );

            return;
        }


        /*
         * Convert the generated opponent-style
         * cat into the player's starter.
         */
        selectedCat.setName(
                catName
        );

        selectedCat.setPlayerCat(
                true
        );

        selectedCat.setInParty(
                true
        );

        selectedCat.setCurrentHp(
                selectedCat.getMaxHp()
        );


        try {

            /*
             * Save the selected cat.
             */
            Cat savedCat =
                    catDAO.insert(
                            selectedCat,
                            player.getPlayerId()
                    );


            /*
             * Make this cat the player's
             * active battle cat.
             */
            player.setActiveCatId(
                    savedCat.getId()
            );


            /*
             * Save the new active_cat_id
             * to the player table.
             */
            playerDAO.update(
                    player
            );


            /*
             * Starter setup is finished.
             */
            SceneFactory.show(
                    SceneType.MAIN
            );


        } catch (SQLException exception) {

            exception.printStackTrace();

            statusLabel.setText(
                    "Could not save your starter."
            );


        } catch (RuntimeException exception) {

            exception.printStackTrace();

            statusLabel.setText(
                    "Could not save your starter."
            );
        }
    }
}