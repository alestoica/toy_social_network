package org.example.repository.db;

import org.example.domain.Request;
import org.example.domain.User;
import org.example.repository.Repository;
import org.example.repository.paging.Page;
import org.example.repository.paging.PageImplementation;
import org.example.repository.paging.Pageable;
import org.example.validators.RequestValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class RequestDBRepository implements Repository<Long, Request> {
    protected String url;
    protected String username;
    protected String password;
    protected RequestValidator validator;
    private UserDBRepository userDBRepository;

    public RequestDBRepository(String url, String username, String password, RequestValidator validator, UserDBRepository userDBRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.userDBRepository = userDBRepository;
    }

    public Page<Request> findAll(Long id, Pageable pageable) {
        ArrayList<Request> requests = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from requests where \"to\" = ? limit ? offset ?");) {

            statement.setLong(1, id);
            statement.setInt(2, pageable.getPageSize());
            statement.setInt(3, pageable.getPageNumber() * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long idRequest = resultSet.getLong("id");

                Long from = resultSet.getLong("from");
                Optional<User> userFrom = userDBRepository.findOne(from);

                Long to = resultSet.getLong("to");
                Optional<User> userTo = userDBRepository.findOne(to);

                String status = resultSet.getString("status");

                if (userFrom.isPresent() && userTo.isPresent()) {
                    Request request = new Request(userFrom.get(), userTo.get());
                    request.setStatus(status);
                    request.setId(idRequest);

                    requests.add(request);
                }
            }

            return new PageImplementation<>(pageable, requests.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Request> findOne(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from requests where id = ?");) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Long from = resultSet.getLong("from");
                Optional<User> userFrom = userDBRepository.findOne(from);

                Long to = resultSet.getLong("to");
                Optional<User> userTo = userDBRepository.findOne(to);

                String status = resultSet.getString("status");

                if (userFrom.isPresent() && userTo.isPresent()) {
                    Request request = new Request(userFrom.get(), userTo.get());
                    request.setStatus(status);
                    request.setId(id);

                    return Optional.of(request);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Request> findAll() {
        ArrayList<Request> requests = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from requests");
             ResultSet resultSet = statement.executeQuery();) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");

                Long from = resultSet.getLong("from");
                Optional<User> userFrom = userDBRepository.findOne(from);

                Long to = resultSet.getLong("to");
                Optional<User> userTo = userDBRepository.findOne(to);

                String status = resultSet.getString("status");

                if (userFrom.isPresent() && userTo.isPresent()) {
                    Request request = new Request(userFrom.get(), userTo.get());
                    request.setStatus(status);
                    request.setId(id);

                    requests.add(request);
                }
            }

            return requests;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Request> save(Request entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("insert into requests (id, \"from\", \"to\", status) values (?, ?, ?, ?)");) {

            statement.setLong(1, entity.getId());
            statement.setLong(2, entity.getFrom().getId());
            statement.setLong(3, entity.getTo().getId());
            statement.setString(4, entity.getStatus());

            validator.validate(entity);

            int response = statement.executeUpdate();

            return response == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Request> delete(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("delete from requests where id = ?");) {

            statement.setLong(1, id);

            Request request = findOne(id).get();
            int response = statement.executeUpdate();
            return response == 1 ? Optional.of(request) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Request> update(Long aLong, Request entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("update requests set status = ? where id = ?");) {

            statement.setString(1, entity.getStatus());
            statement.setLong(2, entity.getId());

            validator.validate(entity);

            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

