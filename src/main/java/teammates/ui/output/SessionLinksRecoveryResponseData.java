package teammates.ui.output;

/**
 * The output format for session links recovery request.
 */
public class SessionLinksRecoveryResponseData extends ApiOutput {
    private final String message;

    public SessionLinksRecoveryResponseData(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
