package teammates.ui.exception;

/**
 * Exception thrown when an error occurs during the authentication process or when it is in an invalid state.
 * e.g. the request does not contain the expected parameters, or fails to pass the state validation checks.
 */
public class AuthException extends Exception {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

}
