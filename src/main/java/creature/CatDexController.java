package creature;

import account.AccountService;
import account.Player;
import app.SceneFactory;
import app.SceneType;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;


public class CatDexController {

    @FXML
    private ListView<Cat> catListView;

    @FXML
    private Label statusLabel;

    private CatDAO catDAO;

    private Player player;


    @FXML
    private void handleBack() {

        SceneFactory.show(
            SceneType.MAIN
        );
    }


    @FXML
    private void initialize() {

        /*
         * Tells the CatDex how each Cat
         * should look in the ListView.
         */
        setupCatListView(
            catListView
        );

        catListView.setFixedCellSize(
            100
        );



      player =
            AccountService
                .getInstance()
                .getCurrentPlayer()
                .orElse(null);


        if (player == null
            || player.getPlayerId() == null) {

            statusLabel.setText(
                "No player is logged in"
            );

            return;
        }


        int playerId =
            player.getPlayerId();


        catDAO =
            new CatDAO();


        ArrayList<Cat> cats =
            catDAO.findAll(
                playerId
            );


        /*
         * We now add the actual Cat object
         * instead of cat.toString().
         */
        for (Cat cat : cats) {

            catListView
                .getItems()
                .add(
                    cat
                );
        }


        statusLabel.setText(
            "Cats stored: "
                + cats.size()
        );


        System.out.println(
            "Cats loaded: "
                + cats.size()
        );
    }
    @FXML
    private void handleResetCatDex() {

        if (player == null
            || player.getPlayerId() == null) {

            statusLabel.setText(
                "No player is logged in."
            );

            return;
        }


        Alert confirmation =
            new Alert(
                Alert.AlertType.CONFIRMATION
            );


        confirmation.setTitle(
            "Reset CatDex"
        );

        confirmation.setHeaderText(
            "Start your CatDex over?"
        );

        confirmation.setContentText(
            "This will permanently remove all cats "
                + "from your CatDex, Party, and Storage. "
                + "You will then choose a new starter."
        );


        ButtonType result =
            confirmation
                .showAndWait()
                .orElse(
                    ButtonType.CANCEL
                );


        if (result != ButtonType.OK) {

            return;
        }


        boolean deleted =
            catDAO.deleteAllForPlayer(
                player.getPlayerId()
            );


        if (deleted) {

            catListView
                .getItems()
                .clear();


            SceneFactory.show(
                SceneType.STARTER
            );

        } else {

            statusLabel.setText(
                "CatDex could not be reset."
            );
        }
    }


    /**
     * Makes each CatDex row display
     * the cat's sprite and name.
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


                            if (empty || cat == null) {

                                setText(null);
                                setGraphic(null);

                                return;
                            }


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


                            Label nameLabel =
                                new Label(
                                    cat.getName()
                                );

                            nameLabel.setStyle(
                                "-fx-font-size: 16px;"
                                    + "-fx-font-weight: bold;"
                            );


                            String ownershipText;

                            if (cat.isPlayerCat()) {

                                if (cat.isInParty()) {

                                    ownershipText =
                                        "Party Cat";

                                } else {

                                    ownershipText =
                                        "Stored Cat";
                                }

                            } else {

                                ownershipText =
                                    "Encountered Cat";
                            }


                            Label typeLabel =
                                new Label(
                                    cat.getType()
                                        + "  •  "
                                        + ownershipText
                                );


                            Label hpLabel =
                                new Label(
                                    "HP: "
                                        + cat.getCurrentHp()
                                        + " / "
                                        + cat.getMaxHp()
                                );


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


                            VBox information =
                                new VBox(
                                    4,
                                    nameLabel,
                                    typeLabel,
                                    hpLabel,
                                    abilityLabel
                                );


                            HBox row =
                                new HBox(
                                    15,
                                    catImage,
                                    information
                                );

                            row.setPadding(
                                new Insets(
                                    8
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

}