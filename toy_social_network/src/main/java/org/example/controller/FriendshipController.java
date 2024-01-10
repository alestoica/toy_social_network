package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.repository.db.*;
import org.example.repository.paging.Page;
import org.example.repository.paging.PageImplementation;
import org.example.repository.paging.PageableImplementation;
import org.example.service.*;
import org.example.utils.events.FriendshipChangeEvent;
import org.example.utils.events.UserChangeEvent;
import org.example.utils.observer.Observer;
import org.example.validators.MessageValidator;
import org.example.validators.RequestValidator;
import org.example.validators.UserValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendshipController implements Observer<FriendshipChangeEvent>  {
    PagingUserService userService;
    PagingFriendshipService friendshipService;
    PageImplementation<User> page;
    ObservableList<User> model = FXCollections.observableArrayList();
    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, String> tableColumnFirstName;
    @FXML
    TableColumn<User, String> tableColumnLastName;
    @FXML
    ComboBox<Integer> pageSize;
    User selectedUser;
    Stage dialogStage;

    public void setFriendshipService(PagingUserService userService, PagingFriendshipService friendshipService, Stage stage, User user) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        friendshipService.addObserver(this);
        this.dialogStage = stage;
        this.selectedUser = user;

        ArrayList<Long> friendsIds = userService.getFriends(selectedUser.getId());
        ArrayList<User> friends = new ArrayList<>();

        for (Long id : friendsIds) {
            friends.add(userService.findOne(id).get());
        }

        page = new PageImplementation<>(new PageableImplementation(0, 0), friends.stream());

        Integer nr = 1;
        ArrayList<Integer> pages = new ArrayList<>();
        for (User friend : friends) {
            pages.add(nr);
            nr++;
        }

        pageSize.getItems().setAll(pages);
        pageSize.getSelectionModel().selectLast();
        pageSize.setOnAction(event -> {
            page.getPageable().setPageNumber(0);
            initModel();
        });

        initModel();
    }

    private void initModel() {
        page.getPageable().setPageSize(pageSize.getSelectionModel().getSelectedItem());
        Page<Long> users = friendshipService.findAll(selectedUser.getId(), page.getPageable());
        ArrayList<User> friends = new ArrayList<>();

        for (Long id : users.getContent().toList()) {
            friends.add(userService.findOne(id).get());
        }
        model.setAll(friends);
    }

    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableView.setItems(model);
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                User selected = tableView.getSelectionModel().getSelectedItem();

                if (selected != null)
                    showMessageDialog(selectedUser, selected);
            }
        });
    }

    public void handleRemoveFriend() {
        User toBeRemoved = tableView.getSelectionModel().getSelectedItem();

        if (toBeRemoved != null) {
            Tuple<Long, Long> idFriendship = new Tuple<>(selectedUser.getId(), toBeRemoved.getId());

            Optional<Friendship> deleted = friendshipService.delete(idFriendship);

            if (deleted.isPresent())
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "The friendship has been successfully deleted!");
        } else
            MessageAlert.showErrorMessage(null, "No user was selected!");
    }

    public void handleSendFriendRequest() {
        showFriendRequestDialog(selectedUser);
    }

    public void handleNext() {
        if (page.getPageable().getPageNumber() < (pageSize.getItems().size() - 1)/page.getPageable().getPageSize()) {
            page.nextPageable();
            initModel();
        }
        else
            MessageAlert.showErrorMessage(null, "There is no other page!");
    }

    public void handlePrev() {
        if (page.getPageable().getPageNumber() > 0) {
            page.prevPageable();
            initModel();
        }
        else
            MessageAlert.showErrorMessage(null, "There is no other page!");
    }

    public void showFriendRequestDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/send-request-view.fxml"));
            AnchorPane layout = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setTitle("Send Friend Request");
            dialogStage.setScene(new Scene(layout));

            RequestValidator requestValidator = new RequestValidator();
            UserValidator userValidator = new UserValidator();
            UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Alexandra.27.09", userValidator);
            RequestDBPagingRepository requestDBRepository = new RequestDBPagingRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Alexandra.27.09", requestValidator, userDBRepository);
            RequestService requestService = new RequestService(requestDBRepository);
            SendFriendRequestController sendFriendRequestController = loader.getController();
            sendFriendRequestController.setRequestService(userService, requestService, dialogStage, user);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showMessageDialog(User user1, User user2) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/message-view.fxml"));
            AnchorPane layout = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Our Conversation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(layout));

            MessageValidator messageValidator = new MessageValidator();
            UserValidator userValidator = new UserValidator();
            UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Alexandra.27.09", userValidator);
            MessageDBPagingRepository messageDBRepository = new MessageDBPagingRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Alexandra.27.09", messageValidator, userDBRepository);
            MessageService messageService = new MessageService(messageDBRepository);

            MessageController messageController = loader.getController();
            messageController.setMessageService(userService, messageService, dialogStage, user1, user2);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        initModel();
    }
}
