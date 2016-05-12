package teammates.common.exception;

@SuppressWarnings("serial")
public class NullPostParameterException extends RuntimeException {
    public NullPostParameterException(final String message) {
        super(message);
    }
}
