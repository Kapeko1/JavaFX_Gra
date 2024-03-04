module com.example.javafx_gra {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.javafx_gra to javafx.fxml;
    exports com.example.javafx_gra;
}