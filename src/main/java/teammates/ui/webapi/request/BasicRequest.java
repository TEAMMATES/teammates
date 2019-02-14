package teammates.ui.webapi.request;

import teammates.common.exception.InvalidHttpRequestBodyException;

/**
 * A BasicRequest represents the request body of a HTTP request. Within the HTTP request, it is expected that the
 * body of the request would contain certain specific parameters in order to fulfill the requirements of the API.
 * A BasicRequest specifies those requirements. They are then extracted out to external interfaces at
 * web/types/api-output.ts from generateTypes. These BasicRequest are also used in the context of the API, wherein
 * they are parsed in Action's getAndValidateRequestBody method to return the request object itself to the API.
 */
public abstract class BasicRequest {

    /**
     * Validate the request.
     */
    public abstract void validate();

    /**
     * Asserts a condition or throws {@link InvalidHttpRequestBodyException}.
     */
    public void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new InvalidHttpRequestBodyException(message);
        }
    }
}
