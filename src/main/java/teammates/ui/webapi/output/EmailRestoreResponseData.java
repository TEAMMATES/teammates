package teammates.ui.webapi.output;

/**
 * The output format for link recovery request.
 */
public class EmailRestoreResponseData extends ApiOutput {
    private final boolean status;
    private final String message;

    public EmailRestoreResponseData(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public boolean getStatus() {
        return this.status;
    }

    public String getMessage() {
        return message;
    }
}
