package org.example.repository.db;

import org.example.domain.User;
import org.example.repository.paging.Page;
import org.example.repository.paging.PageImplementation;
import org.example.repository.paging.Pageable;
import org.example.repository.paging.PagingRepository;
import org.example.validators.UserValidator;
import org.example.validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UserDBPagingRepository extends UserDBRepository implements PagingRepository<Long, User> {
    public UserDBPagingRepository(String url, String username, String password, UserValidator validator) {
        super(url, username, password, validator);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        ArrayList<User> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users limit ? offset ?");) {

            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, pageable.getPageNumber() * pageable.getPageSize());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");

                User user = new User(firstName, lastName, username, password);
                user.setId(id);
                user.setFriends(getFriends(id));

                users.add(user);
            }

            return new PageImplementation<>(pageable, users.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
