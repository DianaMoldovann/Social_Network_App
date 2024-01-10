package com.example.final_social_network.Controller;

import com.example.final_social_network.utils.events.Event;
import com.example.final_social_network.utils.observer.Observer;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MessageAlert implements Observer<Event> {
    public static void showMessage(Stage owner, Alert.AlertType type, String header, String text){
        Alert message=new Alert(type);
        message.setHeaderText(header);
        message.setContentText(text);
        message.initOwner(owner);
        message.showAndWait();
    }

    public static void showErrorMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Error");
        message.setContentText(text);
        message.showAndWait();
    }

    @Override
    public void update(Event event) {
        
    }
}

