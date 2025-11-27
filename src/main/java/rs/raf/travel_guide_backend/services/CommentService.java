package rs.raf.travel_guide_backend.services;

import rs.raf.travel_guide_backend.entities.Comment;
import rs.raf.travel_guide_backend.entities.User;
import rs.raf.travel_guide_backend.respositories.comment.CommentRepository;

import javax.inject.Inject;

public class CommentService extends AbstractIsAuthorized {

    @Inject
    private CommentRepository commentRepository;

    public Comment addComment(Integer post_id, Comment comment) {
        return commentRepository.addComment(post_id, comment);
    }

    public void deleteComment(Integer post_id, Integer comment_id) {
        commentRepository.deleteComment(post_id, comment_id);
    }

    @Override
    public boolean isAuthorized(User user) {
        return user != null;
    }
}
