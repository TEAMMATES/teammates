package teammates.common.exception;

/**
 * Exception thrown when a crucial HTTP request does not succeed.
 */
@SuppressWarnings("serial")
public class HttpRequestFailedException extends Exception {

    public HttpRequestFailedException(String message) {
        super(message);
    }

}
