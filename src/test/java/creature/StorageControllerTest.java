package creature;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StorageControllerTest
    extends ApplicationTest {


  @Override
  public void start(Stage stage)
      throws Exception {

    FXMLLoader loader =
        new FXMLLoader(
            getClass().getResource(
                "/creature/storage.fxml"
            )
        );

    Scene scene =
        new Scene(
            loader.load()
        );

    stage.setScene(
        scene
    );

    stage.show();
  }


  @Test
  public void partyCountUpdatesWhenCatIsAdded() {

    ListView<Cat> partyList =
        lookup(
            "#partyListView"
        ).queryListView();


    Label partyCount =
        lookup(
            "#partyCountLabel"
        ).queryAs(
            Label.class
        );


    ArrayList<String> abilities =
        new ArrayList<String>();

    abilities.add(
        "SCRATCH"
    );


    Cat fakeCat =
        new Cat(
            "Test Cat",
            "Tabby",
            100,
            abilities,
            true,
            true
        );


    /*
     * Make the UI change on the
     * JavaFX Application Thread.
     */
    interact(() -> {

      partyList
          .getItems()
          .add(
              fakeCat
          );
    });


    assertEquals(
        1,
        partyList
            .getItems()
            .size()
    );


    assertEquals(
        "Party: 1 / 4",
        partyCount.getText()
    );
  }
}