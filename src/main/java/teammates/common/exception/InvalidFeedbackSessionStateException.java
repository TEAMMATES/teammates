package teammates.common.exception;

/**
 * Exception thrown when an operation is attempted on a feedback session that is not in the expected state.
 */
public class InvalidFeedbackSessionStateException extends Exception {

    public InvalidFeedbackSessionStateException(String message) {
        super(message);
    }

}
