package marketplace;

import account.AccountService;
import account.Player;
import account.PlayerDAO;
import app.SceneFactory;
import app.SceneType;
import creature.Cat;
import creature.CatDAO;
import database.DatabaseManager;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import org.junit.jupiter.api.Tag;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestFX coverage for Marketplace & Trading Issue #12.
 *
 * Each test uses its own temporary SQLite database so the UI tests do not
 * modify clash-of-claws.db.
 */
@Tag("testfx")
@ExtendWith(ApplicationExtension.class)
class TraderSceneTest {

    private static final String PASSWORD = "test123";

    private Path testDatabaseFile;
    private Player currentPlayer;
    private TraderItem testItem;
    private Cat testCat;

    @Start
    void start(Stage stage) throws Exception {
        configureTemporaryDatabase();
        seedTestData();

        SceneFactory.initialize(stage);
        SceneFactory.show(SceneType.MAIN);
    }

    @AfterEach
    void cleanUp() throws IOException {
        AccountService.getInstance().logout();
        System.clearProperty(DatabaseManager.DATABASE_URL_PROPERTY);

        if (testDatabaseFile != null) {
            Files.deleteIfExists(testDatabaseFile);
        }
    }

    @Test
    void traderSceneOpensFromMainMenu(FxRobot robot) {
        openTrader(robot);

        assertNotNull(
                robot.lookup("#traderItemsTable").query()
        );
        assertNotNull(
                robot.lookup("#creatureSellingSection").query()
        );
    }

    @Test
    void selectingItemUpdatesDetailsPanel(FxRobot robot) {
        openTrader(robot);
        selectTestItem(robot);

        Label name = robot.lookup("#selectedNameLabel").queryAs(Label.class);
        Label price = robot.lookup("#selectedPriceLabel").queryAs(Label.class);
        Label stock = robot.lookup("#selectedStockLabel").queryAs(Label.class);

        assertEquals(testItem.getItemName(), name.getText());
        assertEquals(testItem.getPrice() + " coins", price.getText());
        assertEquals(String.valueOf(testItem.getStockQuantity()), stock.getText());
    }

    @Test
    void successfulPurchaseShowsConfirmationAndUpdatesBalance(FxRobot robot) {
        openTrader(robot);
        selectTestItem(robot);

        robot.clickOn("#buyItemButton");

        DialogPane dialog = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertEquals("Item purchased successfully.", dialog.getHeaderText());
        assertTrue(dialog.getContentText().contains("purchased for 25 coins"));

        robot.clickOn("OK");
        WaitForAsyncUtils.waitForFxEvents();

        Label balance = robot.lookup("#coinBalanceLabel").queryAs(Label.class);
        assertEquals("Your Coins: 175", balance.getText());
    }

    @Test
    void invalidQuantityShowsWarningAlert(FxRobot robot) {
        openTrader(robot);
        selectTestItem(robot);

        Spinner<Integer> spinner =
                robot.lookup("#quantitySpinner").queryAs(Spinner.class);

        robot.interact(() -> spinner.getEditor().setText("0"));
        robot.clickOn("#buyItemButton");

        DialogPane dialog = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertEquals("Enter a whole number greater than zero.", dialog.getHeaderText());
        assertTrue(dialog.getContentText().contains("greater than zero"));

        robot.clickOn("OK");
    }

    @Test
    void insufficientCoinsShowsWarningAlert(FxRobot robot) {
        openTrader(robot);
        selectTestItem(robot);

        Spinner<Integer> spinner =
                robot.lookup("#quantitySpinner").queryAs(Spinner.class);

        // 9 items x 25 coins = 225, while the test player has 200 coins.
        robot.interact(() -> spinner.getEditor().setText("9"));
        robot.clickOn("#buyItemButton");

        DialogPane dialog = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertEquals("You do not have enough coins.", dialog.getHeaderText());
        assertTrue(dialog.getContentText().contains("Insufficient coins"));

        robot.clickOn("OK");
    }

    @Test
    void creatureSellingSectionDisplaysOwnedCreature(FxRobot robot) {
        openTrader(robot);

        ListView<Cat> list =
                robot.lookup("#creatureListView").queryAs(ListView.class);

        assertFalse(list.getItems().isEmpty());

        robot.interact(() -> list.getSelectionModel().select(0));
        WaitForAsyncUtils.waitForFxEvents();

        Label name = robot.lookup("#creatureNameLabel").queryAs(Label.class);
        Label saleValue = robot.lookup("#creatureSaleValueLabel").queryAs(Label.class);

        assertEquals(testCat.getName(), name.getText());
        assertEquals(testCat.getMaxHp() + " coins", saleValue.getText());
    }

    @Test
    void successfulCreatureSaleShowsConfirmation(FxRobot robot) {
        openTrader(robot);

        ListView<Cat> list =
                robot.lookup("#creatureListView").queryAs(ListView.class);

        robot.interact(() -> list.getSelectionModel().select(0));
        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn("#sellCreatureButton");

        DialogPane dialog = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertTrue(dialog.getHeaderText().contains("was sold successfully"));
        assertTrue(dialog.getContentText().contains("80 coins"));

        robot.clickOn("OK");
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(list.getItems().isEmpty());

        Label balance = robot.lookup("#coinBalanceLabel").queryAs(Label.class);
        assertEquals("Your Coins: 280", balance.getText());
    }

    @Test
    void activeCreatureShowsWarningInsteadOfSelling(FxRobot robot) throws Exception {
        currentPlayer.setActiveCatId(testCat.getId());
        new PlayerDAO().update(currentPlayer);

        openTrader(robot);

        ListView<Cat> list =
                robot.lookup("#creatureListView").queryAs(ListView.class);

        robot.interact(() -> list.getSelectionModel().select(0));
        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn("#sellCreatureButton");

        DialogPane dialog = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertEquals("The active creature cannot be sold.", dialog.getHeaderText());
        assertTrue(dialog.getContentText().contains("active creature"));

        robot.clickOn("OK");

        assertFalse(list.getItems().isEmpty());
    }

    @Test
    void returnButtonGoesBackToMainMenu(FxRobot robot) {
        openTrader(robot);

        robot.clickOn("#returnToMainMenuButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(robot.lookup("Visit the Trader").query());
    }

    private void openTrader(FxRobot robot) {
        robot.clickOn("Visit the Trader");
        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(robot.lookup("#traderRoot").query());
    }

    private void selectTestItem(FxRobot robot) {
        TableView<TraderItem> table =
                robot.lookup("#traderItemsTable").queryAs(TableView.class);

        int index = -1;
        for (int i = 0; i < table.getItems().size(); i++) {
            if (testItem.getItemName().equals(table.getItems().get(i).getItemName())) {
                index = i;
                break;
            }
        }

        if (index < 0) {
            throw new AssertionError("Seeded trader item was not displayed.");
        }

        int selectedIndex = index;
        robot.interact(() -> table.getSelectionModel().select(selectedIndex));
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void configureTemporaryDatabase() throws Exception {
        testDatabaseFile = Files.createTempFile(
                "clash-of-claws-testfx-",
                ".db"
        );

        // Let SQLite create the database after the schema is selected.
        Files.deleteIfExists(testDatabaseFile);

        String databaseUrl = "jdbc:sqlite:" + testDatabaseFile.toAbsolutePath();
        System.setProperty(DatabaseManager.DATABASE_URL_PROPERTY, databaseUrl);

        DatabaseManager.getInstance().initializeDatabase();
    }

    private void seedTestData() throws Exception {
        AccountService accountService = AccountService.getInstance();
        accountService.logout();

        String username = "trader_ui_" + UUID.randomUUID().toString().substring(0, 8);

        accountService.register(username, PASSWORD, PASSWORD);
        currentPlayer = accountService.authenticate(username, PASSWORD);

        currentPlayer.setCurrencyBalance(200);
        new PlayerDAO().update(currentPlayer);

        testItem = new TraderItem(
                "UI Test Potion",
                "HEALING",
                "Restores health during battle.",
                25,
                10
        );
        new TraderItemDAO().createItem(testItem);

        testCat = new Cat(
                "UI Test Cat",
                "Tabby",
                80,
                new ArrayList<>(List.of("SCRATCH")),
                true,
                false
        );
        new CatDAO().insert(testCat, currentPlayer.getPlayerId());
    }
}

