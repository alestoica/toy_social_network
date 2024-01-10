package org.example.repository.db;

import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.repository.Repository;
import org.example.repository.paging.Page;
import org.example.repository.paging.PageImplementation;
import org.example.repository.paging.Pageable;
import org.example.validators.FriendshipValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class FriendshipDBRepository implements Repository<Tuple<Long, Long>, Friendship> {
    protected String url;
    protected String username;
    protected String password;
    protected FriendshipValidator validator;

    public FriendshipDBRepository(String url, String username, String password, FriendshipValidator validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships where id1 = ? and id2 = ?");) {

            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String date = resultSet.getString("date");

                Friendship friendship = new Friendship();
                friendship.setId(id);
                friendship.setDate(date);

                return Optional.of(friendship);
            }

            statement.setLong(1, id.getRight());
            statement.setLong(2, id.getLeft());
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String date = resultSet.getString("date");

                Friendship friendship = new Friendship();
                Tuple<Long, Long> reversedID = new Tuple<>(id.getRight(), id.getLeft());
                friendship.setId(reversedID);
                friendship.setDate(date);

                return Optional.of(friendship);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Page<Long> findAll(Long id, Pageable pageable) {
        ArrayList<Long> friends = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("""
                     select u.id from friendships f left join users u on f.id2 = u.id where f.id1 = ?
                     union
                     select u.id from friendships f left join users u on f.id1 = u.id where f.id2 = ?
                     limit ? offset ?;""");) {

            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setInt(3, pageable.getPageSize());
            statement.setInt(4, pageable.getPageNumber() * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id_friend = resultSet.getLong("id");
                friends.add(id_friend);
            }

            return new PageImplementation<>(pageable, friends.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Friendship> findAll() {
        ArrayList<Friendship> friendships = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships");
             ResultSet resultSet = statement.executeQuery();) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String date = resultSet.getString("date");

                Friendship friendship = new Friendship();
                Tuple<Long, Long> id = new Tuple<>(id1, id2);
                friendship.setId(id);
                friendship.setDate(date);

                friendships.add(friendship);
            }

            return friendships;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("insert into friendships (id1, id2, date) values (?, ?, ?);");) {

            statement.setLong(1, entity.getId().getLeft());
            statement.setLong(2, entity.getId().getRight());
            statement.setString(3, entity.getDate());

            validator.validate(entity);

            if (findOne(new Tuple<>(entity.getId().getLeft(), entity.getId().getRight())).isPresent())
                return Optional.empty();

            int response = statement.executeUpdate();

            return response == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("delete from friendships where id1 = ? and id2 = ?");) {

            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());

            Friendship friendship = findOne(id).get();
            int response = statement.executeUpdate();

            if (response == 0) {
                statement.setLong(1, id.getRight());
                statement.setLong(2, id.getLeft());
                Tuple<Long, Long> reversedID = new Tuple<>(id.getRight(), id.getLeft());
                friendship = findOne(reversedID).get();
                response = statement.executeUpdate();
            }

            return response == 1 ? Optional.of(friendship) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> update(Tuple<Long, Long> longLongTuple, Friendship entity) {
        return Optional.empty();
    }
}
