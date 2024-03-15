module org.nicholas.guicardealershipsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.nicholas.guicardealershipsystem to javafx.fxml;
    exports org.nicholas.guicardealershipsystem;
}