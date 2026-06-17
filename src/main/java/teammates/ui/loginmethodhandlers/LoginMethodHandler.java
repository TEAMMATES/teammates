package teammates.ui.loginmethodhandlers;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interface for handling different login methods.
 */
public interface LoginMethodHandler {
    /**
     * Handles the login process for the specific login method.
     */
    void handleLogin(HttpServletRequest req, HttpServletResponse resp, String nextUrl) throws IOException;

    /**
     * Handles the callback from the login method by retrieving the authentication result.
     */
    AuthResult handleCallback(HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
