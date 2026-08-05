package app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private void handleBackToLogin() {
        SceneFactory.show(SceneType.LOGIN);
    }

    @FXML
    private void handleBattle() {
        SceneFactory.show(SceneType.BATTLE);
    }


    @FXML
    private void openCatDex(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainApplication.class.getResource("/app/cat-dex.fxml")
        );

        Parent catDexRoot = loader.load();

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        Scene scene = new Scene(catDexRoot, 960, 540);

        stage.setScene(scene);
        stage.setTitle("Clash of Claws - Cat Dex");
        stage.show();
    }
}