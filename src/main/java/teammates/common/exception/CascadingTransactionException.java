package teammates.common.exception;

/**
 * Exception thrown when committing a batch of students into the datastore.
 */
@SuppressWarnings("serial")
public class CascadingTransactionException extends Exception {

    public CascadingTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

}
