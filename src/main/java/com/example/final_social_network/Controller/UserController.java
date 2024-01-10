package com.example.final_social_network.Controller;

import com.example.final_social_network.Domain.FriendDTO;
import com.example.final_social_network.Domain.User;
import com.example.final_social_network.Repository.FriendshipRepository.paging.Page;
import com.example.final_social_network.Repository.FriendshipRepository.paging.Pageable;
import com.example.final_social_network.Service.FriendRequestService;
import com.example.final_social_network.Service.FriendshipService;
import com.example.final_social_network.Service.MessageService;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<Event> {
    //Text fields
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldEmail;

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

    //Text Field for friend search
    @FXML
    private TextField textFieldEmailFindFriend;
    @FXML
    private TextArea textAreaFoundFriend;

    //TableView for friend reequests
    @FXML
    private TableView<FriendDTO> requestsTableView;
    @FXML
    private TableColumn<FriendDTO, String> firstNameColumnRequest;
    @FXML
    private TableColumn<FriendDTO, String> lastNameColumnRequest;
    @FXML
    private TableColumn<FriendDTO, LocalDate> dateColumnRequest;


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

    //Observable list for the friend Request Table
    ObservableList<FriendDTO> requestsModel = FXCollections.observableArrayList();


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

        //Request Table
        firstNameColumnRequest.setCellValueFactory(new PropertyValueFactory<>("firstNameFriend"));
        lastNameColumnRequest.setCellValueFactory(new PropertyValueFactory<>("lastNameFriend"));
        dateColumnRequest.setCellValueFactory(new PropertyValueFactory<>("date"));
        requestsTableView.setItems(requestsModel);

    }

    private void initModel() {
        // init Friends table
        initFriendsModel();

        //init Requests table
        initRequestsModel();
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

    private void initRequestsModel() {
        Iterable<FriendDTO> requests = requestService.getAllFriendRequestOfUser(idUser);
        List<FriendDTO> requestsList = StreamSupport.stream(requests.spliterator(), false)
                .collect(Collectors.toList());
        requestsModel.setAll(requestsList);
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
        if (event instanceof FriendshipChangeEvent) {
            initFriendsModel();
        }
        if (event instanceof RequestChangeEvent) {
            initRequestsModel();
        }
    }

    public void handleLogOut() {
        userStage.close();
    }

    public void handleDelete() {
        userService.delete(idUser);
        userStage.close();
    }

    public void handleUpdate() {
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

    public void handleDeleteFriend() {
        FriendDTO selected = friednsTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            friendshipService.delete(idUser, selected.getIdFriend());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "You are not friend anymore with" + selected.getEmailFriend());
        } else MessageAlert.showErrorMessage(null, "Select a friend!");
    }

    public void handleSearchFriend() {
        String emailFriend = textFieldEmailFindFriend.getText();
        try {
            FriendDTO friend = friendshipService.findFriend(idUser, emailFriend);
            textAreaFoundFriend.setText(friend.toString());
        }
        catch (IllegalArgumentException err) {
            MessageAlert.showErrorMessage(null, err.getMessage());
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

    public void prevHandler() {
        currentPage--;
        initModel();
    }

    public void nextHandler() {
        currentPage++;
        initModel();
    }
}
