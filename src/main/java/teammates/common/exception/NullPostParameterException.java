package teammates.common.exception;

@SuppressWarnings("serial")
public class NullPostParameterException extends RuntimeException {
    public NullPostParameterException(String message) {
        super(message);
    }
}
