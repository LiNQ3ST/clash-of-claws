package marketplace;

import account.AccountService;
import account.Player;
import app.SceneFactory;
import creature.Cat;
import creature.CatDAO;
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
import java.util.ArrayList;

/**
 * Controller for the Trader scene.
 *
 * Issue #11 connects the existing Issue #10 UI to TraderService so that
 * purchases and creature sales now change the database.
 */
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
    private ListView<Cat> creatureListView;

    @FXML
    private Label creatureNameLabel;

    @FXML
    private Label creatureTypeLabel;

    @FXML
    private Label creatureHpLabel;

    @FXML
    private Label creatureSaleValueLabel;

    @FXML
    private Button sellCreatureButton;

    private final TraderItemDAO traderItemDAO = new TraderItemDAO();
    private final TraderService traderService = new TraderService();
    private final CatDAO catDAO = new CatDAO();
    private final AccountService accountService = AccountService.getInstance();

    private Player currentPlayer;

    @FXML
    private void initialize() {
        configureItemTable();
        configureQuantitySpinner();
        configureSelections();
        refreshAll();
    }

    private void configureItemTable() {
        itemNameColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getItemName()));

        itemTypeColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getItemType()));

        priceColumn.setCellValueFactory(data ->
                new ReadOnlyIntegerWrapper(data.getValue().getPrice()).asObject());

        stockColumn.setCellValueFactory(data ->
                new ReadOnlyIntegerWrapper(data.getValue().getStockQuantity()).asObject());
    }

    private void configureQuantitySpinner() {
        quantitySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1)
        );
    }

    private void configureSelections() {
        traderItemsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldItem, newItem) -> showItemDetails(newItem));

        creatureListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldCat, newCat) -> showCreatureDetails(newCat));
    }

    @FXML
    private void refreshAll() {
        refreshItems();
        refreshPlayerAndCreatures();
    }

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

    private void refreshPlayerAndCreatures() {
        currentPlayer = accountService.getCurrentPlayer().orElse(null);

        if (currentPlayer == null || currentPlayer.getPlayerId() == null) {
            coinBalanceLabel.setText("Your Coins: --");
            creatureListView.setItems(FXCollections.observableArrayList());
            creatureListView.setPlaceholder(new Label("Log in to view your creatures."));
            sellCreatureButton.setDisable(true);
            clearCreatureDetails();
            return;
        }

        coinBalanceLabel.setText("Your Coins: " + currentPlayer.getCurrencyBalance());

        try {
            ArrayList<Cat> cats = catDAO.findAll(currentPlayer.getPlayerId());
            ArrayList<Cat> ownedCats = new ArrayList<>();

            for (Cat cat : cats) {
                if (cat.isPlayerCat()) {
                    ownedCats.add(cat);
                }
            }

            creatureListView.setItems(FXCollections.observableArrayList(ownedCats));
            creatureListView.setPlaceholder(
                    new Label("You do not have any owned creatures yet.")
            );
            sellCreatureButton.setDisable(true);
            clearCreatureDetails();

        } catch (RuntimeException exception) {
            creatureListView.setItems(FXCollections.observableArrayList());
            creatureListView.setPlaceholder(new Label("Creatures could not be loaded."));
            sellCreatureButton.setDisable(true);
            clearCreatureDetails();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Creature Error",
                    "Your creatures could not be loaded.",
                    exception.getMessage()
            );
        }
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

        int maximum = Math.max(1, item.getStockQuantity());
        quantitySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maximum, 1)
        );
    }

    private void clearItemDetails() {
        selectedNameLabel.setText("-");
        selectedTypeLabel.setText("-");
        selectedDescriptionLabel.setText("Select an item from the table.");
        selectedPriceLabel.setText("-");
        selectedStockLabel.setText("-");
    }

    private void showCreatureDetails(Cat cat) {
        if (cat == null) {
            clearCreatureDetails();
            sellCreatureButton.setDisable(true);
            return;
        }

        creatureNameLabel.setText(cat.getName());
        creatureTypeLabel.setText(cat.getType());
        creatureHpLabel.setText(cat.getCurrentHp() + "/" + cat.getMaxHp());
        creatureSaleValueLabel.setText(
                traderService.calculateSaleValue(cat) + " coins"
        );
        sellCreatureButton.setDisable(false);
    }

    private void clearCreatureDetails() {
        creatureNameLabel.setText("-");
        creatureTypeLabel.setText("-");
        creatureHpLabel.setText("-");
        creatureSaleValueLabel.setText("-");
    }

    @FXML
    private void buyItem() {
        if (!hasLoggedInPlayer()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Login Required",
                    "Log in before buying an item.",
                    null
            );
            return;
        }

        TraderItem selectedItem =
                traderItemsTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "No Item Selected",
                    "Select an item first.",
                    "Choose a potion or catching item from the table."
            );
            return;
        }

        int quantity = quantitySpinner.getValue();

        try {
            TraderService.PurchaseResult result = traderService.purchaseItem(
                    currentPlayer.getPlayerId(),
                    selectedItem.getItemId(),
                    quantity
            );

            currentPlayer.setCurrencyBalance(result.newBalance());

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Purchase Complete",
                    "Item purchased successfully.",
                    quantity + " x " + selectedItem.getItemName()
                            + " purchased for " + result.totalCost() + " coins."
            );

            refreshAll();

        } catch (IllegalArgumentException | IllegalStateException exception) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Purchase Failed",
                    "The purchase could not be completed.",
                    exception.getMessage()
            );

        } catch (SQLException exception) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "The purchase could not be saved.",
                    exception.getMessage()
            );
        }
    }

    @FXML
    private void sellCreature() {
        if (!hasLoggedInPlayer()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Login Required",
                    "Log in before selling a creature.",
                    null
            );
            return;
        }

        Cat selectedCat = creatureListView.getSelectionModel().getSelectedItem();

        if (selectedCat == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "No Creature Selected",
                    "Select a creature first.",
                    null
            );
            return;
        }

        try {
            TraderService.SaleResult result = traderService.sellCreature(
                    currentPlayer.getPlayerId(),
                    selectedCat.getId()
            );

            currentPlayer.setCurrencyBalance(result.newBalance());

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Creature Sold",
                    selectedCat.getName() + " was sold successfully.",
                    "You received " + result.saleValue() + " coins."
            );

            refreshAll();

        } catch (IllegalArgumentException | IllegalStateException exception) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Sale Failed",
                    "The creature could not be sold.",
                    exception.getMessage()
            );

        } catch (SQLException exception) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "The creature sale could not be saved.",
                    exception.getMessage()
            );
        }
    }

    private boolean hasLoggedInPlayer() {
        return currentPlayer != null && currentPlayer.getPlayerId() != null;
    }

    @FXML
    private void returnToMainMenu() {
        SceneFactory.showMainScene();
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



