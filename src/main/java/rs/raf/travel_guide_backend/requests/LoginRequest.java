package rs.raf.travel_guide_backend.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LoginRequest {

    @NotEmpty(message = "Email is required")
    @NotNull(message = "Email is required")
    String email;

    @NotEmpty(message = "Password is required")
    @NotNull(message = "Password is required")
    String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public LoginRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
