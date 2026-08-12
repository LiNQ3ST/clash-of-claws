package creature;

import account.AccountService;
import account.Player;
import app.SceneFactory;
import app.SceneType;


import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    private ObservableList<Cat> partyCats; // data binding for partycats
    private ObservableList<Cat> storedCats; // data binding for storagecat

    private CatDAO catDAO;

    private Player player;


    @FXML
    private void initialize() {

        partyCats =
                FXCollections.observableArrayList();

        storedCats =
                FXCollections.observableArrayList();

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
     * Reloads both lists from the database.
     */
    private void loadCats() {

        partyListView
                .getItems()
                .clear();

        storageListView
                .getItems()
                .clear();


        int playerId =
                player.getPlayerId();


        ArrayList<Cat> partyCats =
                catDAO.findPartyCats(
                        playerId
                );


        ArrayList<Cat> storedCats =
                catDAO.findStoredCats(
                        playerId
                );


        for (Cat cat : partyCats) {

            partyListView
                    .getItems()
                    .add(cat);
        }


        for (Cat cat : storedCats) {

            storageListView
                    .getItems()
                    .add(cat);
        }


        partyCountLabel.setText(
                "Party: "
                        + partyCats.size()
                        + " / 4"
        );


        storageCountLabel.setText(
                "Storage: "
                        + storedCats.size()
        );
    }


    /**
     * Swaps one selected party cat
     * with one selected stored cat.
     */
    @FXML
    private void handleSwap() {

        Cat partyCat =
                partyListView
                        .getSelectionModel()
                        .getSelectedItem();


        Cat storedCat =
                storageListView
                        .getSelectionModel()
                        .getSelectedItem();


        if (partyCat == null
                || storedCat == null) {

            statusLabel.setText(
                    "Select one party cat and one stored cat."
            );

            return;
        }


        /*
         * Until active-cat behavior is agreed on with
         * the account/battle system, don't allow the
         * active cat to be placed into storage.
         */
        if (isActiveCat(partyCat)) {

            statusLabel.setText(
                    "The active cat cannot be moved to storage."
            );

            return;
        }


        boolean swapped =
                catDAO.swapCats(
                        partyCat,
                        storedCat,
                        player.getPlayerId()
                );


        if (swapped) {

            statusLabel.setText(
                    partyCat.getName()
                            + " was swapped with "
                            + storedCat.getName()
                            + "."
            );

            loadCats();

        } else {

            statusLabel.setText(
                    "The cats could not be swapped."
            );
        }
    }


    /**
     * Moves a stored cat into an empty party slot.
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


        if (catDAO.countPartyCats(
                player.getPlayerId()
        ) >= 4) {

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

            statusLabel.setText(
                    storedCat.getName()
                            + " joined the party."
            );

            loadCats();

        } else {

            statusLabel.setText(
                    "The cat could not be moved."
            );
        }
    }


    /**
     * Moves a party cat into storage.
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

            statusLabel.setText(
                    partyCat.getName()
                            + " was moved to storage."
            );

            loadCats();

        } else {

            statusLabel.setText(
                    "The cat could not be moved."
            );
        }
    }


    /**
     * Checks whether this is the player's
     * currently selected active cat.
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


    @FXML
    private void handleParty() {

        SceneFactory.show(
                SceneType.PARTY
        );
    }


    @FXML
    private void handleBack() {

        SceneFactory.show(
                SceneType.MAIN
        );
    }
}