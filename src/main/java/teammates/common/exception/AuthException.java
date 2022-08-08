package teammates.common.exception;

/**
 * Exception thrown when error is encountered while executing authentication-related services.
 */
public class AuthException extends Exception {

    private final String errorCode;

    public AuthException(Throwable cause) {
        this(cause, "");
    }

    public AuthException(Throwable cause, String errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

}
