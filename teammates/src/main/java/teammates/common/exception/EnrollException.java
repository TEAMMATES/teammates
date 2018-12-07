package teammates.common.exception;

@SuppressWarnings("serial")
public class EnrollException extends TeammatesException {
    public EnrollException(String message) {
        super(message);
    }

    public EnrollException(String specificErrorcode, String message) {
        super(specificErrorcode, message);
    }
}
