package rs.raf.travel_guide_backend.resources;

import rs.raf.travel_guide_backend.entities.User;
import rs.raf.travel_guide_backend.requests.LoginRequest;
import rs.raf.travel_guide_backend.requests.LoginResult;
import rs.raf.travel_guide_backend.services.UserService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private UserService userService;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(@Valid LoginRequest loginRequest) {
        LoginResult result = this.userService.login(loginRequest.getEmail(), loginRequest.getPassword());

        Map<String, String> body = new HashMap<>();

        switch (result.getStatus()) {
            case OK:
                body.put("jwt", result.getJwt());
                return Response.ok(body).build();

            case SUSPENDED:
                body.put("message", "Your account is suspended.");
                return Response.status(Response.Status.FORBIDDEN).entity(body).build();

            case INVALID_CREDENTIALS:
            default:
                body.put("message", "Invalid email or password.");
                return Response.status(Response.Status.UNAUTHORIZED).entity(body).build();
        }
    }

    @GET
    public Response getAllUsers() {
        return Response.ok(this.userService.getAllUsers()).build();
    }

    @GET
    @Path("/{id}")
    public User findUser(@PathParam("id") Integer id) {
        return this.userService.findUser(id);
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(@Valid User user) {
        return this.userService.addUser(user);
    }

    @PUT
    @Path("/changeStatus/{id}")
    public Response changeStatus(@PathParam("id") Integer user_id) {
        userService.changeStatus(user_id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public User updateUser(@PathParam("id") Integer id, User user) {
        return userService.updateUser(id, user);
    }
}
