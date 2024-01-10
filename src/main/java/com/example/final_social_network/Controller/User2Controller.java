package com.example.final_social_network.Controller;

import com.example.final_social_network.Domain.User;
import com.example.final_social_network.Service.FriendRequestService;
import com.example.final_social_network.Service.FriendshipService;
import com.example.final_social_network.Service.MessageService;
import com.example.final_social_network.Service.UserService;
import com.example.final_social_network.StartApplication;
import com.example.final_social_network.utils.events.*;
import com.example.final_social_network.utils.observer.Observer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class User2Controller implements Observer<Event> {
    //Text fields
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldEmail;


    //User information
    private Long idUser;
    private String firstNameUser;
    private String lastNameUser;
    private String emailUser;


    //Service
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService requestService;
    private MessageService messageService;

    //Tab Pane and Stage
    @FXML
    private TabPane userTabPane;
    private Stage userStage;


    public void setService(UserService userSev, FriendshipService friendshipSev,
                           FriendRequestService requestSev, MessageService messSev) {
        userService = userSev;
        friendshipService = friendshipSev;
        requestService = requestSev;
        messageService = messSev;
        userService.addObserver(this);
        friendshipService.addObserver(this);
        requestService.addObserver(this);
        messageService.addObserver(this);
    }

    public void setTab(TabPane userrrTabPane, Stage st){
        userTabPane = userrrTabPane;
        userStage = st;
    }

    public void setUser(String firstName, String lastName, String email) {
        firstNameUser = firstName;
        lastNameUser = lastName;
        emailUser = email;
        idUser = userService.findOne(email).getId();

        //Set the User information
        textFieldFirstName.setText(firstNameUser);
        textFieldLastName.setText(lastNameUser);
        textFieldEmail.setText(emailUser);

        initModel();
    }

    @FXML
    public void initialize() {
    }

    private void initModel() {
        userTabPane.getTabs().get(0).setText("Profile: " + emailUser);
    }

    @Override
    public void update(Event event) {
        initModel();

        if (event instanceof UserChangeEvent userChangeEvent) {
            if (userChangeEvent.getType() == ChangeEventType.UPDATE) {
                User newUser = userChangeEvent.getNewUser();
                firstNameUser = newUser.getFirstName();
                lastNameUser = newUser.getLastName();

                textFieldFirstName.setText(firstNameUser);
                textFieldLastName.setText(lastNameUser);
            }
        }
    }

    public void handleLogOut() {
        userStage.close();
    }

    public void handleSettings() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartApplication.class.getResource("Settings-view.fxml"));
            AnchorPane root = loader.load();

            Tab updateTab = new Tab("Update your account");
            updateTab.setContent(root);
            userTabPane.getTabs().add(updateTab);
            userTabPane.getSelectionModel().select(updateTab);

            SettingsController controller = loader.getController();
            controller.setService(userService);
            controller.setTab(userTabPane, updateTab, userStage);
            controller.setUser(idUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleChat() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartApplication.class.getResource("Chat-view.fxml"));
            AnchorPane root = loader.load();

            Tab chatTab = new Tab("Chats " + emailUser);
            chatTab.setContent(root);
            userTabPane.getTabs().add(chatTab);
            userTabPane.getSelectionModel().select(chatTab);

            ChatController controller = loader.getController();
            controller.setService(userService, messageService);
            controller.setTab(userTabPane, chatTab);
            controller.setUser(idUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handlerFriendships() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartApplication.class.getResource("Friendships-view.fxml"));
            AnchorPane root = loader.load();

            Tab friendshipsTab = new Tab("Friends of " + emailUser);
            friendshipsTab.setContent(root);
            userTabPane.getTabs().add(friendshipsTab);
            userTabPane.getSelectionModel().select(friendshipsTab);

            FriendshipsController controller = loader.getController();
            controller.setService(friendshipService, userService, requestService);
            controller.setTab(userTabPane, friendshipsTab);
            controller.setUser(idUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handlerFriendRequest() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartApplication.class.getResource("FriendRequests-view.fxml"));
            AnchorPane root = loader.load();

            Tab requestsTab = new Tab("Friend Requests of  " + emailUser);
            requestsTab.setContent(root);
            userTabPane.getTabs().add(requestsTab);
            userTabPane.getSelectionModel().select(requestsTab);

            FriendRequestController controller = loader.getController();
            controller.setService(friendshipService, requestService, userService);
            controller.setTab(userTabPane, requestsTab);
            controller.setUser(idUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
