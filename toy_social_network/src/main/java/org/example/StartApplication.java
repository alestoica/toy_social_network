package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.controller.UserController;
import org.example.repository.db.UserDBRepository;
import org.example.service.PagingUserService;
import org.example.service.UserService;
import org.example.validators.UserValidator;
import org.example.domain.User;
import org.example.repository.db.UserDBPagingRepository;
import org.example.repository.paging.Page;
import org.example.repository.paging.Pageable;
import org.example.repository.paging.PageableImplementation;
import org.example.repository.paging.PagingRepository;

import java.io.IOException;

public class StartApplication extends Application {
    UserValidator userValidator;
//    UserDBRepository userRepository;
    UserDBPagingRepository userRepository;
//    UserService userService;
    PagingUserService userService;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        userValidator = new UserValidator();
//        userRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Alexandra.27.09", userValidator);

        userRepository = new UserDBPagingRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Alexandra.27.09", userValidator);

//        Pageable pageable = new PageableImplementation(1, 3);
//        Page<User> page = userRepository.findAll(pageable);
//        page.getContent().forEach(System.out::println);

        userService = new PagingUserService(userRepository);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/org/example/user-view.fxml"));
        AnchorPane layout = loader.load();
        primaryStage.setTitle("Social Network");
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);

        UserController userController = loader.getController();
        userController.setUserService(userService);

        primaryStage.setWidth(800);
        primaryStage.show();
    }
}
