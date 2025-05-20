module com.example.wizardspells {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.wizardspells to javafx.fxml;
    exports com.example.wizardspells;
}