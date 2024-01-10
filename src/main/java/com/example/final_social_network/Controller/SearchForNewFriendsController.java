package com.example.final_social_network.Controller;

import com.example.final_social_network.Domain.User;
import com.example.final_social_network.Service.FriendRequestService;
import com.example.final_social_network.Service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SearchForNewFriendsController {
    // Tab Pane and My Tab
    private TabPane userTabPane;
    private Tab searchTab;



    // Services that we need
    private UserService userService;
    private FriendRequestService requestService;



    //String we were looking for
    String searchedString;



    // User information
    Long idUser;


    // FXML components
    @FXML
    private TableView<User> searchTableView;
    @FXML
    private TableColumn<String, User> firstNameColumnSearch;
    @FXML
    private TableColumn<String, User> lastNameColumnSearch;
    @FXML
    private TableColumn<String, User> emailColumnSearch;
    @FXML
    private TextField searchTextField;


    ObservableList<User> searchModel = FXCollections.observableArrayList();


    public void setService(UserService userSev, FriendRequestService requestSev){
        userService = userSev;
        requestService = requestSev;
    }

    public void setTab(TabPane mainTabPane, Tab myTab){
        userTabPane = mainTabPane;
        searchTab = myTab;
    }

    public void setUser(Long id) {
        idUser = id;
    }

    @FXML
    public void initialize() {
        firstNameColumnSearch.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumnSearch.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumnSearch.setCellValueFactory(new PropertyValueFactory<>("email"));
        searchTableView.setItems(searchModel);


        searchTextField.textProperty().addListener(observable -> {
            searchedString = searchTextField.getText();
            initModel();
        });

    }

    private void initModel() {
        // init Friends table
        Iterable<User> users = userService.getAllFiltered(searchedString);
        List<User> usersList = StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList());
        searchModel.setAll(usersList);
    }

    public void handleSearchButton() {
        initModel();
    }

    public void handleSendRequest() {
        User selected = (User) searchTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                requestService.sendAFriendRequest(idUser, selected.getId());
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Succes" , "you have successfully sent the friend request");
                searchTableView.getSelectionModel().clearSelection();
            } catch (IllegalArgumentException err) {
                MessageAlert.showErrorMessage(null, err.getMessage());
            }
        } else MessageAlert.showErrorMessage(null, "Select a user!");
    }

}
