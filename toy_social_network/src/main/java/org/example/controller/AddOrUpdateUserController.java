package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.domain.User;
import org.example.service.UserService;
import org.example.validators.ValidationException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AddOrUpdateUserController {
    UserService userService;
    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private TextField textFieldPassword;
    User selectedUser;
    Stage dialogStage;

    public void setUserService(UserService userService, Stage stage, User user) {
        this.userService = userService;
        this.dialogStage = stage;
        this.selectedUser = user;

        if (selectedUser != null) {
            dialogStage.setTitle("Update User");
            setFields(selectedUser);
            textFieldId.setEditable(false);
        } else {
            dialogStage.setTitle("Add User");
            Random random = new Random();
            Long id = random.nextLong(100000);
            textFieldId.setText(id.toString());
            textFieldId.setEditable(false);
        }
    }

    private void setFields(User user) {
        textFieldId.setText(user.getId().toString());
        textFieldFirstName.setText(user.getFirstName());
        textFieldLastName.setText(user.getLastName());
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
    public void handleAddOrUpdate() {
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        Long id = Long.valueOf(textFieldId.getText());
        String username = textFieldUsername.getText();
        String password = hashPassword(textFieldPassword.getText());

        User user = new User(firstName, lastName, username, password);
        user.setId(id);

        if (selectedUser != null)
            updateUser(user);
        else
            saveUser(user);
    }

    private void saveUser(User user) {
        try {
            Optional<User> savedUser = this.userService.save(user);

            if (savedUser.isEmpty())
                dialogStage.close();

            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Save User","The user has been saved!");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        dialogStage.close();
    }

    private void updateUser(User user) {
        try {
            Optional<User> updatedUser = this.userService.update(user.getId(), user);

            if (updatedUser.isEmpty())
                dialogStage.close();

            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Update User","The user has been updated!");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        dialogStage.close();
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
