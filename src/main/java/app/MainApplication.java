package app;

import database.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        initializeDatabase();

        FXMLLoader loader = new FXMLLoader(
                MainApplication.class.getResource("/app/cat-dex.fxml")
        );

        Scene scene = new Scene(loader.load(), 960, 540);

        stage.setTitle("Clash of Claws");
        stage.setScene(scene);
        stage.show();
    }

    private void initializeDatabase() {
        try {
            DatabaseManager.initializeDatabase();
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to initialize the Clash of Claws database.",
                    exception
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}