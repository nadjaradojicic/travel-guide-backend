package rs.raf.travel_guide_backend.respositories.activity;

import rs.raf.travel_guide_backend.entities.Activity;
import rs.raf.travel_guide_backend.respositories.MySQLAbstractRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLActivityRepository extends MySQLAbstractRepository implements ActivityRepository {

    @Override
    public List<Activity> getAllActivities() {

        List<Activity> activities = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            String sqlQuery = "SELECT * FROM activities";
            statement = connection.prepareStatement(sqlQuery);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                Activity activity = new Activity(id, name);
                activities.add(activity);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(statement);
            this.closeConnection(connection);
        }

        return activities;
    }

    @Override
    public Activity findActivityById(Integer activity_id) {
        Activity activity = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            String sqlQuery = "SELECT * FROM activities WHERE id = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, activity_id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                activity = new Activity(id, name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return activity;
    }

    @Override
    public List<Activity> findActivitiesForPost(Integer postId) {

        List<Activity> postActivities = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            String sqlQuery =
                    "SELECT a.id, a.name " +
                            "FROM post_activities AS pa " +
                            "JOIN activities AS a ON a.id = pa.activity_id " +
                            "WHERE pa.post_id = ? " +
                            "ORDER BY a.name";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, postId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                Activity activity = new Activity(id, name);
                postActivities.add(activity);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return postActivities;
    }

    @Override
    public void addActivity(Activity activity) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            String[] generatedColumns = {"id"};

            String sqlQuery = "INSERT INTO activities (name) VALUES (?)";
            preparedStatement = connection.prepareStatement(sqlQuery, generatedColumns);
            preparedStatement.setString(1, activity.getName());
            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                activity.setId(id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
    }

    @Override
    public void deleteActivity(Integer activity_id) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = this.newConnection();
            String sqlQuery = "DELETE FROM activities WHERE id = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, activity_id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
    }
}
