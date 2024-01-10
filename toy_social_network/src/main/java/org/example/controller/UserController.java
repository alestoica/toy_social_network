package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.domain.User;
import org.example.repository.db.FriendshipDBPagingRepository;
import org.example.repository.db.FriendshipDBRepository;
import org.example.repository.paging.Page;
import org.example.repository.paging.PageImplementation;
import org.example.repository.paging.Pageable;
import org.example.repository.paging.PageableImplementation;
import org.example.service.FriendshipService;
import org.example.service.PagingFriendshipService;
import org.example.service.PagingUserService;
import org.example.service.UserService;
import org.example.utils.events.UserChangeEvent;
import org.example.utils.observer.Observer;
import org.example.validators.FriendshipValidator;

import javax.xml.transform.sax.TemplatesHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<UserChangeEvent> {
    PagingUserService userService;
    PageImplementation<User> page;
    ObservableList<User> model = FXCollections.observableArrayList();
    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, String> tableColumnUsername;
    @FXML
    ComboBox<Integer> pageSize;

    public void setUserService(PagingUserService userService) {
        this.userService = userService;
        this.userService.addObserver(this);

        ArrayList<User> users = new ArrayList<>();
        userService.findAll().forEach(users::add);
        page = new PageImplementation<>(new PageableImplementation(0, 0), users.stream());

        Integer nr = 1;
        ArrayList<Integer> pages = new ArrayList<>();
        for (User user : userService.findAll()) {
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
        Page<User> users = userService.findAll(page.getPageable());
        model.setAll(users.getContent().collect(Collectors.toList()));
    }

    public void initialize() {
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableColumnUsername.setStyle("-fx-alignment: CENTER;");
        tableView.setItems(model);
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                User selected = tableView.getSelectionModel().getSelectedItem();

                if (selected != null)
                    showLogInDialog(selected);
            }
        });
    }

    public void handleAddUser() {
        showUserAddOrUpdateDialog(null);
    }

    public void handleDeleteUser() {
        User toBeDeleted = tableView.getSelectionModel().getSelectedItem();

        if (toBeDeleted != null) {
            Optional<User> deleted = userService.delete(toBeDeleted.getId());

            if (deleted.isPresent())
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "The user has been successfully deleted!");
        } else
            MessageAlert.showErrorMessage(null, "No user was selected!");
    }

    public void handleUpdateUser() {
        User toBeUpdated = tableView.getSelectionModel().getSelectedItem();

        if (toBeUpdated != null)
            showUserAddOrUpdateDialog(toBeUpdated);
        else
            MessageAlert.showErrorMessage(null, "No user was selected!");
    }

    public void showUserAddOrUpdateDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/edit-user-view.fxml"));
            AnchorPane layout = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(layout));

            AddOrUpdateUserController addOrUpdateUserController = loader.getController();
            addOrUpdateUserController.setUserService(userService, dialogStage, user);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLogInDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/login-view.fxml"));
            AnchorPane layout = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Log In");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(layout));

            LogInController logInController = loader.getController();

            logInController.setUserService(userService, dialogStage, user);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public void update(UserChangeEvent userChangeEvent) {
        initModel();
    }
}
