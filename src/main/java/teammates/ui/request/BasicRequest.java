package teammates.ui.request;

/**
 * The request body of a HTTP request.
 */
public abstract class BasicRequest {

    /**
     * Validate the request.
     */
    public abstract void validate() throws InvalidHttpRequestBodyException;

    /**
     * Asserts a condition is true or throws {@link InvalidHttpRequestBodyException}.
     */
    void assertTrue(boolean condition, String message) throws InvalidHttpRequestBodyException {
        if (!condition) {
            throw new InvalidHttpRequestBodyException(message);
        }
    }

    /**
     * Asserts a condition is false or throws {@link InvalidHttpRequestBodyException}.
     */
    void assertFalse(boolean condition, String message) throws InvalidHttpRequestBodyException {
        if (condition) {
            throw new InvalidHttpRequestBodyException(message);
        }
    }
}
