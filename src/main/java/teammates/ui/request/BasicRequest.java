package teammates.ui.request;

import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The request body of a HTTP request.
 */
public abstract class BasicRequest {

    /**
     * Validate the request.
     */
    public abstract void validate() throws InvalidHttpRequestBodyException;

    /**
     * Validates a condition is true or throws {@link InvalidHttpRequestBodyException}.
     */
    void validateTrue(boolean condition, String message) throws InvalidHttpRequestBodyException {
        if (!condition) {
            throw new InvalidHttpRequestBodyException(message);
        }
    }

    /**
     * Validates a condition is false or throws {@link InvalidHttpRequestBodyException}.
     */
    void validateFalse(boolean condition, String message) throws InvalidHttpRequestBodyException {
        if (condition) {
            throw new InvalidHttpRequestBodyException(message);
        }
    }
}
