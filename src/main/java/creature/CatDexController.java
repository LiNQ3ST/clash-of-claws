package creature;

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
    private void initialize() {

        CatDAO catDAO = new CatDAO();

        // Creates the table only if it does not already exist.
        catDAO.initializeTable();

        // Reads every cat from the database.
        ArrayList<Cat> cats = catDAO.findAll();

        // Adds each cat to the visible list.
        for (Cat cat : cats) {
            catListView.getItems().add(cat.toString());        }

        statusLabel.setText("Cats stored: " + cats.size());

        // Temporary console check.
        System.out.println("Cats loaded: " + cats.size());
    }
}