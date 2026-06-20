package teammates.ui.loginmethodhandlers;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

import teammates.ui.exception.InvalidAuthStateException;

/**
 * Interface for handling different login methods.
 */
public interface LoginMethodHandler {
    /**
     * Handles the login process for the specific login method.
     *
     * @return the external URL to redirect the user to for login.
     * @throws IOException if an I/O error occurs during the login process.
     * @throws InvalidAuthStateException if the authentication state is invalid.
     */
    String handleLogin(HttpServletRequest req, String nextUrl) throws IOException, InvalidAuthStateException;

    /**
     * Handles the callback from the login method by retrieving the authentication result.
     *
     * @return the authentication result.
     * @throws IOException if an I/O error occurs during the callback process.
     * @throws InvalidAuthStateException if the authentication state is invalid.
     */
    AuthResult handleCallback(HttpServletRequest req, AuthState state) throws IOException, InvalidAuthStateException;
}
