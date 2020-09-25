package teammates.ui.request;

import teammates.common.exception.InvalidHttpRequestBodyException;

/**
 * The request body of a HTTP request.
 */
public abstract class BasicRequest {

    /**
     * Validate the request.
     */
    public abstract void validate();

    /**
     * Asserts a condition or throws {@link InvalidHttpRequestBodyException}.
     */
    void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new InvalidHttpRequestBodyException(message);
        }
    }
}
