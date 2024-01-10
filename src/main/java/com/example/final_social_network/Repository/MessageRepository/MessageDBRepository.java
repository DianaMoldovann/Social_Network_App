package com.example.final_social_network.Repository.MessageRepository;

import com.example.final_social_network.Domain.Message;
import com.example.final_social_network.Domain.MessageDTO;
import com.example.final_social_network.Repository.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDBRepository implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;

    public MessageDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Message> findOne(Long idMessage) {
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement(
                             "SELECT m.id, m.from_id, m.date, m.message, m.replied_message, sm.to_id " +
                                "FROM messages m " +
                                "JOIN sent_messages sm ON m.id = sm.message_id " +
                                "WHERE m.id = ?")
        ) {
            statement.setLong(1, idMessage);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long from = resultSet.getLong("from_id");
                LocalDate date= resultSet.getDate("date").toLocalDate();
                String stringMessage = resultSet.getString("message");
                Long replied_meessage = resultSet.getLong("replied_message");
                List<Long> toList = new ArrayList<>();
                toList.add(resultSet.getLong("to_id"));
                while (resultSet.next()) {
                    toList.add(resultSet.getLong("to_id"));
                }
                Message message = new Message(from, toList, stringMessage, date, replied_meessage);
                message.setId(idMessage);
                return Optional.of(message);
            } else return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Message> getAll() {
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement1 = connection.prepareStatement(
                        "SELECT * FROM messages m")
        ) {
            ResultSet resultSet1 = statement1.executeQuery();
            List<Message> allMessages = new ArrayList<>();
            if (resultSet1.next()) {
                long idMessage = resultSet1.getLong("id");
                Long from = resultSet1.getLong("from_id");
                LocalDate date= resultSet1.getDate("date").toLocalDate();
                String stringMessage = resultSet1.getString("message");
                Long replied_meessage = resultSet1.getLong("replied_message");
                List<Long> toList = new ArrayList<>();
                try (
                        PreparedStatement statement2 = connection.prepareStatement(
                                "SELECT sm.to_id FROM sent_messages sm WHERE message_id = ?")
                ){
                    statement1.setLong(1, idMessage);
                    ResultSet resultSet2 = statement2.executeQuery();
                    while (resultSet2.next()) {
                        toList.add(resultSet2.getLong("to_id"));
                    }
                    Message message = new Message(from, toList, stringMessage, date, replied_meessage);
                    message.setId(idMessage);
                    allMessages.add(message);
                }
            }
            return allMessages;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Message> add(Message entity) {
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement1 = connection.prepareStatement("INSERT INTO messages (from_id, message, date, replied_message)" +
                        "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            statement1.setLong(1, entity.getFrom());
            statement1.setString(2, entity.getMessage());
            statement1.setDate(3, Date.valueOf(entity.getData()));
            if (entity.getRepliedMessage() == null) {
                statement1.setNull(4, Types.NULL);
            }
            else {
                statement1.setLong(4, entity.getRepliedMessage());
            }
            int affectedRows = statement1.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = statement1.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long idMessage = generatedKeys.getLong(1);
                    for (Long to_user_id : entity.getTo()) {
                        try (
                                PreparedStatement statement2 = connection.prepareStatement(
                                        "INSERT INTO sent_messages(message_id, to_id)" +
                                                "VALUES (?, ?) ")
                        ) {
                            statement2.setLong(1, idMessage);
                            statement2.setLong(2, to_user_id);
                            statement2.executeUpdate();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return Optional.of(entity);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        @Override
    public Optional<Message> delete(Long idMessage) {
            try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM messages WHERE " +
                         "id = ?")

            ) {
                preparedStatement.setLong(1, idMessage);
                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows == 0 ? Optional.empty() : findOne(idMessage);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }



    /**
     * finds all users who have sent a message to the user whose id is the given argument
     * and all users to whom he has sent a message
     * all users with careers have an open chat
     */
    public Iterable<Long> findAllWhoHaveAChatWithUser(Long idUser) {
        try(
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(
                              "SELECT DISTINCT LEAST(m.from_id, sm.to_id) as id1, GREATEST(m.from_id, sm.to_id) as id2," +
                                      "MAX(m.date) AS max_date" +
                                 " FROM messages m" +
                                 " JOIN sent_messages sm on m.id = sm.message_id" +
                                 " WHERE sm.to_id = ? OR m.from_id = ?"+
                                 " GROUP BY id1, id2" +
                                 " ORDER BY max_date DESC"
                            )
        ) {
            statement.setLong(1, idUser);
            statement.setLong(2, idUser);
            ResultSet resultSet = statement.executeQuery();
            List<Long> usersWhoSentAMessageOrderByDate = new ArrayList<>();
            while (resultSet.next()) {
                long id1 = resultSet.getLong("id1");
                long id2 = resultSet.getLong("id2");
                Long other = (idUser == id2) ? id1 : id2;
                usersWhoSentAMessageOrderByDate.add(other);
            }
            return usersWhoSentAMessageOrderByDate;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * finds all messages between two users
     */
    public Iterable<MessageDTO> findAllMessagesBetweenTwoUsers(Long idUser, Long idFriend) {
        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT m.id, m.from_id, sm.to_id, m.message, m.date, m.replied_message FROM messages m " +
                                "JOIN sent_messages sm on m.id = sm.message_id" +
                                " WHERE (sm.to_id = ? AND m.from_id = ?) OR (sm.to_id = ? AND m.from_id = ?)"+
                                " ORDER BY m.date"
                )
        ) {
            statement.setLong(1, idUser);
            statement.setLong(2, idFriend);
            statement.setLong(3, idFriend);
            statement.setLong(4, idUser);
            ResultSet resultSet = statement.executeQuery();
            List<MessageDTO> messages = new ArrayList<>();
            while (resultSet.next()) {
                long idMessage = resultSet.getLong("id");
                long idFrom = resultSet.getLong("from_id");
                long idTo = resultSet.getLong("to_id");
                String stringMessage = resultSet.getString("message");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                Long idRepliedMessage = resultSet.getLong("replied_message");
                if (resultSet.wasNull()) {
                    // replied_message is NULL
                    idRepliedMessage = null;
                }
                MessageDTO messageDTO = new MessageDTO(idFrom, idTo, stringMessage, date, idRepliedMessage);
                messageDTO.setId(idMessage);
                messages.add(messageDTO);
            }
            return messages;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
