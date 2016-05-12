package teammates.common.exception;

@SuppressWarnings("serial")
public class EnrollException extends TeammatesException {
    public EnrollException(final String message) {
        super(message);
    }

    public EnrollException(final String specificErrorcode, final String message) {
        super(specificErrorcode, message);
    }
}
