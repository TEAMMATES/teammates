package teammates.ui.request;

/**
 * Exception thrown when an HTTP request body does not conform to an expected format
 * (e.g. It is not deserializable or some fields are missing).
 */
public class InvalidHttpRequestBodyException extends RuntimeException {

    public InvalidHttpRequestBodyException(String message) {
        super(message);
    }

    public InvalidHttpRequestBodyException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public InvalidHttpRequestBodyException(String message, Throwable cause) {
        super(message, cause);
    }

}
