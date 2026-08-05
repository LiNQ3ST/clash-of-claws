module clashofclaws {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens app to javafx.fxml;
    opens creature to javafx.fxml;
    opens battle to javafx.fxml;

    exports app;
    exports battle;
    exports database;
}