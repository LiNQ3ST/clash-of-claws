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
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;




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

        setupCatListView(
            partyListView
        );

        setupCatListView(
            storageListView
        );


        partyListView.setFixedCellSize(
            100
        );

        storageListView.setFixedCellSize(
            100
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
                                " / 4"
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
         * Do not allow the active cat
         * to be placed into storage.
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

            /*
             * The DAO already saved the changes
             * to the database.
             *
             * Now update our ObservableLists.
             */
            partyCats.remove(
                    partyCat
            );

            storedCats.remove(
                    storedCat
            );


            partyCats.add(
                    storedCat
            );

            storedCats.add(
                    partyCat
            );


            statusLabel.setText(
                    partyCat.getName()
                            + " was swapped with "
                            + storedCat.getName()
                            + "."
            );

        } else {

            statusLabel.setText(
                    "The cats could not be swapped."
            );
        }
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
        if (partyCats.size() >= 4) {

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
                                48
                            );

                            catImage.setFitHeight(
                                48
                            );


                            CatSpriteRenderer.setSprite(
                                catImage,
                                cat,
                                CatSpriteRenderer.IDLE
                            );


                            Label catName =
                                new Label(
                                    cat.getName()
                                );


                            HBox row =
                                new HBox(
                                    10,
                                    catImage,
                                    catName
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