package teammates.common.exception;

@SuppressWarnings("serial")
public class NullPostParameterException extends InvalidPostParametersException {
    public NullPostParameterException(String message) {
        super(message);
    }
}
