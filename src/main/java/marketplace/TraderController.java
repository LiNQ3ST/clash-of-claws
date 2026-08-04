package marketplace;

import app.SceneFactory;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.SQLException;

public class TraderController {

    @FXML
    private Label coinBalanceLabel;

    @FXML
    private TableView<TraderItem> traderItemsTable;

    @FXML
    private TableColumn<TraderItem, String> itemNameColumn;

    @FXML
    private TableColumn<TraderItem, String> itemTypeColumn;

    @FXML
    private TableColumn<TraderItem, Integer> priceColumn;

    @FXML
    private TableColumn<TraderItem, Integer> stockColumn;

    @FXML
    private Label selectedNameLabel;

    @FXML
    private Label selectedTypeLabel;

    @FXML
    private Label selectedDescriptionLabel;

    @FXML
    private Label selectedPriceLabel;

    @FXML
    private Label selectedStockLabel;

    @FXML
    private Spinner<Integer> quantitySpinner;

    @FXML
    private ListView<String> creatureListView;

    @FXML
    private Button sellCreatureButton;

    private final TraderItemDAO traderItemDAO = new TraderItemDAO();

    @FXML
    private void initialize() {
        itemNameColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getItemName()));
        itemTypeColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getItemType()));
        priceColumn.setCellValueFactory(data ->
                new ReadOnlyIntegerWrapper(data.getValue().getPrice()).asObject());
        stockColumn.setCellValueFactory(data ->
                new ReadOnlyIntegerWrapper(data.getValue().getStockQuantity()).asObject());

        quantitySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1)
        );

        traderItemsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldItem, newItem) -> showItemDetails(newItem));

        coinBalanceLabel.setText("Your Coins: -- (Accounts integration pending)");
        creatureListView.setPlaceholder(
                new Label("Creature roster integration is planned for Milestone 2.")
        );
        sellCreatureButton.setDisable(true);

        refreshItems();
    }

    @FXML
    private void refreshItems() {
        try {
            traderItemsTable.setItems(FXCollections.observableArrayList(
                    traderItemDAO.findAllAvailableItems()
            ));
            clearItemDetails();
        } catch (SQLException exception) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "The trader items could not be loaded.",
                    exception.getMessage()
            );
        }
    }

    @FXML
    private void buyItem() {
        TraderItem selectedItem = traderItemsTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "No Item Selected",
                    "Select an item before clicking Buy Item.",
                    null
            );
            return;
        }

        int quantity = quantitySpinner.getValue();
        showAlert(
                Alert.AlertType.INFORMATION,
                "Milestone 1 Trader",
                "Trader scene and database connection are working.",
                "Selected " + quantity + " x " + selectedItem.getItemName()
                        + ". Purchase logic will be implemented in Milestone 2."
        );
    }

    @FXML
    private void returnToMainMenu() {
        SceneFactory.showMainScene();
    }

    private void showItemDetails(TraderItem item) {
        if (item == null) {
            clearItemDetails();
            return;
        }

        selectedNameLabel.setText(item.getItemName());
        selectedTypeLabel.setText(item.getItemType());
        selectedDescriptionLabel.setText(item.getDescription());
        selectedPriceLabel.setText(item.getPrice() + " coins");
        selectedStockLabel.setText(String.valueOf(item.getStockQuantity()));
    }

    private void clearItemDetails() {
        selectedNameLabel.setText("-");
        selectedTypeLabel.setText("-");
        selectedDescriptionLabel.setText("Select an item from the table.");
        selectedPriceLabel.setText("-");
        selectedStockLabel.setText("-");
    }

    private static void showAlert(
            Alert.AlertType alertType,
            String title,
            String header,
            String content
    ) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

