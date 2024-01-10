package org.example.controller;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.domain.Message;
import org.example.domain.User;
import org.example.repository.paging.Page;
import org.example.repository.paging.PageImplementation;
import org.example.repository.paging.PageableImplementation;
import org.example.service.FriendshipService;
import org.example.service.MessageService;
import org.example.service.UserService;
import org.example.utils.events.FriendshipChangeEvent;
import org.example.utils.events.MessageChangeEvent;
import org.example.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageController implements Observer<MessageChangeEvent> {
    UserService userService;
    MessageService messageService;
    PageImplementation<Message> page;
    ObservableList<Message> model = FXCollections.observableArrayList();
    @FXML
    TableView<Message> tableView;
    @FXML
    TableColumn<User, String> tableColumnFrom;
    @FXML
    TableColumn<Message, String> tableColumnMessage;
    @FXML
    Label chatLabel;
    @FXML
    TextField textFieldMessage;
    @FXML
    ComboBox<Integer> pageSize;
    User selectedUser1;
    User selectedUser2;
    Stage dialogStage;

    public void setMessageService(UserService userService, MessageService messageService, Stage stage, User user1, User user2) {
        this.userService = userService;
        this.messageService = messageService;
        this.messageService.addObserver(this);
        this.dialogStage = stage;
        this.selectedUser1 = user1;
        this.selectedUser2 = user2;
        this.chatLabel.setText("Chat with " + selectedUser2.getFirstName());

        ArrayList<Message> messages = new ArrayList<>(messageService.findAll(selectedUser1.getId(), selectedUser2.getId(), new PageableImplementation(0, 100)).getContent().toList());
        page = new PageImplementation<>(new PageableImplementation(0, 0), messages.stream());

        Integer nr = 1;
        ArrayList<Integer> pages = new ArrayList<>();
        for (Message message : messages) {
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
        Page<Message> messages = messageService.findAll(selectedUser1.getId(), selectedUser2.getId(), page.getPageable());
        model.setAll(messages.getContent().collect(Collectors.toList()));
    }

    public void initialize() {
        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<>("from"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        tableView.setItems(model);
    }

    public void handleDeleteMessage() {
        Message toBeDeleted = tableView.getSelectionModel().getSelectedItem();

        if (toBeDeleted != null) {
            Optional<Message> deleted = messageService.delete(toBeDeleted.getId());

            if (deleted.isPresent())
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "The message has been successfully deleted!");
        } else
            MessageAlert.showErrorMessage(null, "No message was selected!");
    }

    public void handleSendMessage() {
        ArrayList<Long> to = new ArrayList<>();
        to.add(selectedUser2.getId());
        Message toBeSent = new Message(selectedUser1, to, textFieldMessage.getText());
        Random random = new Random();
        Long id = random.nextLong(100000);
        toBeSent.setId(id);

        Optional<Message> sent = messageService.save(toBeSent);

        if (sent.isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Sent", "The message was sent!");
            textFieldMessage.clear();
        }
    }

    public void handleReplyMessage() {
        Message toBeReplied = tableView.getSelectionModel().getSelectedItem();

        if (toBeReplied != null) {
            ArrayList<Long> to = new ArrayList<>();
            to.add(selectedUser2.getId());
            Message toBeSent = new Message(selectedUser1, to, "reply for: " + toBeReplied.getMessage() + "\n" + textFieldMessage.getText());
            Random random = new Random();
            Long id = random.nextLong(100000);
            toBeSent.setId(id);

            Optional<Message> sent = messageService.save(toBeSent);

            toBeReplied.setReply(id);
            messageService.update(toBeReplied.getId(), toBeReplied);

            if (sent.isEmpty()) {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Sent", "The message was sent!");
                textFieldMessage.clear();
            }
        } else
            MessageAlert.showErrorMessage(null, "No message was selected!");
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
    public void update(MessageChangeEvent messageChangeEvent) {
        initModel();
    }
}
