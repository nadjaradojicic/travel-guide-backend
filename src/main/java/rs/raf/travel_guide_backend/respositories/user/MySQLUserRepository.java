package rs.raf.travel_guide_backend.respositories.user;

import rs.raf.travel_guide_backend.entities.Role;
import rs.raf.travel_guide_backend.entities.User;
import rs.raf.travel_guide_backend.respositories.MySQLAbstractRepository;

import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLUserRepository extends MySQLAbstractRepository implements UserRepository{

    @Override
    public User findUserByEmail(String email) {
        User user = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM users where email = ?");
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String emaill = resultSet.getString("email");
                String name = resultSet.getString("name");
                String surname = resultSet.getString("surname");
                String role = resultSet.getString("role");
                Boolean status = resultSet.getBoolean("status");
                String hashedPassword = resultSet.getString("hashedPassword");

                user = new User(userId, name, surname, emaill, Role.valueOf(role), status, hashedPassword);

            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return user;
    }


    @Override
    public User findUserById(Integer user_id){
        User user = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM users where id = ?");
            preparedStatement.setInt(1, user_id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String name = resultSet.getString("name");
                String surname = resultSet.getString("surname");
                String role = resultSet.getString("role");
                Boolean status = resultSet.getBoolean("status");
                String hashedPassword = resultSet.getString("hashedPassword");

                user = new User(userId, name, surname, email, Role.valueOf(role), status, hashedPassword);

            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users  = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = this.newConnection();

            String sqlQuery = "SELECT * from users";
            preparedStatement = connection.prepareStatement(sqlQuery);
            resultSet = preparedStatement.executeQuery(sqlQuery);

            while (resultSet.next()){
                int user_id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String name = resultSet.getString("name");
                String surname = resultSet.getString("surname");
                String role = resultSet.getString("role");
                Boolean status = resultSet.getBoolean("status");

                users.add(new User(user_id, name, surname, email, Role.valueOf(role), status));
            }

            connection.close();
            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection(connection);
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
        }

        return users;
    }

    @Override
    public Response addUser(User user){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();

            String[] generatedColumns = {"id"};

            preparedStatement = connection.prepareStatement("INSERT INTO users (email, name, surname, role, hashedPassword, status) VALUES(?, ?, ?, ?, ?, ?)", generatedColumns);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getSurname());
            preparedStatement.setString(4, user.getRole().toString());
            preparedStatement.setString(5, user.getHashedPassword());
            preparedStatement.setBoolean(6, user.getStatus());
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }

            return Response.ok(user).build();

        } catch (SQLIntegrityConstraintViolationException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("User with this email already exists.")
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error while adding a new user.")
                    .build();
        } finally {
            closeResources(resultSet, preparedStatement, connection);
        }
    }

    private void closeResources(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing Connection: " + e.getMessage());
            }
        }
    }


    @Override
    public void changeStatus(Integer user_id) {
        User user = findUserById(user_id);
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = this.newConnection();
            String sqlQuery = "UPDATE users SET status = ? WHERE id = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);

            boolean newStatus = !Boolean.TRUE.equals(user.getStatus());
            preparedStatement.setBoolean(1, newStatus);
            preparedStatement.setInt(2, user_id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
    }


    @Override
    public User updateUser(Integer id, User updatedUser) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        User oldUser = findUserById(id);
        String hashedPassword = oldUser.getHashedPassword();

        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement(
                    "UPDATE users SET email = ?, name = ?, surname = ?, role = ?, hashedPassword = ? WHERE id = ?"
            );
            preparedStatement.setString(1, updatedUser.getEmail());
            preparedStatement.setString(2, updatedUser.getName());
            preparedStatement.setString(3, updatedUser.getSurname());
            preparedStatement.setString(4, updatedUser.getRole().toString());
            preparedStatement.setString(5, hashedPassword);
            preparedStatement.setInt(6, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return updatedUser;
    }


}
