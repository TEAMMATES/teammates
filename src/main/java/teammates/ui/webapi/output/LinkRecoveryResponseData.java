package teammates.ui.webapi.output;

/**
 * The output format for link recovery request.
 */
public class LinkRecoveryResponseData extends ApiOutput {
    private final boolean isEmailSent;
    private final String message;

    public LinkRecoveryResponseData(boolean isEmailSent, String message) {
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
