package battle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controls the basic Battle Engine scene.
 *
 * @author Quinton Nisonger
 * @version 0.1.0
 * @since 8/3/2026
 */
public class BattleController {

  @FXML
  private Label battleMessageLabel;

  @FXML
  private Label playerHealthLabel;

  @FXML
  private Label opponentHealthLabel;

  @FXML
  private void handleAttack() {
    battleMessageLabel.setText("Cattatatta attacked!");
  }

  @FXML
  private void handleRun() {
    battleMessageLabel.setText("The player ran away!");
  }

  @FXML
  private void handleBag() {
    battleMessageLabel.setText("The player opened their bag.");
  }
}