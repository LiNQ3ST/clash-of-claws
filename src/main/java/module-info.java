module clashofclaws {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens app to javafx.fxml;
    opens account to javafx.fxml;
    opens creature to javafx.fxml;
    opens battle to javafx.fxml;
    opens adminarena to javafx.fxml;
    opens marketplace to javafx.fxml;

    exports app;
    exports account;
    exports creature;
    exports battle;
    exports adminarena;
    exports marketplace;
    exports database;
}