package creature;

import account.AccountService;
import account.Player;
import app.SceneFactory;
import app.SceneType;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class CatDexController {

    @FXML
    private ListView<String> catListView;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleBack() {
        SceneFactory.show(SceneType.MAIN);
    }

    @FXML
    private void initialize() {

        Player player =
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


        CatDAO catDAO =
                new CatDAO();


        ArrayList<Cat> cats =
                catDAO.findAll(
                        playerId
                );


        for (Cat cat : cats) {

            catListView
                    .getItems()
                    .add(
                            cat.toString()
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
}