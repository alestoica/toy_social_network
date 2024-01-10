package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.example.domain.Request;
import org.example.domain.User;
import org.example.service.RequestService;
import org.example.service.UserService;
import org.example.validators.ValidationException;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class SendFriendRequestController {
    UserService userService;
    RequestService requestService;
    List<User> modelComboBox;
    @FXML
    ComboBox<User> comboBox;
    User selectedUser;
    Stage dialogStage;

    public void setRequestService(UserService userService, RequestService requestService, Stage stage, User user) {
        this.userService = userService;
        this.requestService = requestService;
        this.dialogStage = stage;
        this.selectedUser = user;

        modelComboBox = userService.getNotFriends(selectedUser.getId()).stream().toList();
        comboBox.getItems().setAll(modelComboBox);
        comboBox.getSelectionModel().selectFirst();
    }

    public void initialize() {

    }

    public void handleSend() {
        try {
            User selected = comboBox.getSelectionModel().getSelectedItem();

            Request request = new Request(selectedUser, selected);
            request.setStatus("pending");
            Random random = new Random();
            Long id = random.nextLong(100000);
            request.setId(id);

            requestService.save(request);

            dialogStage.close();

            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Send Request","The request has been sent!");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }
}
