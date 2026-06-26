package teammates.ui.request;

import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The request body for checking access to or performing a course join link.
 */
public class CourseJoinKeyRequest extends BasicRequest {
    private String key;

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(key != null, "Key cannot be null");
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
