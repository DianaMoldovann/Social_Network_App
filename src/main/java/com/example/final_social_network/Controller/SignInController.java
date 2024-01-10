package com.example.final_social_network.Controller;

import com.example.final_social_network.Service.FriendRequestService;
import com.example.final_social_network.Service.FriendshipService;
import com.example.final_social_network.Service.MessageService;
import com.example.final_social_network.Service.UserService;
import com.example.final_social_network.StartApplication;
import com.example.final_social_network.Validator.ValidationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SignInController {
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService requestService;
    private MessageService messageService;
    private TabPane socialNetworkTabPane;
    private Tab signInTab;

    public void setService(UserService userSev, FriendshipService friendshipSev,
                           FriendRequestService requestSev, MessageService messSev){
        userService = userSev;
        friendshipService = friendshipSev;
        requestService = requestSev;
        messageService = messSev;
    }

    public void setTab(TabPane mainTabPane, Tab myTab){
        socialNetworkTabPane = mainTabPane;
        signInTab = myTab;
    }

    @FXML
    private void initialize() {
    }

    public void handleSignIn() {
        //Save the user
        String firstName = textFieldFirstName.getText( );
        String lastName = textFieldLastName.getText();
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();

        try {
            userService.add(firstName, lastName, email, password);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Success SignIn", "Welcome to your account");

            //open user profile
            try {
                // create a new tab for User profile.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(StartApplication.class.getResource("User2-view.fxml"));
                TabPane userTabPane = loader.load();

                // Create a new Stage for every user
                Stage userStage = new Stage();
                userStage.initModality(Modality.WINDOW_MODAL);
                Scene userScene = new Scene(userTabPane);
                userStage.setScene(userScene);

                User2Controller userController = loader.getController();
                userController.setService(userService, friendshipService, requestService, messageService);
                userController.setTab(userTabPane, userStage);
                userController.setUser(firstName,lastName, email);

                userStage.show();
                socialNetworkTabPane.getTabs().remove(signInTab);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (ValidationException | IllegalArgumentException ve) {
            MessageAlert.showErrorMessage(null,ve.getMessage());
        }

    }
}
