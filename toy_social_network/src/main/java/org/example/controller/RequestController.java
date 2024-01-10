package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.domain.*;
import org.example.repository.db.FriendshipDBRepository;
import org.example.repository.db.MessageDBRepository;
import org.example.repository.db.UserDBRepository;
import org.example.repository.paging.Page;
import org.example.repository.paging.PageImplementation;
import org.example.repository.paging.PageableImplementation;
import org.example.service.*;
import org.example.utils.events.RequestChangeEvent;
import org.example.utils.events.UserChangeEvent;
import org.example.utils.observer.Observer;
import org.example.validators.FriendshipValidator;
import org.example.validators.MessageValidator;
import org.example.validators.UserValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RequestController implements Observer<RequestChangeEvent> {
    UserService userService;
    PagingRequestService requestService;
    FriendshipService friendshipService;
    PageImplementation<Request> page;
    ObservableList<Request> model = FXCollections.observableArrayList();
    @FXML
    TableView<Request> tableView;
    @FXML
    TableColumn<User, String> tableColumnFrom;
    @FXML
    TableColumn<Request, String> tableColumnStatus;
    @FXML
    ComboBox<Integer> pageSize;
    User selectedUser;
    Stage dialogStage;

    public void setRequestService(UserService userService, PagingRequestService requestService, FriendshipService friendshipService, Stage stage, User user) {
        this.userService = userService;
        this.requestService = requestService;
        this.requestService.addObserver(this);
        this.friendshipService = friendshipService;
        this.dialogStage = stage;
        this.selectedUser = user;

        ArrayList<Request> requests = new ArrayList<>(requestService.findAll(selectedUser.getId(), new PageableImplementation(0, 100)).getContent().toList());
        page = new PageImplementation<>(new PageableImplementation(0, 0), requests.stream());

        Integer nr = 1;
        ArrayList<Integer> pages = new ArrayList<>();
        for (Request request : requests) {
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
        Page<Request> requests = requestService.findAll(selectedUser.getId(), page.getPageable());
        model.setAll(requests.getContent().collect(Collectors.toList()));
    }

    public void initialize() {
        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<>("from"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableView.setItems(model);
    }

    public void handleAcceptRequest() {
        Request toBeAccepted = tableView.getSelectionModel().getSelectedItem();

        if (toBeAccepted != null) {
            Long userId1 = toBeAccepted.getFrom().getId();
            Long userId2 = toBeAccepted.getTo().getId();
            Tuple<Long, Long> idFriendship = new Tuple<>(userId1, userId2);
            Friendship friendship = new Friendship();
            friendship.setId(idFriendship);
            Optional<Friendship> accepted = friendshipService.save(friendship);

            toBeAccepted.setStatus("accepted");
            requestService.update(toBeAccepted.getId(), toBeAccepted);

            if (accepted.isEmpty())
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Accept Request", "The request has been accepted and the friendship has been successfully added!");
        } else
            MessageAlert.showErrorMessage(null, "No request was selected!");
    }

    public void handleDeclineRequest() {
        Request toBeDeclined = tableView.getSelectionModel().getSelectedItem();

        if (toBeDeclined != null) {
            toBeDeclined.setStatus("declined");
            requestService.update(toBeDeclined.getId(), toBeDeclined);

            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Decline Request", "The request has been declined!");
        } else
            MessageAlert.showErrorMessage(null, "No request was selected!");
    }

    public void handleDeleteRequest() {
        Request toBeDeleted = tableView.getSelectionModel().getSelectedItem();

        if (toBeDeleted != null) {
            Optional<Request> deleted = requestService.delete(toBeDeleted.getId());

            if (deleted.isPresent())
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete Request", "The request has been successfully deleted!");
        } else
            MessageAlert.showErrorMessage(null, "No request was selected!");
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
    public void update(RequestChangeEvent requestChangeEvent) {
        initModel();
    }
}
