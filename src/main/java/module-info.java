module swp.com.fxapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens swp.com.fxapp to javafx.fxml;
    exports swp.com.fxapp;
}