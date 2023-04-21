package teammates.common.exception;

/**
 * Exception thrown when error is encountered while executing authentication-related services.
 */
public class AuthException extends Exception {

    public AuthException(Throwable cause) {
        super(cause);
    }

}
