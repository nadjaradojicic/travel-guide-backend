package rs.raf.travel_guide_backend.requests;

public class LoginResult {
    public enum Status { OK, INVALID_CREDENTIALS, SUSPENDED }

    private final Status status;
    private final String jwt;

    public LoginResult(Status status, String jwt) {
        this.status = status;
        this.jwt = jwt;
    }
    public Status getStatus() { return status; }
    public String getJwt() { return jwt; }
}
