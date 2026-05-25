package teammates.ui.request;

/**
 * The request that contain RegKey.
 */
public class RegKeyRequest extends BasicRequest {
    private String key;

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(key != null, "RegKey can not be null");
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
