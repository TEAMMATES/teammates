package teammates.common.exception;

@SuppressWarnings("serial")
public class JoinCourseException extends TeammatesException {
    public JoinCourseException() {
        super();
    }

    public JoinCourseException(final String errorcode, final String message) {
        super(errorcode, message);
    }

    public JoinCourseException(final String message) {
        super(message);
    }
}
