package rs.raf.travel_guide_backend.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.codec.digest.DigestUtils;
import rs.raf.travel_guide_backend.entities.Role;
import rs.raf.travel_guide_backend.entities.User;
import rs.raf.travel_guide_backend.requests.LoginResult;
import rs.raf.travel_guide_backend.respositories.user.UserRepository;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

public class UserService extends AbstractIsAuthorized{

    @Inject
    UserRepository userRepository;

    public LoginResult login(String email, String password) {
        String hashedPassword = DigestUtils.sha256Hex(password);

        User user = this.userRepository.findUserByEmail(email);
        if (user == null) {
            return new LoginResult(LoginResult.Status.INVALID_CREDENTIALS, null);
        }

        if (!user.getHashedPassword().equals(hashedPassword)) {
            return new LoginResult(LoginResult.Status.INVALID_CREDENTIALS, null);
        }

        if (!Boolean.TRUE.equals(user.getStatus())) {
            return new LoginResult(LoginResult.Status.SUSPENDED, null);
        }

        Date issuedAt = new Date();
        Algorithm algorithm = Algorithm.HMAC256("secret");

        String jwt = JWT.create()
                .withIssuedAt(issuedAt)
                .withSubject(email)
                .withClaim("role", user.getRole().toString())
                .sign(algorithm);

        return new LoginResult(LoginResult.Status.OK, jwt);
    }

    public boolean isAuthorized(User user){
        return user != null && user.getRole() == Role.admin;
    }

    public boolean isAuthenticated(User user) {
        return user != null;
    }

    public List<User> getAllUsers(){
        return userRepository.getAllUsers();
    }

    public User findUser(Integer id){
        return userRepository.findUserById(id);
    }

    public Response addUser(User user){
        String hashedPassword = DigestUtils.sha256Hex(user.getHashedPassword());
        user.setHashedPassword(hashedPassword);
        return userRepository.addUser(user);
    }

    public void changeStatus(Integer user_id){
        userRepository.changeStatus(user_id);
    }

    public User updateUser(Integer id, User user){
        return userRepository.updateUser(id, user);
    }
}
