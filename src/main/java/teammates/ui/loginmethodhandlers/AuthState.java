package teammates.ui.loginmethodhandlers;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.ui.output.LoginMethod;

/**
 * Represents the state object to be persisted during the callback.
 */
public class AuthState {
    private final String nextUrl;
    private final String sessionId;
    private final LoginMethod method;

    @JsonCreator
    public AuthState(String nextUrl, String sessionId, LoginMethod method) {
        this.nextUrl = nextUrl;
        this.sessionId = sessionId;
        this.method = method;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public String getSessionId() {
        return sessionId;
    }

    public LoginMethod getMethod() {
        return method;
    }
}
