package teammates.common.exception;

/**
 * Exception thrown when an operation is attempted on an account verification request that is not in the expected state.
 */
public class InvalidVerificationRequestStateException extends Exception {

    public InvalidVerificationRequestStateException(String message) {
        super(message);
    }

}
