package org.example.repository.db;

import org.example.domain.Message;
import org.example.domain.User;
import org.example.repository.Repository;
import org.example.repository.paging.Page;
import org.example.repository.paging.PageImplementation;
import org.example.repository.paging.Pageable;
import org.example.validators.MessageValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MessageDBRepository implements Repository<Long, Message> {
    protected String url;
    protected String username;
    protected String password;
    protected MessageValidator validator;
    private UserDBRepository userDBRepository;

//    public MessageDBRepository(String url, String username, String password, MessageValidator validator) {
//        this.url = url;
//        this.username = username;
//        this.password = password;
//        this.validator = validator;
//    }

    public MessageDBRepository(String url, String username, String password, MessageValidator validator, UserDBRepository userDBRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.userDBRepository = userDBRepository;
    }

    public Page<Message> findAll(Long id1, Long id2, Pageable pageable) {
        ArrayList<Message> messages = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     """
                         select * from messages where "from" = ? and ? = any("to")
                         union
                         select * from messages where "from" = ? and ? = any("to")
                         order by date
                         limit ? offset ?;""");) {

            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id2);
            statement.setLong(4, id1);
            statement.setInt(5, pageable.getPageSize());
            statement.setInt(6, pageable.getPageNumber() * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long from = resultSet.getLong("from");
                Optional<User> userFrom = userDBRepository.findOne(from);

                Array arrayTo = resultSet.getArray("to");
                Long[] arrayData = (Long[]) arrayTo.getArray();
                ArrayList<Long> to = new ArrayList<>(List.of(arrayData));

                Long idMessage = resultSet.getLong("id");

                String sentMessage = resultSet.getString("message");

                String date = resultSet.getString("date");

                Long reply = resultSet.getLong("reply");

                if (userFrom.isPresent()) {
                    Message message = new Message(userFrom.get(), to, sentMessage);
                    message.setId(idMessage);
                    message.setDate(date);
                    message.setReply(reply);

                    messages.add(message);
                }
            }

            return new PageImplementation<>(pageable, messages.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    public ArrayList<Message> findAllForTwoUsers(Long id1, Long id2) {
//        ArrayList<Message> messages = new ArrayList<>();
//
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement(
//                     """
//                         select * from messages where "from" = ? union
//                         select * from messages where "from" = ?
//                         order by date limit 1;
//                         """);) {
//
//            statement.setLong(1, id1);
//            statement.setLong(2, id2);
//            ResultSet resultSet = statement.executeQuery();
//
//            if (resultSet.next()) {
//                Long from = resultSet.getLong("from");
//
//                Array arrayTo = resultSet.getArray("to");
//                Long[] arrayData = (Long[]) arrayTo.getArray();
//                ArrayList<Long> to = new ArrayList<>(List.of(arrayData));
//
//                Long idMessage = resultSet.getLong("id");
//
//                String sentMessage = resultSet.getString("message");
//
//                String date = resultSet.getString("date");
//
//                Long reply = resultSet.getLong("reply");
//
//                Message message = new Message(from, to, sentMessage);
//                message.setId(idMessage);
//                message.setDate(date);
//                message.setReply(reply);
//
//                if (reply != 0 && Objects.equals(findOne(reply).get().getFrom(), id2))
//                    messages.add(message);
//
//                Long id = id2;
//
//                while (reply != 0) {
//                    message = findOne(reply).get();
//
//                    if (Objects.equals(message.getFrom(), id)) {
//                        messages.add(message);
//                    }
//
//                    if (Objects.equals(id, id2))
//                        id = id1;
//                    else
//                        id = id2;
//
//                    reply = message.getReply();
//                }
//            }
//
//            return messages;
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public Iterable<Message> findAllForTwoUsers(Long id1, Long id2) {
//        ArrayList<Message> messages = new ArrayList<>();
//
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement("select * from messages where \"from\" = ? union select * from messages where \"from\" = ?");) {
//
//            statement.setLong(1, id1);
//            statement.setLong(2, id2);
//            ResultSet resultSet = statement.executeQuery();
//
//            while (resultSet.next()) {
//                Long from = resultSet.getLong("from");
//
//                Array arrayTo = resultSet.getArray("to");
//                Long[] arrayData = (Long[]) arrayTo.getArray();
//                ArrayList<Long> to = new ArrayList<>(List.of(arrayData));
//
//                for (Long idUser : to) {
//                    if (from.equals(id1) && idUser.equals(id2) || from.equals(id2) && idUser.equals(id1)) {
//                        Long idMessage = resultSet.getLong("id");
//                        String sentMessage = resultSet.getString("message");
//                        String date = resultSet.getString("date");
//                        Long reply = resultSet.getLong("reply");
//
//                        Message message = new Message(from, to, sentMessage);
//                        message.setId(idMessage);
//                        message.setDate(date);
//                        message.setReply(reply);
//
//                        messages.add(message);
//                    }
//                }
//            }
//
//            return messages;
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public Iterable<Message> findAllForTwoUsers(Long id1, Long id2) {
//        ArrayList<Message> messages = new ArrayList<>();
//
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement("select * from messages where \"from\" = ? and reply = ? union select * from messages where \"from\" = ? and reply = ?");) {
//
//            statement.setLong(1, id1);
//            statement.setLong(2, id2);
//            statement.setLong(3, id2);
//            statement.setLong(4, id1);
//            ResultSet resultSet = statement.executeQuery();
//
//            while (resultSet.next()) {
//                Long idMessage = resultSet.getLong("id");
//                Long from = resultSet.getLong("from");
//
//                Array arrayTo = resultSet.getArray("to");
//                Long[] arrayData = (Long[]) arrayTo.getArray();
//                ArrayList<Long> to = new ArrayList<>(List.of(arrayData));
//
//                String sentMessage = resultSet.getString("message");
//                String date = resultSet.getString("date");
//                Long reply = resultSet.getLong("reply");
//
//                Message message = new Message(from, to, sentMessage);
//                message.setId(idMessage);
//                message.setDate(date);
//                message.setReply(reply);
//
//                messages.add(message);
//            }
//
//            return messages;
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public Optional<Message> findOne(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from messages where id = ?");) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Long from = resultSet.getLong("from");
                Optional<User> userFrom = userDBRepository.findOne(from);

                Array arrayTo = resultSet.getArray("to");
                Long[] arrayData = (Long[]) arrayTo.getArray();
                ArrayList<Long> to = new ArrayList<>(List.of(arrayData));

                String sentMessage = resultSet.getString("message");

                String date = resultSet.getString("date");

                Long reply = resultSet.getLong("reply");

                if (userFrom.isPresent()) {
                    Message message = new Message(userFrom.get(), to, sentMessage);
                    message.setId(id);
                    message.setDate(date);
                    message.setReply(reply);

                    return Optional.of(message);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Message> findAll() {
        ArrayList<Message> messages = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from messages");
             ResultSet resultSet = statement.executeQuery();) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");

                Long from = resultSet.getLong("from");
                Optional<User> userFrom = userDBRepository.findOne(from);

                Array arrayTo = resultSet.getArray("to");
                Long[] arrayData = (Long[]) arrayTo.getArray();
                ArrayList<Long> to = new ArrayList<>(List.of(arrayData));

                String sentMessage = resultSet.getString("message");

                String date = resultSet.getString("date");

                Long reply = resultSet.getLong("reply");

                if (userFrom.isPresent()) {
                    Message message = new Message(userFrom.get(), to, sentMessage);
                    message.setId(id);
                    message.setDate(date);
                    message.setReply(reply);

                    messages.add(message);
                }
            }

            return messages;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> save(Message entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("insert into messages (id, \"from\", \"to\", message, date) values (?, ?, ?, ?, ?)");) {

            statement.setLong(1, entity.getId());
            statement.setLong(2, entity.getFrom().getId());
            statement.setArray(3, connection.createArrayOf("BIGINT", entity.getTo().toArray()));
            statement.setString(4, entity.getMessage());
            statement.setString(5, entity.getDate());
//            statement.setLong(6, entity.getReply());

            validator.validate(entity);

            int response = statement.executeUpdate();

            return response == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> delete(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("delete from messages where id = ?");) {

            statement.setLong(1, id);

            Message message = findOne(id).get();
            int response = statement.executeUpdate();
            return response == 1 ? Optional.of(message) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> update(Long aLong, Message entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("update messages set reply = ? where id = ?");) {

            statement.setLong(1, entity.getReply());
            statement.setLong(2, entity.getId());

            validator.validate(entity);

            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

