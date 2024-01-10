package com.example.final_social_network.Controller;

import com.example.final_social_network.Service.FriendRequestService;
import com.example.final_social_network.Service.FriendshipService;
import com.example.final_social_network.Service.MessageService;
import com.example.final_social_network.Service.UserService;
import com.example.final_social_network.StartApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

@SuppressWarnings("ALL")
public class SocialNetworkController {
    @FXML
    private TabPane tabPane;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService requestService;
    private MessageService messageService;

    public void setService(UserService userSev, FriendshipService friendshipSev,
                           FriendRequestService requestSev, MessageService messSev) {
        userService = userSev;
        friendshipService = friendshipSev;
        requestService = requestSev;
        messageService = messSev;
    }

    @FXML
    private void initialize() {
    }


    public void handleLogin() {
        try {
            // create a new tab for Login.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartApplication.class.getResource("Login-view.fxml"));
            AnchorPane root = loader.load();

            // Add a tab for entering login data.
            Tab loginTab = new Tab("Login");
            loginTab.setContent(root);
            tabPane.getTabs().add(loginTab);
            tabPane.getSelectionModel().select(loginTab);

            LoginController controller = loader.getController();
            controller.setService(userService, friendshipService, requestService, messageService);
            controller.setTab(tabPane, loginTab);

//            loginStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSignIN(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartApplication.class.getResource("SignIn-view.fxml"));
            AnchorPane root = loader.load();

            Tab signInTab = new Tab("Sign In");
            signInTab.setContent(root);
            tabPane.getTabs().add(signInTab);
            tabPane.getSelectionModel().select(signInTab);

            SignInController controller = loader.getController();
            controller.setService(userService, friendshipService, requestService, messageService);
            controller.setTab(tabPane, signInTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
