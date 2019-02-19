package teammates.common.exception;

/**
 * Exception thrown when an operation may result in overloading the performance limit of the system.
 */
@SuppressWarnings("serial")
public class ExceedingRangeException extends Exception {

    public ExceedingRangeException(String message) {
        super(message);
    }

}
