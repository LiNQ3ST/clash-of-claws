package creature;

import account.AccountService;
import account.Player;
import account.PlayerDAO;
import app.SceneFactory;
import app.SceneType;

import java.sql.SQLException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;


public class PartyController {

    @FXML
    private ListView<Cat> partyListView;

    @FXML
    private Label partyCountLabel;

    @FXML
    private Label statusLabel;


    private CatDAO catDAO;

    private Player player;


    @FXML
    private void initialize() {

        /*
         * Set up how each Cat looks
         * inside the Party ListView.
         */
        setupCatListView(
            partyListView
        );


        /*
         * Give each cat enough vertical
         * space for its sprite and info.
         */
        partyListView.setFixedCellSize(
            100
        );


        catDAO =
            new CatDAO();


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

            return;
        }


        loadParty();
    }


    /**
     * Loads the player's current party
     * from the database.
     */
    private void loadParty() {

        partyListView
            .getItems()
            .clear();


        ArrayList<Cat> partyCats =
            catDAO.findPartyCats(
                player.getPlayerId()
            );


        for (Cat cat : partyCats) {

            partyListView
                .getItems()
                .add(
                    cat
                );
        }


        partyCountLabel.setText(
            "Party: "
                + partyCats.size()
                + " / 4"
        );


        if (partyCats.isEmpty()) {

            statusLabel.setText(
                "Your party is empty."
            );

        } else {

            statusLabel.setText(
                "Select Manage Storage to change your party."
            );
        }
    }


    /**
     * Controls how each Cat appears
     * inside the Party ListView.
     */
    private void setupCatListView(
        ListView<Cat> listView
    ) {

        listView.setCellFactory(
            new Callback<ListView<Cat>, ListCell<Cat>>() {

                @Override
                public ListCell<Cat> call(
                    ListView<Cat> catList
                ) {

                    return new ListCell<Cat>() {

                        @Override
                        protected void updateItem(
                            Cat cat,
                            boolean empty
                        ) {

                            super.updateItem(
                                cat,
                                empty
                            );


                            /*
                             * Empty ListView rows should
                             * display nothing.
                             */
                            if (empty
                                || cat == null) {

                                setText(null);
                                setGraphic(null);

                                return;
                            }


                            /*
                             * Cat sprite.
                             */
                            ImageView catImage =
                                new ImageView();


                            catImage.setFitWidth(
                                72
                            );

                            catImage.setFitHeight(
                                72
                            );


                            CatSpriteRenderer.setSprite(
                                catImage,
                                cat,
                                CatSpriteRenderer.IDLE
                            );


                            /*
                             * Cat name.
                             */
                            Label nameLabel =
                                new Label(
                                    cat.getName()
                                );


                            nameLabel.setStyle(
                                "-fx-font-size: 16px;"
                                    + "-fx-font-weight: bold;"
                            );


                            /*
                             * Cat type.
                             */
                            Label typeLabel =
                                new Label(
                                    cat.getType()
                                );


                            /*
                             * Current and maximum HP.
                             */
                            Label hpLabel =
                                new Label(
                                    "HP: "
                                        + cat.getCurrentHp()
                                        + " / "
                                        + cat.getMaxHp()
                                );


                            /*
                             * Ability list.
                             */
                            Label abilityLabel =
                                new Label(
                                    "Abilities: "
                                        + String.join(
                                        ", ",
                                        cat.getAbilities()
                                    )
                                );


                            abilityLabel.setWrapText(
                                true
                            );


                            /*
                             * Stack all the cat information
                             * vertically.
                             */
                            VBox information =
                                new VBox(
                                    3,
                                    nameLabel,
                                    typeLabel,
                                    hpLabel,
                                    abilityLabel
                                );


                            /*
                             * Sprite on the left,
                             * information on the right.
                             */
                            HBox row =
                                new HBox(
                                    15,
                                    catImage,
                                    information
                                );


                            row.setAlignment(
                                Pos.CENTER_LEFT
                            );


                            row.setPadding(
                                new Insets(
                                    6
                                )
                            );


                            setText(null);

                            setGraphic(
                                row
                            );
                        }
                    };
                }
            }
        );
    }


    @FXML
    private void handleStorage() {

        SceneFactory.show(
            SceneType.STORAGE
        );
    }


    @FXML
    private void handleBack() {

        SceneFactory.show(
            SceneType.MAIN
        );
    }

    @FXML
    private void handleMakeActive() {

        Cat selectedCat =
                partyListView
                        .getSelectionModel()
                        .getSelectedItem();

        if (selectedCat == null) {
            statusLabel.setText(
                    "Select a cat to make active."
            );
            return;
        }

        player.setActiveCatId(
                selectedCat.getId()
        );

        try {
            boolean updated =
                    new PlayerDAO().update(player);

            if (updated) {
                statusLabel.setText(
                        selectedCat.getName()
                                + " is now your active cat!"
                );
            } else {
                statusLabel.setText(
                        "Could not update the active cat."
                );
            }

        } catch (SQLException exception) {
            statusLabel.setText(
                    "Could not update the active cat."
            );
        }
    }

    @FXML
    private void handleHealParty() {

        ArrayList<Cat> partyCats =
                catDAO.findPartyCats(
                        player.getPlayerId()
                );

        if (partyCats.isEmpty()) {
            statusLabel.setText(
                    "Your party is empty."
            );
            return;
        }

        boolean healedAny = false;

        for (Cat cat : partyCats) {

            if (cat.getCurrentHp() < cat.getMaxHp()) {

                cat.setCurrentHp(
                        cat.getMaxHp()
                );

                catDAO.update(
                        cat,
                        player.getPlayerId()
                );

                healedAny = true;
            }
        }

        if (healedAny) {
            statusLabel.setText(
                    "Your party is fully healed!"
            );
        } else {
            statusLabel.setText(
                    "Your party is already at full health."
            );
        }

        loadParty();

        if (healedAny) {
            statusLabel.setText(
                    "Your party is fully healed!"
            );
        } else {
            statusLabel.setText(
                    "Your party is already at full health."
            );
        }
    }
}