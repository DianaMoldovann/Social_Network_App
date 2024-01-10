package com.example.final_social_network.Controller;

import com.example.final_social_network.Domain.FriendDTO;
import com.example.final_social_network.Repository.FriendshipRepository.paging.Page;
import com.example.final_social_network.Repository.FriendshipRepository.paging.Pageable;
import com.example.final_social_network.Service.FriendRequestService;
import com.example.final_social_network.Service.FriendshipService;
import com.example.final_social_network.Service.UserService;
import com.example.final_social_network.StartApplication;
import com.example.final_social_network.utils.events.*;
import com.example.final_social_network.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

public class FriendshipsController implements Observer<Event> {
    //The table view for friends
    @FXML
    private TableView<FriendDTO> friednsTableView;
    @FXML
    private TableColumn<FriendDTO, String> firstNameColumn;
    @FXML
    private TableColumn<FriendDTO, String> lastNameColumn;
    @FXML
    private TableColumn<FriendDTO, String> emailColumn;
    @FXML
    private TableColumn<FriendDTO, LocalDate> dateColumn;


    //Paging elemets
    @FXML
    private Spinner<Integer> spinnerNrOfElements;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Label currentPageLabel;
    private int pageSize = 5;
    private int currentPage = 1;
    private int totalNrOfElems = 0;

    //Observable list for table
    ObservableList<FriendDTO> friendsModel = FXCollections.observableArrayList();


    //User information
    private Long idUser;


    //Service
    private FriendshipService friendshipService;
    private UserService userService;
    private FriendRequestService requestService;

    //Tab Pane
    @FXML
    private TabPane userTabPane;
    private Tab friendshipTab;


    public void setService(FriendshipService friendshipSev, UserService userSev, FriendRequestService requestSev) {
        friendshipService = friendshipSev;
        userService = userSev;
        requestService = requestSev;
        friendshipService.addObserver(this);
        userService.addObserver(this);
        requestService.addObserver(this);
    }

    public void setTab(TabPane userrrTabPane, Tab friendshipTabb) {
        userTabPane = userrrTabPane;
        friendshipTab = friendshipTabb;
    }

    public void setUser(Long idUserr) {
        idUser = idUserr;

        initModel();
    }

    @FXML
    public void initialize() {
        //Friends Table
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstNameFriend"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastNameFriend"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("emailFriend"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        friednsTableView.setItems(friendsModel);

        spinnerNrOfElements.valueProperty().addListener((observable, oldValue, newValue) -> {
            pageSize = newValue;
            currentPage = 1;
            System.out.println(pageSize);
            System.out.println(currentPage);
            initFriendsModel();
        });
    }

    private void initModel() {
        // init Friends table
        initFriendsModel();
    }

    private void initFriendsModel() {
        Page<FriendDTO> page = friendshipService.findFriendsOfUserOnPage(new Pageable(currentPage, pageSize), idUser);
        int maxPage = (int) Math.ceil((double) page.getTotalNrOfElems() / pageSize);
        if (currentPage > maxPage) {
            currentPage = maxPage;

            page = friendshipService.findFriendsOfUserOnPage(new Pageable(currentPage, pageSize), idUser);
        }
        //Iterable<FriendDTO> friends = friendshipService.findFriendsOfUser(idUser);
        List<FriendDTO> friendsList = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .collect(Collectors.toList());
        friendsModel.setAll(friendsList);
        totalNrOfElems = page.getTotalNrOfElems();
        currentPageLabel.setText(currentPage + " of " + maxPage);

        prevButton.setDisable(currentPage == 1);
        nextButton.setDisable(currentPage * pageSize >= totalNrOfElems);
    }

    @Override
    public void update(Event event) {
        initModel();
        if (event instanceof FriendshipChangeEvent) {
            initFriendsModel();
        }
    }

    public void handleDeleteFriend() {
        FriendDTO selected = friednsTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            friendshipService.delete(idUser, selected.getIdFriend());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "You are not friend anymore with" + selected.getEmailFriend());
        } else MessageAlert.showErrorMessage(null, "Select a friend!");
    }

    public void prevHandler() {
        currentPage--;
        initModel();
    }

    public void nextHandler() {
        currentPage++;
        initModel();
    }

    public void handleAddNewFriends(ActionEvent actionEvent) {
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