package teammates.ui.loginmethodhandlers;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.ui.output.LoginMethod;

/**
 * Represents the state object to be persisted during the callback.
 *
 * @param nextUrl the URL to redirect to after successful authentication
 * @param sessionId the session ID to validate against during callback
 * @param loginMethod the login method used for authentication
 */
public record AuthState(String nextUrl, String sessionId, LoginMethod loginMethod) {

    @JsonCreator
    public AuthState {
        // For Jackson deserialization
    }

}
