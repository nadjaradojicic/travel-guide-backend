package rs.raf.travel_guide_backend.services;

import rs.raf.travel_guide_backend.entities.Role;
import rs.raf.travel_guide_backend.entities.User;

public abstract class AbstractIsAuthorized {

    public boolean isAuthorized(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }

        Role role = user.getRole();

        return role == Role.admin || role == Role.creator;
    }
}
