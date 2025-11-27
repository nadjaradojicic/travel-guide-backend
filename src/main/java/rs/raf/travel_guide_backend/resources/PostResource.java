package rs.raf.travel_guide_backend.resources;

import rs.raf.travel_guide_backend.entities.Destination;
import rs.raf.travel_guide_backend.entities.Post;
import rs.raf.travel_guide_backend.entities.Role;
import rs.raf.travel_guide_backend.entities.User;
import rs.raf.travel_guide_backend.security.UserPrincipal;
import rs.raf.travel_guide_backend.respositories.user.UserRepository;
import rs.raf.travel_guide_backend.services.DestinationService;
import rs.raf.travel_guide_backend.services.PostService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PostResource {

    @Context
    private SecurityContext securityContext;

    @Inject
    private PostService postService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private DestinationService destinationService;

    private UserPrincipal currentPrincipal() {
        return (securityContext != null && securityContext.getUserPrincipal() instanceof UserPrincipal)
                ? (UserPrincipal) securityContext.getUserPrincipal()
                : null;
    }

    private boolean isAuthenticated() {
        return currentPrincipal() != null;
    }

    private boolean hasAnyRole(Role... roles) {
        UserPrincipal p = currentPrincipal();
        if (p == null || p.getRole() == null) return false;
        for (Role r : roles) {
            if (p.getRole().equals(r.toString())) return true;
        }
        return false;
    }

    private User currentUserEntity() {
        UserPrincipal p = currentPrincipal();
        return (p == null) ? null : userRepository.findUserByEmail(p.getName());
    }

    @GET
    public Response getAllPosts() {
        return Response.ok(postService.getAllPosts()).build();
    }

    @POST
    @Path("/{id}")
    public Response addPost(@PathParam("id") Integer destinationId,
                            @Valid Post post) {
        try {
            if (!isAuthenticated()) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
            }
            if (!hasAnyRole(Role.admin, Role.creator)) {
                return Response.status(Response.Status.FORBIDDEN).entity("Insufficient role").build();
            }

            User me = currentUserEntity();
            if (me == null || !Boolean.TRUE.equals(me.getStatus())) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Unknown or inactive user").build();
            }

            Destination dest = destinationService.findDestination(destinationId);
            if (dest == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Destination not found").build();
            }

            post.setAuthor_id(currentPrincipal().getId());

            Post created = postService.addPost(destinationId, post);
            return Response.status(Response.Status.CREATED).entity(created).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Failed to create post").build();
        }
    }

    @GET
    @Path("/{id}")
    public Post findPost(@PathParam("id") Integer id) {
        return postService.findPost(id);
    }

    @GET
    @Path("/destFilter/{id}")
    public Response filterByDestination(@PathParam("id") Integer destination_id) {
        return Response.ok(postService.filterByDestination(destination_id)).build();
    }

    @GET
    @Path("/aktFilter/{id}")
    public Response filterByActivity(@PathParam("id") Integer activity_id) {
        return Response.ok(postService.filterByActivity(activity_id)).build();
    }

    @GET
    @Path("/favorites")
    public Response favoritePosts() {
        try {
            if (!isAuthenticated()) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            UserPrincipal me = currentPrincipal();
            return Response.ok(postService.favoritePosts(me.getId())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Failed to fetch favorites").build();
        }
    }

    @POST
    @Path("/favorites/{id}")
    public Response addPostToFavorites(@PathParam("id") Integer postId) {
        try {
            if (!isAuthenticated()) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            UserPrincipal me = currentPrincipal();
            postService.addFavoritePost(postId, me.getId());
            return Response.status(Response.Status.CREATED).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Post not found").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Failed to add favorite").build();
        }
    }

    @DELETE
    @Path("/favorites/{id}")
    public Response removePostFromFavorites(@PathParam("id") Integer postId) {
        try {
            if (!isAuthenticated()) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            UserPrincipal me = currentPrincipal();
            postService.removeFromFavorites(postId, me.getId());
            return Response.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Failed to remove favorite").build();
        }
    }

    @GET
    @Path("/theMostViewed")
    public Response theMostViewedPosts() {
        return Response.ok(postService.theMostViewedPosts()).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePost(@PathParam("id") Integer id) {
        try {
            if (!isAuthenticated()) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            if (!hasAnyRole(Role.admin, Role.creator)) {
                return Response.status(Response.Status.FORBIDDEN).entity("Insufficient role").build();
            }
            postService.deletePost(id);
            return Response.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Failed to delete post").build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updatePost(@PathParam("id") Integer id,
                               @Valid Post post) {
        try {
            if (!isAuthenticated()) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            if (!hasAnyRole(Role.admin, Role.creator)) {
                return Response.status(Response.Status.FORBIDDEN).entity("Insufficient role").build();
            }

            if (post == null
                    || post.getTitle() == null || post.getTitle().trim().isEmpty()
                    || post.getContent() == null || post.getContent().trim().isEmpty()
                    || post.getDestination_id() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Missing required fields").build();
            }

            Post updated = postService.updatePost(id, post);
            return Response.ok(updated).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Failed to update post").build();
        }
    }
}
