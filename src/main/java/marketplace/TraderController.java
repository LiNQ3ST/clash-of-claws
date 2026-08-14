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
 * Issue #12 adds reusable notifications and TestFX-friendly control IDs.
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

    @FXML
    private void restockTrader() {

        try {
            traderItemDAO.restockDefaultItems();

            refreshItems();

            TraderNotification.success(
                    "Trader Restocked",
                    "The trader has received new supplies!",
                    "Potions and catching items have been restored to their starting stock."
            );

        } catch (SQLException exception) {

            TraderNotification.error(
                    "Restock Failed",
                    "The trader's stock could not be restored.",
                    exception.getMessage()
            );
        }
    }
    private void refreshItems() {
        try {
            traderItemsTable.setItems(FXCollections.observableArrayList(
                    traderItemDAO.findAllAvailableItems()
            ));
            clearItemDetails();
        } catch (SQLException exception) {
            TraderNotification.error(
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

            TraderNotification.error(
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
            TraderNotification.warning(
                    "Login Required",
                    "Log in before buying an item.",
                    null
            );
            return;
        }

        TraderItem selectedItem =
                traderItemsTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            TraderNotification.warning(
                    "No Item Selected",
                    "Select an item first.",
                    "Choose a potion or catching item from the table."
            );
            return;
        }

        Integer quantity = readRequestedQuantity(selectedItem);
        if (quantity == null) {
            return;
        }

        try {
            TraderService.PurchaseResult result = traderService.purchaseItem(
                    currentPlayer.getPlayerId(),
                    selectedItem.getItemId(),
                    quantity
            );

            currentPlayer.setCurrencyBalance(result.newBalance());

            TraderNotification.success(
                    "Purchase Complete",
                    "Item purchased successfully.",
                    quantity + " x " + selectedItem.getItemName()
                            + " purchased for " + result.totalCost() + " coins."
            );

            refreshAll();
        } catch (IllegalArgumentException exception) {
            TraderNotification.warning(
                    "Invalid Quantity",
                    "The quantity is not valid.",
                    exception.getMessage()
            );
        } catch (IllegalStateException exception) {
            showPurchaseRuleAlert(exception.getMessage());
        } catch (SQLException exception) {
            TraderNotification.error(
                    "Database Error",
                    "The purchase could not be saved.",
                    exception.getMessage()
            );
        }
    }

    private Integer readRequestedQuantity(TraderItem selectedItem) {
        String text = quantitySpinner.getEditor().getText();

        final int quantity;
        try {
            quantity = Integer.parseInt(text.trim());
        } catch (NumberFormatException exception) {
            TraderNotification.warning(
                    "Invalid Quantity",
                    "Enter a whole number greater than zero.",
                    "Quantity must be a number."
            );
            return null;
        }

        if (quantity <= 0) {
            TraderNotification.warning(
                    "Invalid Quantity",
                    "Enter a whole number greater than zero.",
                    "Quantity must be greater than zero."
            );
            return null;
        }

        if (quantity > selectedItem.getStockQuantity()) {
            TraderNotification.warning(
                    "Insufficient Stock",
                    "The trader does not have enough stock.",
                    "Available: " + selectedItem.getStockQuantity()
            );
            return null;
        }

        return quantity;
    }

    private void showPurchaseRuleAlert(String message) {
        String safeMessage = message == null ? "The purchase could not be completed." : message;
        String lower = safeMessage.toLowerCase();

        if (lower.contains("insufficient coins")) {
            TraderNotification.warning(
                    "Insufficient Coins",
                    "You do not have enough coins.",
                    safeMessage
            );
            return;
        }

        if (lower.contains("not enough stock")) {
            TraderNotification.warning(
                    "Insufficient Stock",
                    "The trader does not have enough stock.",
                    safeMessage
            );
            return;
        }

        TraderNotification.warning(
                "Purchase Failed",
                "The purchase could not be completed.",
                safeMessage
        );
    }

    @FXML
    private void sellCreature() {
        if (!hasLoggedInPlayer()) {
            TraderNotification.warning(
                    "Login Required",
                    "Log in before selling a creature.",
                    null
            );
            return;
        }

        Cat selectedCat = creatureListView.getSelectionModel().getSelectedItem();

        if (selectedCat == null) {
            TraderNotification.warning(
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

            TraderNotification.success(
                    "Creature Sold",
                    selectedCat.getName() + " was sold successfully.",
                    "You received " + result.saleValue() + " coins."
            );

            refreshAll();
        } catch (IllegalArgumentException exception) {
            TraderNotification.warning(
                    "Sale Failed",
                    "The creature could not be sold.",
                    exception.getMessage()
            );
        } catch (IllegalStateException exception) {
            showSaleRuleAlert(exception.getMessage());
        } catch (SQLException exception) {
            TraderNotification.error(
                    "Database Error",
                    "The creature sale could not be saved.",
                    exception.getMessage()
            );
        }
    }

    private void showSaleRuleAlert(String message) {
        String safeMessage = message == null ? "The creature could not be sold." : message;
        String lower = safeMessage.toLowerCase();

        if (lower.contains("active creature")) {
            TraderNotification.warning(
                    "Active Creature",
                    "The active creature cannot be sold.",
                    safeMessage
            );
            return;
        }

        if (lower.contains("do not own")
                || lower.contains("not owned")
                || lower.contains("not owned by the player")) {
            TraderNotification.error(
                    "Creature Not Owned",
                    "You cannot sell this creature.",
                    safeMessage
            );
            return;
        }

        TraderNotification.warning(
                "Sale Failed",
                "The creature could not be sold.",
                safeMessage
        );
    }

    private boolean hasLoggedInPlayer() {
        return currentPlayer != null && currentPlayer.getPlayerId() != null;
    }

    @FXML
    private void returnToMainMenu() {
        SceneFactory.showMainScene();
    }
}




