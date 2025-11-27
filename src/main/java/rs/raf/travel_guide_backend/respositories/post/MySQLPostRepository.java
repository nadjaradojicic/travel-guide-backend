package rs.raf.travel_guide_backend.respositories.post;

import rs.raf.travel_guide_backend.entities.Activity;
import rs.raf.travel_guide_backend.entities.Comment;
import rs.raf.travel_guide_backend.entities.Post;
import rs.raf.travel_guide_backend.respositories.MySQLAbstractRepository;
import rs.raf.travel_guide_backend.respositories.activity.ActivityRepository;
import rs.raf.travel_guide_backend.respositories.comment.CommentRepository;

import javax.inject.Inject;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MySQLPostRepository extends MySQLAbstractRepository implements PostRepository {

    @Inject
    private ActivityRepository activityRepository;

    @Inject
    private CommentRepository commentRepository;

    @Override
    public Post addPost(Integer destination_id, Post post) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatementPostActivity = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            connection.setAutoCommit(false);

            String[] generatedColumns = {"id"};
            LocalDate date = LocalDate.now();
            String dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            preparedStatement = connection.prepareStatement(
                    "INSERT INTO posts (title, content, author_id, created_at, destination_id, cover) VALUES(?, ?, ?, ?, ?, ?)",
                    generatedColumns
            );
            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setInt(3, post.getAuthor_id());
            preparedStatement.setString(4, dateString);
            preparedStatement.setInt(5, destination_id);
            preparedStatement.setString(6, post.getCover());

            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                post.setId(resultSet.getInt(1));

                List<Activity> activities = post.getActivities();
                if (activities != null && !activities.isEmpty()) {
                    String sqlPostActivity = "INSERT INTO post_activities (post_id, activity_id) VALUES (?, ?)";
                    preparedStatementPostActivity = connection.prepareStatement(sqlPostActivity);

                    int postId = resultSet.getInt(1);
                    for (Activity activity : activities) {
                        int activityId = activity.getId();
                        preparedStatementPostActivity.setInt(1, postId);
                        preparedStatementPostActivity.setInt(2, activityId);
                        preparedStatementPostActivity.executeUpdate();
                    }
                }
            }

            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatementPostActivity);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return post;
    }

    @Override
    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            String sql =
                    "SELECT p.id, p.cover, p.title, p.content, p.created_at, p.destination_id, p.author_id, " +
                            "CONCAT_WS(' ', u.name, u.surname) AS author_name " +
                            "FROM posts AS p " +
                            "JOIN users AS u ON p.author_id = u.id " +
                            "ORDER BY p.created_at DESC";

            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                int author_id = resultSet.getInt("author_id");
                String created_at = resultSet.getString("created_at");
                Integer destination_id = resultSet.getInt("destination_id");
                String author_name = resultSet.getString("author_name");
                String cover = resultSet.getString("cover");

                List<Comment> comments = commentRepository.findCommentsForPost(id);
                List<Activity> activities = activityRepository.findActivitiesForPost(id);

                Post newPost = new Post(id, title, content, author_id, created_at, destination_id);
                newPost.setComments(comments);
                newPost.setActivities(activities);
                newPost.setAuthor_name(author_name);
                newPost.setCover(cover);
                posts.add(newPost);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return posts;
    }

    @Override
    public Post findPost(Integer id) {
        Post post = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            String upit =
                    "SELECT p.id, p.title, p.cover, p.content, p.view_count, " +
                            "CONCAT_WS(' ', u.name, u.surname) AS author_name, " +
                            "p.author_id, p.created_at, p.destination_id " +
                            "FROM posts p " +
                            "INNER JOIN users u ON p.author_id = u.id " +
                            "WHERE p.id = ?";

            preparedStatement = connection.prepareStatement(upit);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int postId = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                Integer author_id = resultSet.getInt("author_id");
                String created_at = resultSet.getString("created_at");
                Integer destination_id = resultSet.getInt("destination_id");
                String author_name = resultSet.getString("author_name");
                String cover = resultSet.getString("cover");

                incrementViews(postId);

                post = new Post(postId, title, content, author_id, created_at, destination_id);
                post.setCover(cover);

                List<Comment> comments = commentRepository.findCommentsForPost(postId);
                post.setComments(comments);

                List<Activity> activities = activityRepository.findActivitiesForPost(postId);
                post.setActivities(activities);
                post.setAuthor_name(author_name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return post;
    }

    @Override
    public void deletePost(Integer id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM posts WHERE id = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
    }

    @Override
    public void incrementViews(Integer postId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = this.newConnection();

            String sqlQuery = "UPDATE posts SET view_count = view_count + 1 WHERE id = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, postId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
    }

    @Override
    public List<Post> filterByDestination(Integer destination_id) {
        List<Post> posts = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            String sql =
                    "SELECT p.id, p.title, p.cover, p.content, p.view_count, " +
                            "CONCAT_WS(' ', u.name, u.surname) AS author_name, " +
                            "p.created_at, p.destination_id, p.author_id " +
                            "FROM posts p " +
                            "INNER JOIN users u ON p.author_id = u.id " +
                            "WHERE p.destination_id = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, destination_id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                int author_id = resultSet.getInt("author_id");
                String created_at = resultSet.getString("created_at");
                Integer destinatioon_id = resultSet.getInt("destination_id");
                String author_name = resultSet.getString("author_name");
                String cover = resultSet.getString("cover");

                List<Comment> comments = commentRepository.findCommentsForPost(id);
                List<Activity> activities = activityRepository.findActivitiesForPost(id);

                Post newPost = new Post(id, title, content, author_id, created_at, destinatioon_id);
                newPost.setComments(comments);
                newPost.setActivities(activities);
                newPost.setAuthor_name(author_name);
                newPost.setCover(cover);
                posts.add(newPost);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return posts;
    }

    @Override
    public List<Post> theMostViewedPosts() {

        List<Post> theMostViewed = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String sqlQuery =
                    "SELECT p.id, p.title, p.cover, p.content, " +
                            "CONCAT_WS(' ', u.name, u.surname) AS author_name, " +
                            "p.created_at, p.view_count, p.destination_id, p.author_id " +
                            "FROM posts p " +
                            "INNER JOIN users u ON p.author_id = u.id " +
                            "WHERE p.created_at >= DATE_SUB(NOW(), INTERVAL 365 DAY) " +
                            "ORDER BY p.view_count DESC " +
                            "LIMIT 10";

            connection = this.newConnection();
            preparedStatement = connection.prepareStatement(sqlQuery);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                int author_id = resultSet.getInt("author_id");
                String created_at = resultSet.getString("created_at");
                Integer destination_id = resultSet.getInt("destination_id");
                String author_name = resultSet.getString("author_name");
                String cover = resultSet.getString("cover");

                List<Comment> comments = commentRepository.findCommentsForPost(id);
                List<Activity> activities = activityRepository.findActivitiesForPost(id);

                Post newPost = new Post(id, title, content, author_id, created_at, destination_id);
                newPost.setComments(comments);
                newPost.setActivities(activities);
                newPost.setAuthor_name(author_name);
                newPost.setCover(cover);
                theMostViewed.add(newPost);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return theMostViewed;
    }

    @Override
    public List<Post> filterByActivity(Integer activity_id) {
        List<Post> postsByActivity = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String sqlQuery =
                    "SELECT p.id, p.title, p.cover, p.content, " +
                            "CONCAT_WS(' ', u.name, u.surname) AS author_name, " +
                            "p.author_id, p.created_at, p.destination_id " +
                            "FROM posts p " +
                            "INNER JOIN post_activities pa ON p.id = pa.post_id " +
                            "INNER JOIN activities a ON pa.activity_id = a.id " +
                            "INNER JOIN users u ON p.author_id = u.id " +
                            "WHERE a.id = ?";

            connection = this.newConnection();
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, activity_id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                Integer author_id = resultSet.getInt("author_id");
                String created_at = resultSet.getString("created_at");
                Integer destination_id = resultSet.getInt("destination_id");
                String author_name = resultSet.getString("author_name");
                String cover = resultSet.getString("cover");

                List<Comment> comments = commentRepository.findCommentsForPost(id);
                List<Activity> activities = activityRepository.findActivitiesForPost(id);

                Post newPost = new Post(id, title, content, author_id, created_at, destination_id);
                newPost.setComments(comments);
                newPost.setActivities(activities);
                newPost.setAuthor_name(author_name);
                newPost.setCover(cover);
                postsByActivity.add(newPost);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return postsByActivity;
    }

    @Override
    public Post updatePost(Integer id, Post updatedPost) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatementPostActivity = null;

        try {
            connection = this.newConnection();
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(
                    "UPDATE posts SET title = ?, content = ?, destination_id = ?, cover = ? WHERE id = ?"
            );
            preparedStatement.setString(1, updatedPost.getTitle());
            preparedStatement.setString(2, updatedPost.getContent());
            preparedStatement.setInt(3, updatedPost.getDestination_id());
            preparedStatement.setString(4, updatedPost.getCover());
            preparedStatement.setInt(5, id);
            preparedStatement.executeUpdate();

            preparedStatement.close();

            preparedStatement = connection.prepareStatement("DELETE FROM post_activities WHERE post_id = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            List<Activity> activities = updatedPost.getActivities();
            if (activities != null && !activities.isEmpty()) {
                String sqlPostActivities = "INSERT INTO post_activities (post_id, activity_id) VALUES (?, ?)";
                preparedStatementPostActivity = connection.prepareStatement(sqlPostActivities);

                for (Activity activity : activities) {
                    int activity_id = activity.getId();
                    preparedStatementPostActivity.setInt(1, id);
                    preparedStatementPostActivity.setInt(2, activity_id);
                    preparedStatementPostActivity.executeUpdate();
                }
            }

            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatementPostActivity);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return updatedPost;
    }

    @Override
    public List<Post> favoritePosts(Integer user_id) {
        List<Post> favPosts = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String sqlQuery =
                    "SELECT p.id, p.title, p.cover, p.content, " +
                            "       CONCAT_WS(' ', u.name, u.surname) AS author_name, " +
                            "       p.author_id, p.created_at, p.destination_id " +
                            "FROM posts p " +
                            "JOIN user_favorites f ON f.post_id = p.id " +
                            "JOIN users u ON u.id = p.author_id " +
                            "WHERE f.user_id = ?";

            connection = this.newConnection();
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, user_id);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                Integer author_id = resultSet.getInt("author_id");
                String created_at = resultSet.getString("created_at");
                Integer destination_id = resultSet.getInt("destination_id");
                String author_name = resultSet.getString("author_name");
                String cover = resultSet.getString("cover");

                List<Comment> comments = commentRepository.findCommentsForPost(id);
                List<Activity> activities = activityRepository.findActivitiesForPost(id);

                Post newPost = new Post(id, title, content, author_id, created_at, destination_id);
                newPost.setComments(comments);
                newPost.setActivities(activities);
                newPost.setAuthor_name(author_name);
                newPost.setCover(cover);
                favPosts.add(newPost);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return favPosts;
    }

    @Override
    public void addFavoritePost(Integer post_id, Integer user_id) {
        String sql = "INSERT INTO user_favorites (user_id, post_id) VALUES (?, ?)";
        try (Connection connection = this.newConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, user_id);
            preparedStatement.setInt(2, post_id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeFromFavorites(Integer post_id, Integer user_id) {
        String sql = "DELETE FROM user_favorites WHERE user_id = ? AND post_id = ?";
        try (Connection connection = this.newConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, user_id);
            preparedStatement.setInt(2, post_id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
