package teammates.common.exception;

/**
 * Exception thrown when regenerating the registration key of a course's student.
 */
@SuppressWarnings("serial")
public class RegenerateStudentException extends Exception {

    public RegenerateStudentException(String message) {
        super(message);
    }

}
