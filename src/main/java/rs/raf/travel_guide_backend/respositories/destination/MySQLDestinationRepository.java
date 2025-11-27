package rs.raf.travel_guide_backend.respositories.destination;

import rs.raf.travel_guide_backend.entities.Destination;
import rs.raf.travel_guide_backend.entities.Post;
import rs.raf.travel_guide_backend.respositories.MySQLAbstractRepository;
import rs.raf.travel_guide_backend.respositories.post.PostRepository;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLDestinationRepository extends MySQLAbstractRepository implements DestinationRepository {

    @Inject
    private PostRepository postRepository;

    @Override
    public Response addDestination(Destination destination) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();

            String[] generatedColumns = {"id"};

            preparedStatement = connection.prepareStatement(
                    "INSERT INTO destinations (name, description, cover) VALUES(?, ?, ?)",
                    generatedColumns
            );
            preparedStatement.setString(1, destination.getName());
            preparedStatement.setString(2, destination.getDescription());
            preparedStatement.setString(3, destination.getCover());
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                destination.setId(resultSet.getInt(1));
            }

            return Response.ok(destination).build();

        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("SQLIntegrityConstraintViolationException: " + e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity("Destination with this name already exists.")
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error while adding new destination.")
                    .build();
        } finally {
            closeResources(resultSet, preparedStatement, connection);
        }
    }

    private void closeResources(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) {
        if (resultSet != null) {
            try { resultSet.close(); } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
        if (preparedStatement != null) {
            try { preparedStatement.close(); } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }
        }
        if (connection != null) {
            try { connection.close(); } catch (SQLException e) {
                System.err.println("Error closing Connection: " + e.getMessage());
            }
        }
    }

    @Override
    public Destination findDestination(Integer id) {
        Destination destination = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM destinations WHERE id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int destinationId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String cover = resultSet.getString("cover");

                destination = new Destination(destinationId, name, description);
                destination.setCover(cover);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return destination;
    }

    @Override
    public Response deleteDestination(Integer destination_id) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        List<Post> posts = postRepository.filterByDestination(destination_id);

        if (posts.isEmpty()) {
            try {
                connection = this.newConnection();

                String sqlQuery = "DELETE FROM destinations WHERE id = ?";
                preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setInt(1, destination_id);
                preparedStatement.executeUpdate();

                return Response.ok("Destination successfully deleted.").build();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                this.closeStatement(preparedStatement);
                this.closeConnection(connection);
            }
        }

        return Response.status(Response.Status.FORBIDDEN)
                .entity("Destination can not be deleted because there are connected posts with it.")
                .build();
    }

    @Override
    public List<Destination> getAllDestinations() {
        List<Destination> destinations = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();

            String sqlQuery = "SELECT * FROM destinations";
            preparedStatement = connection.prepareStatement(sqlQuery);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int destination_id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String cover = resultSet.getString("cover");

                Destination newDestination = new Destination(destination_id, name, description);
                newDestination.setCover(cover);
                destinations.add(newDestination);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return destinations;
    }

    @Override
    public Destination updateDestination(Integer id, Destination updatedDestination) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement(
                    "UPDATE destinations SET name = ?, description = ?, cover = ? WHERE id = ?"
            );
            preparedStatement.setString(1, updatedDestination.getName());
            preparedStatement.setString(2, updatedDestination.getDescription());
            preparedStatement.setString(3, updatedDestination.getCover());
            preparedStatement.setInt(4, id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return updatedDestination;
    }
}
