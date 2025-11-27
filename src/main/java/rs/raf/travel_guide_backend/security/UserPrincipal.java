package rs.raf.travel_guide_backend.security;

public class UserPrincipal implements java.security.Principal{

    private final int id;
    private final String email;
    private final String role;

    public UserPrincipal(int id, String email, String role) {
        this.id = id; this.email = email; this.role = role;
    }


    @Override
    public String getName() {
        return email;
    }

    public int getId() {
        return id;
    }

    public String getRole() {
        return role;
    }
}
