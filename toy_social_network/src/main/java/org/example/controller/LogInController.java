package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.domain.User;
import org.example.repository.db.FriendshipDBPagingRepository;
import org.example.service.PagingFriendshipService;
import org.example.service.PagingUserService;
import org.example.validators.FriendshipValidator;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

public class LogInController {
    PagingUserService userService;
    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private TextField textFieldPassword;
    User selectedUser;
    Stage dialogStage;

    public void setUserService(PagingUserService userService, Stage stage, User user) {
        this.userService = userService;
        this.dialogStage = stage;
        this.selectedUser = user;

        textFieldId.setText(selectedUser.getId().toString());
        textFieldId.setEditable(false);

        textFieldUsername.setText(selectedUser.getUsername());
        textFieldUsername.setEditable(false);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashedBytes = digest.digest(password.getBytes());

            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    public void handleLogIn() {
        Long id = Long.valueOf(textFieldId.getText());
        String password = textFieldPassword.getText();

        Optional<User> user = userService.findOne(id);

        String hashedPassword = hashPassword(password);

        if (Objects.equals(hashedPassword, user.get().getPassword()))
            showAccountDialog(selectedUser);
        else
            MessageAlert.showErrorMessage(null, "Wrong password!");
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }

    public void showAccountDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/account-view.fxml"));
            AnchorPane layout = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("My Account");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(layout));

            AccountController accountController = loader.getController();

            FriendshipValidator friendshipValidator = new FriendshipValidator();
            FriendshipDBPagingRepository friendshipDBRepository = new FriendshipDBPagingRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Alexandra.27.09", friendshipValidator);
            PagingFriendshipService friendshipService = new PagingFriendshipService(friendshipDBRepository);

            accountController.setAccountService(userService, friendshipService, dialogStage, user);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
