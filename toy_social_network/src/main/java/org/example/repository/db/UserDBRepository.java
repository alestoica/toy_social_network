package org.example.repository.db;

import org.example.domain.User;
import org.example.repository.Repository;
import org.example.validators.UserValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class UserDBRepository implements Repository<Long, User> {
    protected String url;
    protected String username;
    protected String password;

    protected UserValidator validator;

    public UserDBRepository(String url, String username, String password, UserValidator validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    public ArrayList<User> getNotFriends(Long id) {
        ArrayList<User> notFriends = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {

            String placeholders = String.join(",", Collections.nCopies(getFriends(id).size() + 1, "?"));

            String query = "select * from users where id not in (" + placeholders + ")";

            try (PreparedStatement statement = connection.prepareStatement(query)) {

                ArrayList<Long> friends = getFriends(id);
                statement.setLong(1, id);
                int i = 2;
                for (Long idFriend : friends)
                    statement.setLong(i++, idFriend);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Long id_friend = resultSet.getLong("id");
                    notFriends.add(findOne(id_friend).get());
                }

            }

            return notFriends;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Long> getFriends(Long id) {
        ArrayList<Long> friends = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("""
                     select u.id from friendships f left join users u on f.id2 = u.id where f.id1 = ?
                     union
                     select u.id from friendships f left join users u on f.id1 = u.id where f.id2 = ?;""");) {

            statement.setLong(1, id);
            statement.setLong(2, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id_friend = resultSet.getLong("id");
                friends.add(id_friend);
            }

            return friends;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findOne(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users where id = ?");) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");

                User user = new User(firstName, lastName, username, password);
                user.setId(id);
                user.setFriends(getFriends(id));

                return Optional.of(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        ArrayList<User> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery();) {

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

            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> save(User entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("insert into users (id, first_name, last_name, username, password) values (?, ?, ?, ?, ?);");) {

            statement.setLong(1, entity.getId());
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getLastName());
            statement.setString(4, entity.getUsername());
            statement.setString(5, entity.getPassword());

            validator.validate(entity);

            int response = statement.executeUpdate();

            return response == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> delete(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("delete from users where id = ?");) {

            statement.setLong(1, id);

            User user = findOne(id).get();
            int response = statement.executeUpdate();
            return response == 1 ? Optional.of(user) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(Long id, User entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("update users set first_name = ?, last_name = ? where id = ?");) {

            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setLong(3, id);

            validator.validate(entity);

            int response = statement.executeUpdate();
            entity.setFriends(getFriends(id));
            return response == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
