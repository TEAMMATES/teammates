package teammates.common.exception;

@SuppressWarnings("serial")
public class JoinCourseException extends TeammatesException {
    public JoinCourseException() {
        super();
    }

    public JoinCourseException(String errorcode, String message) {
        super(errorcode, message);
    }

    public JoinCourseException(String message) {
        super(message);
    }
}
