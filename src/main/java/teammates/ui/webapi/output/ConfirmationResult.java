package teammates.ui.webapi.output;

/**
 * The result of the confirmation.
 */
public enum ConfirmationResult {

    /**
     * The submission has been confirmed successfully.
     */
    SUCCESS,

    /**
     * The submission has been confirmed but the confirmation email failed to send.
     */
    SUCCESS_BUT_EMAIL_FAIL_TO_SEND
}
