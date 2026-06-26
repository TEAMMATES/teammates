package teammates.ui.loginmethodhandlers;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

import teammates.ui.exception.AuthException;

/**
 * Login method handler for Microsoft Entra login.
 */
public class MicrosoftLoginHandler implements LoginMethodHandler {
    @Override
    public String handleLogin(HttpServletRequest req, String nextUrl) throws IOException, AuthException {
        // Implement Microsoft Entra login logic here
        throw new UnsupportedOperationException("Microsoft Entra login is not yet implemented.");
    }

    @Override
    public AuthResult handleCallback(HttpServletRequest req, AuthState state) throws IOException, AuthException {
        // Implement Microsoft Entra login callback logic here
        throw new UnsupportedOperationException("Microsoft Entra login callback is not yet implemented.");
    }

}
