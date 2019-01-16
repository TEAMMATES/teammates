package teammates.common.exception;

/**
 * Exception thrown when an HTTP request body does not conform to an expected format
 * (e.g. It is not deserializable or some fields are missing).
 */
@SuppressWarnings("serial")
public class InvalidHttpRequestBodyException extends RuntimeException {

    public InvalidHttpRequestBodyException(String message) {
        super(message);
    }

    public InvalidHttpRequestBodyException(String message, Throwable cause) {
        super(message, cause);
    }

}
