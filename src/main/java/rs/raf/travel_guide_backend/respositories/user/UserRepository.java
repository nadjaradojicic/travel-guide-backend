package rs.raf.travel_guide_backend.respositories.user;

import rs.raf.travel_guide_backend.entities.User;

import javax.ws.rs.core.Response;
import java.util.List;

public interface UserRepository {

     User findUserByEmail(String email);

     User findUserById(Integer user_id);

     List<User> getAllUsers();

     Response addUser(User user);

     void changeStatus(Integer user_id);

     User updateUser(Integer id, User updatedUser);
}
