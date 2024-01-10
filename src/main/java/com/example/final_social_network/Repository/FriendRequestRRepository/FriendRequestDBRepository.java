package com.example.final_social_network.Repository.FriendRequestRRepository;

import com.example.final_social_network.Domain.*;
import com.example.final_social_network.Repository.Repository;
import com.example.final_social_network.utils.FriendRequestStatus.FriendRequestStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendRequestDBRepository implements Repository<Tuple<Long, Long>, FriendRequest> {
    private final String url;
    private final String username;
    private final String password;

    public FriendRequestDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<FriendRequest> findOne(Tuple<Long, Long> ID) {
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM friend_request " + "WHERE " +
                        "from_id =? AND to_id = ?")
        ) {
            statement.setLong(1, ID.getLeft());
            statement.setLong(2, ID.getRight());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long from_id = resultSet.getLong("from_id");
                Long to_id = resultSet.getLong("to_id");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                String statusString = resultSet.getString("status");
                FriendRequestStatus status = FriendRequestStatus.valueOf(statusString.toUpperCase());
                FriendRequest friendRequest = new FriendRequest(new Tuple<>(from_id, to_id), date, status);
                return Optional.of(friendRequest);
            } else return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<FriendRequest> getAll() {
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM friend_request")
        ){
            ResultSet resultSet = statement.executeQuery();
            List<FriendRequest> friendRequests = new ArrayList<>();
            while (resultSet.next()) {
                Long from_id = resultSet.getLong("from_id");
                Long to_id = resultSet.getLong("to_id");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                String statusString = resultSet.getString("status");
                FriendRequestStatus status = FriendRequestStatus.valueOf(statusString.toUpperCase());
                FriendRequest friendRequest = new FriendRequest(new Tuple<>(from_id, to_id), date, status);
                friendRequests.add(friendRequest);
            }
            return friendRequests;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> add(FriendRequest entity) {
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("INSERT INTO friend_request(from_id, to_id, date, status)" +
                        "VALUES (?, ?, ?, ?)")
        ){
            statement.setLong(1, entity.getId().getLeft());
            statement.setLong(2, entity.getId().getRight());
            statement.setDate(3, Date.valueOf(entity.getDateOfRequest()));
            statement.setString(4, FriendRequestStatus.PENDING.toString());
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.of(entity) : Optional.empty();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Long, Long> ID ) {
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM friend_request WHERE " +
                     "from_id = ? AND to_id = ?")

        ) {
            preparedStatement.setLong(1, ID.getLeft());
            preparedStatement.setLong(2, ID.getRight());
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows == 0 ? Optional.empty() : findOne(ID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        Optional<FriendRequest> friendRequest= this.findOne(entity.getId());
        if (friendRequest.isPresent()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("UPDATE friend_request SET status = ?, " +
                         "date = ? " +
                         " WHERE from_id = ? AND to_id = ?")) {
                statement.setString(1, entity.getStatus().toString());
                statement.setDate(2, Date.valueOf(entity.getDateOfRequest()));
                statement.setLong(3, friendRequest.get().getId().getLeft());
                statement.setLong(4, friendRequest.get().getId().getRight());
                int affectedRows = statement.executeUpdate();
                return affectedRows == 0 ? Optional.empty(): friendRequest;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else return Optional.empty();
    }
    public Iterable<FriendRequest> getAllFriendRequestOfUser(Long idUSer) {
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM friend_request WHERE to_id = ?" +
                        " AND status = ?")
        ){
            statement.setLong(1, idUSer);
            statement.setString(2,FriendRequestStatus.PENDING.toString());
            ResultSet resultSet = statement.executeQuery();
            List<FriendRequest> friendRequests = new ArrayList<>();
            while (resultSet.next()) {
                Long from_id = resultSet.getLong("from_id");
                Long to_id = resultSet.getLong("to_id");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                String statusString = resultSet.getString("status");
                FriendRequestStatus status = FriendRequestStatus.valueOf(statusString.toUpperCase());
                FriendRequest friendRequest = new FriendRequest(new Tuple<>(from_id, to_id), date, status);
                friendRequests.add(friendRequest);
            }
            return friendRequests;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
