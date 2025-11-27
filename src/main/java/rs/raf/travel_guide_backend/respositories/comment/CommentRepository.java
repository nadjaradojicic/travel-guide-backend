package rs.raf.travel_guide_backend.respositories.comment;

import rs.raf.travel_guide_backend.entities.Comment;

import java.util.List;

public interface CommentRepository {

     List<Comment> findCommentsForPost(Integer post_id);
     Comment addComment(Integer post_id, Comment comment);

     void deleteComment(Integer post_id, Integer comment_id);
}
