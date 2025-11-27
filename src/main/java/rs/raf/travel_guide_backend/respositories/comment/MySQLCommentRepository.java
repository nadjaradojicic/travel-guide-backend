package rs.raf.travel_guide_backend.respositories.comment;

import rs.raf.travel_guide_backend.entities.Comment;
import rs.raf.travel_guide_backend.respositories.MySQLAbstractRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MySQLCommentRepository extends MySQLAbstractRepository implements CommentRepository {

    @Override
    public List<Comment> findCommentsForPost(Integer post_id) {
        List<Comment> comments = new ArrayList<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            String sql =
                    "SELECT c.id, c.author_id, c.content, c.created_at, " +
                            "       CONCAT_WS(' ', u.name, u.surname) AS author_name " +
                            "FROM comments c " +
                            "LEFT JOIN users u ON u.id = c.author_id " +
                            "WHERE c.post_id = ? " +
                            "ORDER BY c.created_at DESC";

            ps = connection.prepareStatement(sql);
            ps.setInt(1, post_id);
            rs = ps.executeQuery();

            while (rs.next()) {
                int commentId = rs.getInt("id");
                Integer authorId = rs.getInt("author_id");
                String content = rs.getString("content");
                String createdAt = rs.getString("created_at");
                String authorName = rs.getString("author_name");

                Comment c = new Comment(commentId, authorId, content, createdAt);

                c.setAuthor_name(authorName != null && !authorName.isEmpty()
                        ? authorName
                        : ("User #" + authorId));

                comments.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(ps);
            this.closeResultSet(rs);
            this.closeConnection(connection);
        }

        return comments;
    }

    @Override
    public Comment addComment(Integer id, Comment comment) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            String[] generatedColumns = {"id"};

            LocalDateTime created_at = LocalDateTime.now();
            String time = created_at.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String sqlQuery =
                    "INSERT INTO comments (author_id, content, created_at, post_id) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sqlQuery, generatedColumns);
            preparedStatement.setInt(1, comment.getAuthor_id());
            preparedStatement.setString(2, comment.getContent());
            preparedStatement.setString(3, time);
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                comment.setId(resultSet.getInt(1));
            }
            comment.setCreated_at(time);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return comment;
    }

    @Override
    public void deleteComment(Integer postId, Integer commentId) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = this.newConnection();
            String sqlQuery = "DELETE FROM comments WHERE id = ? AND post_id = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, commentId);
            preparedStatement.setInt(2, postId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
    }
}
