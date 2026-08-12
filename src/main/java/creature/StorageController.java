package creature;

import account.AccountService;
import account.Player;
import app.SceneFactory;
import app.SceneType;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;


public class StorageController {

    @FXML
    private ListView<Cat> partyListView;

    @FXML
    private ListView<Cat> storageListView;

    @FXML
    private Label partyCountLabel;

    @FXML
    private Label storageCountLabel;

    @FXML
    private Label statusLabel;


    /*
     * These are the lists that the UI watches.
     */
    private ObservableList<Cat> partyCats;

    private ObservableList<Cat> storedCats;

    private static final int MAX_PARTY_SIZE = 4;

    private CatDAO catDAO;

    private Player player;


    @FXML
    private void initialize() {

        /*
         * Create the observable lists.
         */
        partyCats =
                FXCollections.observableArrayList();

        storedCats =
                FXCollections.observableArrayList();


        /*
         * Connect each ListView to its list.
         *
         * We only need to do this once.
         */
        partyListView.setItems(
                partyCats
        );

        storageListView.setItems(
                storedCats
        );


        /*
         * Bind the labels to the list sizes.
         *
         * When a cat is added or removed,
         * these labels update automatically.
         */
        partyCountLabel
                .textProperty()
                .bind(
                        Bindings.concat(
                                "Party: ",
                                Bindings.size(partyCats),
                                " / " ,
                                MAX_PARTY_SIZE
                        )
                );


        storageCountLabel
                .textProperty()
                .bind(
                        Bindings.concat(
                                "Storage: ",
                                Bindings.size(storedCats)
                        )
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


        loadCats();
    }


    /**
     * Loads the party and storage cats
     * from the database.
     */
    private void loadCats() {

        int playerId =
                player.getPlayerId();


        /*
         * setAll replaces everything currently
         * in the ObservableList.
         *
         * Because the ListViews watch these lists,
         * the screen updates automatically.
         */
        partyCats.setAll(
                catDAO.findPartyCats(
                        playerId
                )
        );


        storedCats.setAll(
                catDAO.findStoredCats(
                        playerId
                )
        );
    }




    /**
     * Moves a stored cat into
     * an empty party slot.
     */
    @FXML
    private void handleMoveToParty() {

        Cat storedCat =
                storageListView
                        .getSelectionModel()
                        .getSelectedItem();


        if (storedCat == null) {

            statusLabel.setText(
                    "Select a stored cat first."
            );

            return;
        }


        /*
         * We can use the size of our
         * ObservableList here.
         */
        if (partyCats.size() >= MAX_PARTY_SIZE) {

            statusLabel.setText(
                    "The party is already full."
            );

            return;
        }


        boolean moved =
                catDAO.moveToParty(
                        storedCat,
                        player.getPlayerId()
                );


        if (moved) {

            /*
             * Move the Cat between the two
             * observable lists.
             *
             * The ListViews and count labels
             * will update automatically.
             */
            storedCats.remove(
                    storedCat
            );

            partyCats.add(
                    storedCat
            );


            statusLabel.setText(
                    storedCat.getName()
                            + " joined the party."
            );

        } else {

            statusLabel.setText(
                    "The cat could not be moved."
            );
        }
    }


    /**
     * Moves a party cat
     * into storage.
     */
    @FXML
    private void handleMoveToStorage() {

        Cat partyCat =
                partyListView
                        .getSelectionModel()
                        .getSelectedItem();


        if (partyCat == null) {

            statusLabel.setText(
                    "Select a party cat first."
            );

            return;
        }


        if (isActiveCat(partyCat)) {

            statusLabel.setText(
                    "The active cat cannot be moved to storage."
            );

            return;
        }


        boolean moved =
                catDAO.moveToStorage(
                        partyCat,
                        player.getPlayerId()
                );


        if (moved) {

            /*
             * Move the Cat between
             * the observable lists.
             */
            partyCats.remove(
                    partyCat
            );

            storedCats.add(
                    partyCat
            );


            statusLabel.setText(
                    partyCat.getName()
                            + " was moved to storage."
            );

        } else {

            statusLabel.setText(
                    "The cat could not be moved."
            );
        }
    }


    /**
     * Checks whether the supplied Cat
     * is the player's active cat.
     */
    private boolean isActiveCat(
            Cat cat
    ) {

        Integer activeCatId =
                player.getActiveCatId();


        if (activeCatId == null) {

            return false;
        }


        return activeCatId.equals(
                cat.getId()
        );
    }


    /**
     * Opens the Party screen.
     */
    @FXML
    private void handleParty() {

        SceneFactory.show(
                SceneType.PARTY
        );
    }


    /**
     * Returns to the main menu.
     */
    @FXML
    private void handleBack() {

        SceneFactory.show(
                SceneType.MAIN
        );
    }
}