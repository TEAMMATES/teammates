package teammates.ui.request;

import teammates.common.exception.InvalidParametersException;

/**
 * Exception thrown when an HTTP request body does not conform to an expected format
 * (e.g. It is not deserializable or some fields are missing).
 *
 * <p>This corresponds to HTTP 400 error.
 */
public class InvalidHttpRequestBodyException extends Exception {

    public InvalidHttpRequestBodyException(String message) {
        super(message);
    }

    public InvalidHttpRequestBodyException(InvalidParametersException cause) {
        super(cause.getMessage(), cause);
    }

}
