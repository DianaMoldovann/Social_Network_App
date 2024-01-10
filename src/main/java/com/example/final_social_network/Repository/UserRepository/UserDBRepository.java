package com.example.final_social_network.Repository.UserRepository;

import com.example.final_social_network.Domain.User;
import com.example.final_social_network.Repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDBRepository implements Repository<Long, User> {
    private final String url;
    private final String username;
    private final String password;

    public UserDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Optional<User> findOne(Long ID) {
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM users " + "WHERE id = ?")
        ) {
            statement.setInt(1, Math.toIntExact(ID));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User u = new User(firstName, lastName, email, password);
                u.setId(ID);
                return Optional.of(u);
            } else return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> findOne(String email) {
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM users " + "WHERE email = ?")
        ) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long ID= resultSet.getLong("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String password = resultSet.getString("password");
                User u = new User(firstName, lastName, email, password);
                u.setId(ID);
                return Optional.of(u);
            } else return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<User> getAll() {
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM users")
        ) {
            ResultSet resultSet = statement.executeQuery();
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                Long ID = resultSet.getLong("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User u = new User(firstName, lastName, email, password);
                u.setId(ID);
                u.setId(ID);
                users.add(u);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> add(User entity) {
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("INSERT INTO users (firstName, lastName, email, password)" +
                        "VALUES (?, ?, ?, ?)")
        ) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getPassword());
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.of(entity) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> delete(Long ID) {
        Optional<User> user = this.findOne(ID);
        if (user.isPresent()) {
            try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users WHERE id = ?")

            ) {
                preparedStatement.setLong(1, ID);
                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows == 0 ? Optional.empty() : user;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> update(User entity) {
        Optional<User> user = this.findOne(entity.getId());
        if (user.isPresent()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("UPDATE users SET firstname = ?, lastname" +
                         "= ?, password = ? WHERE id = ?")) {
                statement.setString(1, entity.getFirstName());
                statement.setString(2, entity.getLastName());
                statement.setString(3, entity.getPassword());
                statement.setLong(4, entity.getId());
                int affectedRows = statement.executeUpdate();
                return affectedRows == 0 ? Optional.empty() : user;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else return Optional.of(entity);

    }

    public Iterable<User> getAllFiltered(String filter) {
        filter = "%" + filter + "%";
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM users " +
                        "WHERE CONCAT(firstname, ' ', lastname) LIKE ? ")
        ) {
            statement.setString(1, filter);
            ResultSet resultSet = statement.executeQuery();
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                Long ID = resultSet.getLong("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User u = new User(firstName, lastName, email, password);
                u.setId(ID);
                u.setId(ID);
                users.add(u);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
