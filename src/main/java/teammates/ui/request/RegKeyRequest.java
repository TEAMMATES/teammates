package teammates.ui.request;

/**
 * The request that contains a registration key.
 */
public class RegKeyRequest extends BasicRequest {
    private String key;

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(key != null, "Registration key cannot be null");
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
