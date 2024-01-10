package com.example.final_social_network.Controller;

import com.example.final_social_network.Domain.MessageDTO;
import com.example.final_social_network.Domain.User;
import com.example.final_social_network.Service.MessageService;
import com.example.final_social_network.Service.UserService;
import com.example.final_social_network.StartApplication;
import com.example.final_social_network.Validator.ValidationException;
import com.example.final_social_network.utils.events.Event;
import com.example.final_social_network.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ChatController implements Observer<Event> {
    @FXML
    private Label friendNameLabel;
    @FXML
    private TextField newMessageTextField;
    @FXML
    private ListView<User> usersListView;
    @FXML
    private ListView<MessageDTO> messagesListView;
    @FXML
    private Button sendButton;
    @FXML
    private Button replyButton;

    ObservableList<User> usersModel = FXCollections.observableArrayList();
    ObservableList<MessageDTO> messageModel = FXCollections.observableArrayList();

    private User selectedUser;
    private List<MessageDTO> currentChatMessageList;


    // Login user's data
    private Long idUser;


    //Service
    private UserService userService;
    private MessageService messageService;

    //Tab Pane and currrent Tab
    private TabPane userTabPane;
    private Tab currentTab;

    public void setService(UserService userSev, MessageService messSev) {
        userService = userSev;
        messageService = messSev;
        userService.addObserver(this);
        messageService.addObserver(this);
    }


    public void setTab(TabPane mainTabPane, Tab tab){
        userTabPane = mainTabPane;
        currentTab = tab;
    }


    public void setUser(Long idU) {
        idUser = idU;
        initModel();
    }


    @FXML
    public void initialize() {
        usersListView.setItems(usersModel);
        usersListView.setCellFactory(user -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getFirstName() +' '+ user.getLastName());
                }
            }
        });

        usersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)-> {
            if(newValue != null) {
                sendButton.setDisable(false);
                replyButton.setDisable(false);
                selectedUser = newValue;
                friendNameLabel.setText(selectedUser.getFirstName() + " " + selectedUser.getLastName());
                initChat();
            }
        });

        sendButton.setDisable(true);
        replyButton.setDisable(true);
        messagesListView.setItems(messageModel);
        messagesListView.setCellFactory(message -> new ListCell<>(){
            @Override
            protected void updateItem(MessageDTO message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox container = new VBox();

                    // Daca e reply la un mesaj contruim "antetul"
                    double sizeOfTheMessage = 0.0; // ne trebuie dimensiunea mesajul pt a formata chenarul
                    if (message.getRepliedMessage() != null) {
                        Text replyText = new Text("Replying to " + userService.findOne(message.getTo()).getFirstName() + ": " + messageService.findOne(message.getRepliedMessage()).getMessage() + "\n");
                        replyText.setFill(Color.GREY);
                        container.getChildren().add(replyText);
                        sizeOfTheMessage += replyText.getLayoutBounds().getWidth();
                    }

                    // textul mesajului propriu-zis
                    Text messageText = new Text(message.getMessage());
                    if (message.getFrom().equals(idUser)) {
                        container.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, new CornerRadii(13), null)));
                        setAlignment(Pos.CENTER_RIGHT);
                    } else {
                        container.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, new CornerRadii(13), null)));
                        setAlignment(Pos.CENTER_LEFT);
                    }
                    container.getChildren().add(messageText);

                    // Adaugă margini în jurul chenarului
                    container.setPadding(new Insets(10, 10, 10, 10));

                    sizeOfTheMessage = Double.max(messageText.getLayoutBounds().getWidth(), sizeOfTheMessage);
                    container.setMaxWidth(sizeOfTheMessage + 20);
                    container.setMinWidth(sizeOfTheMessage + 20);

                    // Adaugă chenarul
                    container.setBorder(new Border(new BorderStroke(Color.BLACK,
                            BorderStrokeStyle.SOLID, new CornerRadii(13), BorderWidths.DEFAULT)));

                    setGraphic(container);
                    setTooltip(new Tooltip(message.getData().toString()));
                }
                //messagesListView.scrollTo(messageModel.size() -1);
            }
        });
    }


    private void initModel() {
        initUsers();
        initChat();
    }

    private void initUsers() {
        Iterable<User> users = messageService.findAllWhoHaveAChatWithUser(idUser);
        List<User> usersList = StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList());
        usersModel.setAll(usersList);
    }

    private void initChat() {
        if (selectedUser != null) {
            Iterable<MessageDTO> messages = messageService.findAllMessagesBetweenTwoUsers(idUser, selectedUser.getId());
            currentChatMessageList = StreamSupport.stream(messages.spliterator(), false)
                    .collect(Collectors.toList());
            messageModel.setAll(currentChatMessageList);
        }
    }


    @Override
    public void update(Event event) {
        initModel();
    }


    public void handleNewMessage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartApplication.class.getResource("NewMessage-view.fxml"));
            AnchorPane root = loader.load();

            Tab newMessageTab = new Tab("Chats");
            newMessageTab.setContent(root);
            userTabPane.getTabs().add(newMessageTab);
            userTabPane.getSelectionModel().select(newMessageTab);

            NewMessageController controller = loader.getController();
            controller.setService(messageService, userService);
            controller.setTab(userTabPane, newMessageTab);
            controller.setUser(idUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendHandler() {
        try {
            messageService.sendAMessage(idUser, Collections.singletonList(selectedUser.getId()), newMessageTextField.getText(), null);
            newMessageTextField.clear();
        } catch (ValidationException err) {
            MessageAlert.showErrorMessage(null, err.getMessage());
        }
    }

    public void replyHandler() {
        try {
            MessageDTO selected = messagesListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                messageService.sendAMessage(idUser, Collections.singletonList(selectedUser.getId()), newMessageTextField.getText(), selected.getId());
                newMessageTextField.clear();
            } else MessageAlert.showErrorMessage(null, "Select a message!");
        } catch (ValidationException err) {
            MessageAlert.showErrorMessage(null, err.getMessage());
        }
    }
}
