module com.example.final_social_network {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.final_social_network to javafx.fxml;
    opens com.example.final_social_network.Controller to javafx.fxml;
    exports com.example.final_social_network;
    exports com.example.final_social_network.Controller;
    opens com.example.final_social_network.Domain to javafx.base;
}