package teammates.ui.exception;

/**
 * Exception thrown when the authentication state is invalid or cannot be processed.
 */
public class InvalidAuthStateException extends Exception {

    public InvalidAuthStateException(String message) {
        super(message);
    }

    public InvalidAuthStateException(String message, Throwable cause) {
        super(message, cause);
    }

}
