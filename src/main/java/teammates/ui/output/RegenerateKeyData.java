package teammates.ui.output;

/**
 * The API output format for the regenerate key request.
 */
public class RegenerateKeyData extends ApiOutput {
    private final String message;
    private final String newRegistrationKey;

    public RegenerateKeyData(String msg, String key) {
        message = msg;
        newRegistrationKey = key;
    }

    public String getMessage() {
        return message;
    }

    public String getNewRegistrationKey() {
        return newRegistrationKey;
    }

}
