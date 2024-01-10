package com.example.final_social_network.Controller;

import com.example.final_social_network.Domain.User;
import com.example.final_social_network.Service.FriendRequestService;
import com.example.final_social_network.Service.FriendshipService;
import com.example.final_social_network.Service.MessageService;
import com.example.final_social_network.Service.UserService;
import com.example.final_social_network.StartApplication;
import com.example.final_social_network.Validator.ValidationException;
import com.example.final_social_network.utils.EcryptPassword.PasswordManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService requestService;
    private MessageService messageService;


    private TabPane socialNetworkTabPane;
    private Tab loginTab;

    public void setService(UserService userSev, FriendshipService friendshipSev,
                           FriendRequestService requestSev, MessageService messSev) {
        userService = userSev;
        friendshipService = friendshipSev;
        requestService = requestSev;
        messageService = messSev;
    }

    public void setTab(TabPane mainTabPane, Tab myTab) {
        socialNetworkTabPane = mainTabPane;
        loginTab = myTab;
    }

    @FXML
    private void initialize() {
    }

    public void handleLogin() {
        //Save the user
        String email = textFieldEmail.getText();
        String candidatePassword = textFieldPassword.getText();
        String hashedCandidatePassword = PasswordManager.hashPassword(candidatePassword);
        try {
            User user = userService.findOne(email);
            if (!Objects.equals(user.getPassword(), hashedCandidatePassword)) {
                //incorect password
                MessageAlert.showErrorMessage(null, "Invalid password!");
            } else {
                //corect password
                //open user profile
                try {
                    // create a new tab for User profile.
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(StartApplication.class.getResource("User2-view.fxml"));
                    TabPane userTabPane = loader.load();

                    // Create a new Stage for every user
                    Stage userStage = new Stage();
                    userStage.setTitle(user.getEmail() + " Profile");
                    userStage.initModality(Modality.WINDOW_MODAL);
                    Scene userScene = new Scene(userTabPane);
                    userStage.setScene(userScene);

                    User2Controller userController = loader.getController();
                    userController.setService(userService, friendshipService, requestService, messageService);
                    userController.setTab(userTabPane, userStage);
                    userController.setUser(user.getFirstName(), user.getLastName(), user.getEmail());

                    userStage.show();
                    socialNetworkTabPane.getTabs().remove(loginTab);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (ValidationException | IllegalArgumentException ve) {
            MessageAlert.showErrorMessage(null, ve.getMessage());
        }

    }
}
