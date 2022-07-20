package teammates.common.exception;

/**
 * Exception thrown when error is encountered while using Firebase services.
 */
public class FirebaseException extends Exception {

    public FirebaseException(Throwable cause) {
        super(cause);
    }

}
