package com.example.final_social_network.Controller;

import com.example.final_social_network.Domain.User;
import com.example.final_social_network.Service.UserService;
import com.example.final_social_network.Validator.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class SettingsController {
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldPassword;

    private Long idUser;
    private TabPane userTabPane;
    private Tab updateTab;
    private Stage userStage;

    private UserService userService;

    public void setService(UserService userSev){
        userService = userSev;
    }

    public void setTab(TabPane mainTabPane, Tab myTab, Stage userStagee){
        userTabPane = mainTabPane;
        updateTab = myTab;
        userStage = userStagee;
    }

    public void setUser(Long idUserr) {
        idUser = idUserr;
    }

    @FXML
    private void initialize() {
    }
    public void handleUpdate() {
        String firstName = textFieldFirstName.getText( );
        String lastName = textFieldLastName.getText();
        String password = textFieldPassword.getText();

        if (firstName.isEmpty() && lastName.isEmpty()  && password.isEmpty()) {
            MessageAlert.showErrorMessage(null,  "Fill in at least one of the fields above!");
            return;
        }

        User user = userService.findOne(idUser);
        if (firstName.isEmpty()) firstName = user.getFirstName();
        if (lastName.isEmpty()) lastName = user.getLastName();
        if (password.isEmpty()) password = user.getPassword();

        try {
            userService.update(user.getId(), firstName, lastName, user.getEmail(), password);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Succes", "Your account was succesfully update");
            userTabPane.getTabs().remove(updateTab);
        } catch (ValidationException | IllegalArgumentException ve) {
            MessageAlert.showErrorMessage(null,ve.getMessage());
        }
    }

    public void handleDelete() {
        userService.delete(idUser);
        userStage.close();
    }
}
