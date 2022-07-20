package teammates.ui.output;

/**
 * The output format for send login email request.
 */
public class SendLoginEmailResponseData extends ApiOutput {
    private final boolean isEmailSent;
    private final String message;

    public SendLoginEmailResponseData(boolean isEmailSent, String message) {
        this.isEmailSent = isEmailSent;
        this.message = message;
    }

    public boolean isEmailSent() {
        return this.isEmailSent;
    }

    public String getMessage() {
        return message;
    }
}
