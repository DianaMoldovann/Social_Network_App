package com.example.final_social_network.Repository.FriendshipRepository;

import com.example.final_social_network.Domain.Friendship;
import com.example.final_social_network.Domain.Tuple;
import com.example.final_social_network.Repository.FriendshipRepository.paging.Page;
import com.example.final_social_network.Repository.FriendshipRepository.paging.Pageable;
import com.example.final_social_network.Repository.FriendshipRepository.paging.PagingFriendshipRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipDBRepository implements PagingFriendshipRepository<Tuple<Long, Long>, Friendship> {
    private final String url;
    private final String username;
    private final String password;

    public FriendshipDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Iterable<Long> findFriends(Long id) {
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships WHERE " +
                        "id1 = ? OR id2 = ?")
        ){
            statement.setLong(1,id);
            statement.setLong(2,id);
            ResultSet resultSet = statement.executeQuery();
            List<Long> friends= new ArrayList<>();
            while (resultSet.next()) {
                long ID1 = resultSet.getLong("id1");
                long ID2 = resultSet.getLong("id2");
                if (ID1 == id)
                    friends.add(ID2);
                else
                    friends.add(ID1);
            }
            return friends;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Page<Friendship> findFriendsOnPage(Pageable pageable, Long id) {
        List<Friendship> friends= new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement pageStatement = connection.prepareStatement("SELECT * FROM friendships" +
                        " WHERE id1 = ? OR id2 = ?" +
                        " LIMIT ? OFFSET ?");
                PreparedStatement countStatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM friendships" +
                        " WHERE id1 = ? OR id2 = ?")
        ){
            pageStatement.setLong(1,id);
            pageStatement.setLong(2,id);
            pageStatement.setInt(3, pageable.getPageSize());
            int offset = pageable.getPageNr() == 0?0:pageable.getPageSize() * (pageable.getPageNr()-1);
            pageStatement.setInt(4, offset);
            countStatement.setLong(1,id);
            countStatement.setLong(2,id);
            try(
                    ResultSet pageResultSet = pageStatement.executeQuery();
                    ResultSet countResultSet = countStatement.executeQuery()
            ) {
                int count = 0;
                if (countResultSet.next()) {
                    count = countResultSet.getInt("count");
                }

                while (pageResultSet.next()) {
                    long ID1 = pageResultSet.getLong("id1");
                    long ID2 = pageResultSet.getLong("id2");
                    friends.add(findOne(new Tuple<>(ID1, ID2)).get());
                }
                return new Page<>(friends, count);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> longLongTuple) {
        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships " +
                        "WHERE (id1 = ? AND id2 = ?)  OR (id1 = ? AND id2 = ?)")
        ) {
            statement.setInt(1, Math.toIntExact(longLongTuple.getLeft()));
            statement.setInt(2, Math.toIntExact(longLongTuple.getRight()));
            statement.setInt(4, Math.toIntExact(longLongTuple.getLeft()));
            statement.setInt(3, Math.toIntExact(longLongTuple.getRight()));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                LocalDate date = resultSet.getDate("date").toLocalDate();
                Friendship friendship = new Friendship(longLongTuple, date);
                return Optional.of(friendship);
            }
            else return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Friendship> getAll() {
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships")
        ){
            ResultSet resultSet = statement.executeQuery();
            List<Friendship> friendships = new ArrayList<>();
            while (resultSet.next()) {
                Long ID1 = resultSet.getLong("id1");
                Long ID2 = resultSet.getLong("id2");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                Friendship friendship = new Friendship(new Tuple<>(ID1, ID2), date);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> add(Friendship entity) {
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("INSERT INTO friendships(id1, id2, date)" +
                        "VALUES (?, ?, ?)")
        ){
            statement.setLong(1, entity.getId().getLeft());
            statement.setLong(2, entity.getId().getRight());
            statement.setDate(3, Date.valueOf(entity.getDate()));
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.of(entity) : Optional.empty();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> longLongTuple) {
        Optional<Friendship> friendship = this.findOne(longLongTuple);
        if(friendship.isPresent()) {
            try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM friendships WHERE " +
                         "(id1 = ? AND id2 = ?) OR (id1 = ? AND id2 = ?)")

            ) {
                preparedStatement.setLong(1, longLongTuple.getLeft());
                preparedStatement.setLong(2, longLongTuple.getRight());
                preparedStatement.setLong(4, longLongTuple.getLeft());
                preparedStatement.setLong(3, longLongTuple.getRight());
                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows == 0 ? Optional.empty() : friendship;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        Optional<Friendship> friendship= this.findOne(entity.getId());
        if (friendship.isPresent()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("UPDATE friendships SET date = ?" +
                         " WHERE id1 = ? AND  id2 = ?")) {
                statement.setDate(1, Date.valueOf(entity.getDate()));
                statement.setLong(2, entity.getId().getLeft());
                statement.setLong(3, entity.getId().getRight());
                int affectedRows = statement.executeUpdate();
                return affectedRows == 0 ? Optional.empty(): Optional.of(entity);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else return Optional.of(entity);
    }

}
