module net.veldor.rdc_informer {
    requires kotlin.stdlib;
    requires java.sql;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires org.json;
    requires Java.WebSocket;
    requires com.google.gson;
    requires org.controlsfx.controls;
    requires java.desktop;

    opens net.veldor.rdc_informer.model.selection;
    opens net.veldor.rdc_informer.controller to javafx.fxml;
    opens net.veldor.rdc_informer to javafx.fxml;
    exports net.veldor.rdc_informer;
}