package rs.raf.travel_guide_backend.filter;
import rs.raf.travel_guide_backend.entities.User;
import rs.raf.travel_guide_backend.resources.*;
import rs.raf.travel_guide_backend.security.UserPrincipal;
import rs.raf.travel_guide_backend.services.*;
import rs.raf.travel_guide_backend.respositories.user.UserRepository;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    @Inject
    private UserService userService;
    @Inject
    private PostService postService;
    @Inject
    private DestinationService destinationService;
    @Inject
    private ActivityService activityService;
    @Inject
    private CommentService commentService;
    @Inject
    private UserRepository userRepository;

    private static final Algorithm ALG = Algorithm.HMAC256("secret");
    private static final JWTVerifier VERIFIER = JWT.require(ALG).build();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) return;
        if (!this.isAuthRequired(requestContext)) return;

        try {
            String header = requestContext.getHeaderString("Authorization");
            String token = stripBearer(header);

            if (token == null || token.isEmpty()) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                return;
            }

            DecodedJWT jwt = VERIFIER.verify(token);
            String email = jwt.getSubject();
            if (email == null || email.isEmpty()) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                return;
            }

            User user = userRepository.findUserByEmail(email);
            if (user == null || !Boolean.TRUE.equals(user.getStatus())) { // status=false => suspendovan
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                return;
            }

            setSecurityContext(requestContext, user);

            final String path = requestContext.getUriInfo().getPath();
            List<Object> matchedResources = requestContext.getUriInfo().getMatchedResources();

            for (Object matchedResource : matchedResources) {
                if (matchedResource instanceof UserResource) {
                    if (!this.userService.isAuthorized(user)) {
                        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                        return;
                    }
                } else if (matchedResource instanceof PostResource) {
                    if (path.startsWith("posts/favorites")) {
                        if (!userService.isAuthenticated(user)) {
                            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                            return;
                        }
                        continue;
                    }
                    if (!this.postService.isAuthorized(user)) {
                        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                        return;
                    }
                } else if (matchedResource instanceof DestinationResource) {
                    if (!this.destinationService.isAuthorized(user)) {
                        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                        return;
                    }
                } else if (matchedResource instanceof ActivityResource) {
                    if (!this.activityService.isAuthorized(user)) {
                        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                        return;
                    }
                } else if (matchedResource instanceof CommentResource) {
                    if (!this.commentService.isAuthorized(user)) {
                        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                        return;
                    }
                }
            }
        } catch (Exception exception) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private static String stripBearer(String header) {
        if (header == null) return null;
        return header.startsWith("Bearer ") ? header.substring(7) : header;
    }

    private void setSecurityContext(ContainerRequestContext requestContext, User u) {
        final SecurityContext current = requestContext.getSecurityContext();
        final UserPrincipal principal = new UserPrincipal(u.getId(), u.getEmail(), u.getRole().toString());

        requestContext.setSecurityContext(new SecurityContext() {
            @Override public Principal getUserPrincipal() { return principal; }
            @Override public boolean isUserInRole(String role) {
                return principal.getRole() != null
                        && principal.getRole().equalsIgnoreCase(role);
            }
            @Override public boolean isSecure() { return current != null && current.isSecure(); }
            @Override public String getAuthenticationScheme() { return "Bearer"; }
        });
    }

    private boolean isAuthRequired(ContainerRequestContext req) {
        final String p = req.getUriInfo().getPath();
        if (p.contains("login") || p.contains("register")) return false;

        List<Object> matchedResources = req.getUriInfo().getMatchedResources();
        for (Object r : matchedResources) {
            if (r instanceof UserResource) return true;
            if (r instanceof PostResource) {
                if (p.startsWith("posts/favorites")) return true;
                if (isWrite(req)) return true;
            }
            if (r instanceof DestinationResource && isWrite(req)) return true;
            if (r instanceof ActivityResource && isWrite(req)) return true;
            if (r instanceof CommentResource && isWrite(req)) return true;
        }
        return false;
    }

    private boolean isWrite(ContainerRequestContext req) {
        String m = req.getMethod();
        return "PUT".equals(m) || "POST".equals(m) || "DELETE".equals(m);
    }
}
