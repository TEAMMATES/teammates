package teammates.common.exception;

/**
 * Exception thrown when students/instructors join a course.
 */
@SuppressWarnings("serial")
public class JoinCourseException extends Exception {

    public JoinCourseException(String message) {
        super(message);
    }

}
