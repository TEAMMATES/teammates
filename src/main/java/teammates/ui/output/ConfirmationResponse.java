package teammates.ui.output;

/**
 * The output format of {@link teammates.ui.webapi.ConfirmFeedbackSessionSubmissionAction}.
 */
public class ConfirmationResponse extends ApiOutput {
    private final ConfirmationResult result;
    private final String message;

    public ConfirmationResponse(ConfirmationResult result, String message) {
        this.result = result;
        this.message = message;
    }

    public ConfirmationResult getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
