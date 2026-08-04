module clashofclaws {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens app to javafx.fxml;
    opens creature to javafx.fxml;
    exports app;
    exports database;
}