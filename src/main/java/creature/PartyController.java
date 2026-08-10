package creature;

import account.AccountService;
import account.Player;
import app.SceneFactory;
import app.SceneType;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

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
                    .add(cat);
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
}