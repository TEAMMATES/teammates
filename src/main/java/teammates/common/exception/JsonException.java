package teammates.common.exception;

/**
 * Exception thrown when a JSON serialization or deserialization operation fails.
 *
 * <p>This is intended to be a thin wrapper for exceptions thrown by Jackson.
 */
public class JsonException extends RuntimeException {

    public JsonException(Throwable cause) {
        super(cause);
    }

}
