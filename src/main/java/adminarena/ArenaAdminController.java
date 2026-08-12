package adminarena;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class ArenaAdminController {

    @FXML
    private TableView<Arena> arenaTable;

    @FXML
    private TableColumn<Arena, Number> idColumn;

    @FXML
    private TableColumn<Arena, String> nameColumn;

    @FXML
    private TableColumn<Arena, String> townColumn;

    @FXML
    private TableColumn<Arena, String> difficultyColumn;

    @FXML
    private TableColumn<Arena, Number> rewardColumn;

    @FXML
    private TableColumn<Arena, Boolean> activeColumn;

    @FXML
    private TextField arenaNameField;

    @FXML
    private TextField townNameField;

    @FXML
    private ComboBox<String> difficultyComboBox;

    @FXML
    private TextField rewardField;

    @FXML
    private CheckBox activeCheckBox;

    private ArenaDAO arenaDAO;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getArenaId()
                )
        );

        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getArenaName()
                )
        );

        townColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getTownName()
                )
        );

        difficultyColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDifficulty()
                )
        );

        rewardColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getRewardAmount()
                )
        );

        activeColumn.setCellValueFactory(data ->
                new SimpleBooleanProperty(
                        data.getValue().isActive()
                )
        );

        difficultyComboBox.setItems(
                FXCollections.observableArrayList(
                        "EASY",
                        "MEDIUM",
                        "HARD"
                )
        );

        activeCheckBox.setSelected(true);

        arenaTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldArena, selectedArena) -> {
                    if (selectedArena != null) {
                        populateForm(selectedArena);
                    }
                });
    }

    public void setArenaDAO(ArenaDAO arenaDAO) {
        this.arenaDAO = arenaDAO;
        refreshTable();
    }

    @FXML
    private void handleAddArena() {
        try {
            ensureDaoConfigured();

            Arena arena = readForm(null);
            arenaDAO.insert(arena);

            refreshTable();
            clearForm();

            showMessage(
                    Alert.AlertType.INFORMATION,
                    "Arena Created",
                    "The arena was created successfully."
            );
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (SQLException exception) {
            showError("The arena could not be created.");
        }
    }

    @FXML
    private void handleUpdateArena() {
        Arena selectedArena =
                arenaTable.getSelectionModel().getSelectedItem();

        if (selectedArena == null) {
            showError("Select an arena before updating.");
            return;
        }

        try {
            ensureDaoConfigured();

            Arena arena = readForm(
                    selectedArena.getArenaId()
            );

            arenaDAO.update(arena);

            refreshTable();
            clearForm();

            showMessage(
                    Alert.AlertType.INFORMATION,
                    "Arena Updated",
                    "The arena was updated successfully."
            );
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (SQLException exception) {
            showError("The arena could not be updated.");
        }
    }

    @FXML
    private void handleDeleteArena() {
        Arena selectedArena =
                arenaTable.getSelectionModel().getSelectedItem();

        if (selectedArena == null) {
            showError("Select an arena before deleting.");
            return;
        }

        try {
            ensureDaoConfigured();

            arenaDAO.delete(selectedArena.getArenaId());

            refreshTable();
            clearForm();

            showMessage(
                    Alert.AlertType.INFORMATION,
                    "Arena Deleted",
                    "The arena was deleted successfully."
            );
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (SQLException exception) {
            showError("The arena could not be deleted.");
        }
    }

    @FXML
    private void handleClearForm() {
        clearForm();
    }

    private Arena readForm(Integer arenaId) {
        String arenaName = arenaNameField.getText().trim();
        String townName = townNameField.getText().trim();
        String difficulty = difficultyComboBox.getValue();

        if (arenaName.isBlank()) {
            throw new IllegalArgumentException(
                    "Arena name is required."
            );
        }

        if (townName.isBlank()) {
            throw new IllegalArgumentException(
                    "Town name is required."
            );
        }

        if (difficulty == null) {
            throw new IllegalArgumentException(
                    "Select a difficulty."
            );
        }

        int reward;

        try {
            reward = Integer.parseInt(
                    rewardField.getText().trim()
            );
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Reward must be a whole number."
            );
        }

        if (reward < 0) {
            throw new IllegalArgumentException(
                    "Reward cannot be negative."
            );
        }

        return new Arena(
                arenaId,
                arenaName,
                townName,
                difficulty,
                reward,
                activeCheckBox.isSelected()
        );
    }

    private void populateForm(Arena arena) {
        arenaNameField.setText(arena.getArenaName());
        townNameField.setText(arena.getTownName());

        difficultyComboBox.setValue(
                arena.getDifficulty()
        );

        rewardField.setText(
                String.valueOf(arena.getRewardAmount())
        );

        activeCheckBox.setSelected(arena.isActive());
    }

    private void refreshTable() {
        if (arenaDAO == null) {
            return;
        }

        try {
            arenaTable.setItems(
                    FXCollections.observableArrayList(
                            arenaDAO.findAll()
                    )
            );
        } catch (SQLException exception) {
            showError("The arena list could not be loaded.");
        }
    }

    private void clearForm() {
        arenaTable.getSelectionModel().clearSelection();
        arenaNameField.clear();
        townNameField.clear();
        difficultyComboBox.setValue(null);
        rewardField.clear();
        activeCheckBox.setSelected(true);
    }

    private void ensureDaoConfigured() {
        if (arenaDAO == null) {
            throw new IllegalArgumentException(
                    "ArenaDAO has not been configured."
            );
        }
    }

    private void showError(String message) {
        showMessage(
                Alert.AlertType.ERROR,
                "Arena Error",
                message
        );
    }

    private void showMessage(
            Alert.AlertType alertType,
            String title,
            String message
    ) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}