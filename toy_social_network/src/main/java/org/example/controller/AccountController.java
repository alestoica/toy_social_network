package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.repository.db.*;
import org.example.service.*;
import org.example.validators.FriendshipValidator;
import org.example.validators.MessageValidator;
import org.example.validators.RequestValidator;
import org.example.validators.UserValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AccountController {
    PagingUserService userService;
    PagingFriendshipService friendshipService;
    @FXML
    Label welcomeLabel;
    User selectedUser;
    Stage dialogStage;

    public void setAccountService(PagingUserService userService, PagingFriendshipService friendshipService, Stage stage, User user) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.dialogStage = stage;
        this.selectedUser = user;
        this.welcomeLabel.setText("Welcome, " + user.getFirstName() + "!");
    }

    public void initialize() {

    }

    public void handleGetFriends() {
        showFriendshipDialog(selectedUser);
    }

    public void handleGetRequests() {
        showRequestDialog(selectedUser);
    }

    public void showRequestDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/request-view.fxml"));
            AnchorPane layout = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("My Friend Requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(layout));

            RequestController requestController = loader.getController();

            FriendshipValidator friendshipValidator = new FriendshipValidator();
            FriendshipDBPagingRepository friendshipDBRepository = new FriendshipDBPagingRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Alexandra.27.09", friendshipValidator);
            FriendshipService friendshipService = new FriendshipService(friendshipDBRepository);

            RequestValidator requestValidator = new RequestValidator();
            UserValidator userValidator = new UserValidator();
            UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Alexandra.27.09", userValidator);

            RequestDBPagingRepository requestDBRepository = new RequestDBPagingRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Alexandra.27.09", requestValidator, userDBRepository);
            PagingRequestService requestService = new PagingRequestService(requestDBRepository);

            requestController.setRequestService(userService, requestService, friendshipService, dialogStage, user);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showFriendshipDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/friendship-view.fxml"));
            AnchorPane layout = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("My Friends");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(layout));

            FriendshipController friendshipController = loader.getController();

            friendshipController.setFriendshipService(userService, friendshipService, dialogStage, user);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
