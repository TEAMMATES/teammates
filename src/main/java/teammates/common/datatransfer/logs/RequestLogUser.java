package teammates.common.datatransfer.logs;

/**
 * Represents a user who invokes the HTTP request.
 */
public class RequestLogUser {

    private String email;
    private String googleId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

}
