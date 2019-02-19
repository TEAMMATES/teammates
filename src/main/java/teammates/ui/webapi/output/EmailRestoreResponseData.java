package teammates.ui.webapi.output;

import teammates.ui.webapi.action.LinkRecoveryAction;

/**
 * The output format of {@link LinkRecoveryAction}.
 */
public class EmailRestoreResponseData extends ApiOutput {
    private final boolean status;
    private final String message;

    public EmailRestoreResponseData(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public boolean status() {
        return this.status;
    }

    public String getMessage() {
        return message;
    }
}
