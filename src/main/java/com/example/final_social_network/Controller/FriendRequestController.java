package com.example.final_social_network.Controller;

import com.example.final_social_network.Domain.FriendDTO;
import com.example.final_social_network.Service.FriendRequestService;
import com.example.final_social_network.Service.FriendshipService;
import com.example.final_social_network.Service.UserService;
import com.example.final_social_network.StartApplication;
import com.example.final_social_network.utils.events.*;
import com.example.final_social_network.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestController  implements Observer<Event> {

    //TableView for friend reequests
    @FXML
    private TableView<FriendDTO> requestsTableView;
    @FXML
    private TableColumn<FriendDTO, String> firstNameColumnRequest;
    @FXML
    private TableColumn<FriendDTO, String> lastNameColumnRequest;
    @FXML
    private TableColumn<FriendDTO, LocalDate> dateColumnRequest;


    //Observable list for the friend Request Table
    ObservableList<FriendDTO> requestsModel = FXCollections.observableArrayList();


    //User information
    private Long idUser;

    //Service
    private FriendshipService friendshipService;
    private FriendRequestService requestService;
    private UserService userService;

    //Tab Pane and Stage
    @FXML
    private TabPane userTabPane;
    private Tab friendRequestsTab;


    public void setService(FriendshipService friendshipSev,
                           FriendRequestService requestSev,
                           UserService userSev) {
        friendshipService = friendshipSev;
        requestService = requestSev;
        userService = userSev;
        friendshipService.addObserver(this);
        requestService.addObserver(this);
        userService.addObserver(this);
    }

    public void setTab(TabPane userrrTabPane, Tab tab){
        userTabPane = userrrTabPane;
        friendRequestsTab = tab;
    }

    public void setUser(Long idUserr) {
        idUser = idUserr;
        initModel();
    }

    @FXML
    public void initialize() {
        //Request Table
        firstNameColumnRequest.setCellValueFactory(new PropertyValueFactory<>("firstNameFriend"));
        lastNameColumnRequest.setCellValueFactory(new PropertyValueFactory<>("lastNameFriend"));
        dateColumnRequest.setCellValueFactory(new PropertyValueFactory<>("date"));
        requestsTableView.setItems(requestsModel);

    }

    private void initModel() {
        //init Requests table
        initRequestsModel();
    }

    private void initRequestsModel() {
        Iterable<FriendDTO> requests = requestService.getAllFriendRequestOfUser(idUser);
        List<FriendDTO> requestsList = StreamSupport.stream(requests.spliterator(), false)
                .collect(Collectors.toList());
        requestsModel.setAll(requestsList);
    }

    @Override
    public void update(Event event) {
        initModel();
        if (event instanceof RequestChangeEvent) {
            initRequestsModel();
        }
    }

    public void handleAcceptFriendRequest() {
        FriendDTO selected = requestsTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            requestService.acceptAFriendRequest(selected.getIdFriend(), idUser);
        } else MessageAlert.showErrorMessage(null, "Select a request!");
    }

    public void handleDeclineFriendRequest() {
        FriendDTO selected = requestsTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            requestService.declineAFriendRequest(selected.getIdFriend(), idUser);
        } else MessageAlert.showErrorMessage(null, "Select a request!");
    }

    public void handleAddNewFriend() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(StartApplication.class.getResource("SearchForNewFriends-view.fxml"));
            AnchorPane root = loader.load();

            Tab updateTab = new Tab("Search for new friends");
            updateTab.setContent(root);
            userTabPane.getTabs().add(updateTab);
            userTabPane.getSelectionModel().select(updateTab);

            SearchForNewFriendsController controller = loader.getController();
            controller.setService(userService, requestService);
            controller.setTab(userTabPane, updateTab);
            controller.setUser(idUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
