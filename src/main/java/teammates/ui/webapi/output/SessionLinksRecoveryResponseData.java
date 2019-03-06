package teammates.ui.webapi.output;

/**
 * The output format for link recovery request.
 */
public class SessionLinksRecoveryResponseData extends ApiOutput {
    private final boolean isEmailSent;
    private final String message;

    public SessionLinksRecoveryResponseData(boolean isEmailSent, String message) {
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
