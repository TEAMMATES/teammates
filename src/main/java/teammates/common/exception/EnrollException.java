package teammates.common.exception;

/**
 * Exception thrown when enrolling students into a course.
 */
@SuppressWarnings("serial")
public class EnrollException extends Exception {

    public EnrollException(String message) {
        super(message);
    }

}
