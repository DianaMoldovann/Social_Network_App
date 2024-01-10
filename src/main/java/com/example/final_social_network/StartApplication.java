package com.example.final_social_network;

import com.example.final_social_network.Controller.SocialNetworkController;
import com.example.final_social_network.Domain.Friendship;
import com.example.final_social_network.Domain.Message;
import com.example.final_social_network.Domain.User;
import com.example.final_social_network.Repository.FriendRequestRRepository.FriendRequestDBRepository;
import com.example.final_social_network.Repository.FriendshipRepository.FriendshipDBRepository;
import com.example.final_social_network.Repository.MessageRepository.MessageDBRepository;
import com.example.final_social_network.Repository.UserRepository.UserDBRepository;
import com.example.final_social_network.Service.FriendRequestService;
import com.example.final_social_network.Service.FriendshipService;
import com.example.final_social_network.Service.MessageService;
import com.example.final_social_network.Service.UserService;
import com.example.final_social_network.Validator.FriendshipValidator;
import com.example.final_social_network.Validator.MessageValidator;
import com.example.final_social_network.Validator.UserValidator;
import com.example.final_social_network.Validator.Validator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class StartApplication extends Application {

    //private Repository<Long, User> userRepo;
    private UserDBRepository userRepo;
    private FriendshipDBRepository friendshipRepo;
    private FriendRequestDBRepository requestRepo;
    private MessageDBRepository messageRepo;
    private Validator<User> userValidator;
    private Validator<Friendship> friendshipValidator;
    private Validator<Message> messageValidator;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService requestService;
    private MessageService messageService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        userRepo = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Diana27082003");
        userValidator = new UserValidator();
        friendshipRepo = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Diana27082003");
        friendshipValidator = new FriendshipValidator();
        requestRepo = new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Diana27082003");
        messageRepo = new MessageDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Diana27082003");
        messageValidator = new MessageValidator();
        userService = new UserService(userRepo, friendshipRepo, userValidator);
        friendshipService = new FriendshipService(friendshipRepo, userRepo, friendshipValidator);
        requestService = new FriendRequestService(requestRepo, friendshipRepo, userRepo);
        messageService = new MessageService(userRepo, messageRepo, messageValidator);
        initView(primaryStage);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(750);
        Image icon = new Image("/imagini/croco.png");
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Croco Chat");
        primaryStage.show();


    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader applicationLoader = new FXMLLoader();
        applicationLoader.setLocation(getClass().getResource("SocialNetwork-view.fxml"));
        TabPane ApplicationLayout = applicationLoader.load();
        //Scene s = new Scene(ApplicationLayout);
        //s.getStylesheets().add(getClass().getResource("darkMode.css").toExternalForm());
        //primaryStage.setScene(s);
        primaryStage.setScene(new Scene(ApplicationLayout));

        SocialNetworkController socialNetworkController = applicationLoader.getController();
        socialNetworkController.setService(userService, friendshipService, requestService, messageService);

    }
}