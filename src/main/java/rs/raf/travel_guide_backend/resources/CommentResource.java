package rs.raf.travel_guide_backend.resources;

import rs.raf.travel_guide_backend.entities.Comment;
import rs.raf.travel_guide_backend.entities.Post;
import rs.raf.travel_guide_backend.entities.User;
import rs.raf.travel_guide_backend.respositories.user.UserRepository;
import rs.raf.travel_guide_backend.security.UserPrincipal;
import rs.raf.travel_guide_backend.services.CommentService;
import rs.raf.travel_guide_backend.services.PostService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentResource {

    @Inject
    private CommentService commentService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PostService postService;

    @Context
    private SecurityContext securityContext;

    private UserPrincipal currentPrincipal() {
        return (securityContext != null && securityContext.getUserPrincipal() instanceof UserPrincipal)
                ? (UserPrincipal) securityContext.getUserPrincipal()
                : null;
    }

    private boolean isAuthenticated() {
        return currentPrincipal() != null;
    }

    private User currentUserEntity() {
        UserPrincipal p = currentPrincipal();
        return (p == null) ? null : userRepository.findUserByEmail(p.getName());
    }

    @POST
    @Path("/{id}")
    public Response addComment(@PathParam("id") Integer post_id,
                               @Valid Comment comment) {
        try {
            if (!isAuthenticated()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Unauthorized").build();
            }

            User me = currentUserEntity();
            if (me == null || !Boolean.TRUE.equals(me.getStatus())) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Unknown or inactive user").build();
            }

            Post post = postService.findPost(post_id);
            if (post == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Post not found").build();
            }

            comment.setAuthor_id(currentPrincipal().getId());

            Comment created = commentService.addComment(post_id, comment);
            return Response.status(Response.Status.CREATED).entity(created).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to create comment").build();
        }
    }

    @DELETE
    @Path("/post/{postId}/comment/{commentId}")
    public Response deleteComment(@PathParam("postId") Integer postId,
                                  @PathParam("commentId") Integer commentId) {
        commentService.deleteComment(postId, commentId);
        return Response.noContent().build();
    }
}
