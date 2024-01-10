package com.example.final_social_network.Controller;

import com.example.final_social_network.Domain.User;
import com.example.final_social_network.Service.MessageService;
import com.example.final_social_network.Service.UserService;
import com.example.final_social_network.Validator.ValidationException;
import com.example.final_social_network.utils.events.Event;
import com.example.final_social_network.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NewMessageController implements Observer<Event> {
    //FXML COMPONENTS-------------------------------------------------------------------------------------
    @FXML
    private TextField messageTextField;
    @FXML
    private TextField searchForUsersTextField;

    //List view for  users to send the message to
    @FXML
    private ListView<User> usersListView;



    //Observable list for users list----------------------------------------------------------------------------------
    ObservableList<User> usersModel = FXCollections.observableArrayList();


    // String that memorize the searched string from text field----------------------------------------------------------
    String searchedString = "";


    // Login user's data------------------------------------------------------------------------------------------------
    private Long idUser;


    //Service-----------------------------------------------------------------------------------------------------------
    private MessageService messageService;
    private UserService userService;


    //Tab Pane and currrent Tab-----------------------------------------------------------------------------------------
    private TabPane userTabPane;
    private Tab currentTab;



    //SET METHODS-------------------------------------------------------------------------------------------------------
    public void setService(MessageService messSev, UserService userService) {
        messageService = messSev;
        this.userService = userService;
        messageService.addObserver(this);
        userService.addObserver(this);
    }

    public void setTab(TabPane mainTabPane, Tab tab){
        userTabPane = mainTabPane;
        currentTab = tab;
    }

    public void setUser(Long idU) {
        idUser = idU;
        initModel();
    }

    //INIT METHODS-------------------------------------------------------------------------------------------------------
    @FXML
    public void initialize() {
        usersListView.setItems(usersModel);
        usersListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        usersListView.setCellFactory(user -> new ListCell<>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);

                        if (empty || user == null) {
                            setText(null);
                        } else {
                            setText(user.getFirstName() + " " + user.getLastName());
                        }
                    }
                });

        searchForUsersTextField.textProperty().addListener(observable -> {
            searchedString = searchForUsersTextField.getText();
            initModel();
        });

    }

    private void initModel() {
        Iterable<User> users = userService.getAllFiltered(searchedString);
        List<User> usersList = StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList());
        usersModel.setAll(usersList);
    }

    //OBSERVERS METHODS-------------------------------------------------------------------------------------------------------

    @Override
    public void update(Event event) {
        initModel();
    }

    //HANDLERS METHODS-------------------------------------------------------------------------------------------------------

    public void sendHandler(ActionEvent actionEvent) {
        String message = messageTextField.getText();
        try{
            List<User> selectedUsers = usersListView.getSelectionModel().getSelectedItems();
            List<Long> usersID = new ArrayList<>();
            selectedUsers.forEach(u-> usersID.add(u.getId()));
            messageService.sendAMessage(idUser, usersID, message, null);
            userTabPane.getTabs().remove(currentTab);
        } catch (ValidationException err) {
            MessageAlert.showErrorMessage(null, err.getMessage());
        }
    }
}
