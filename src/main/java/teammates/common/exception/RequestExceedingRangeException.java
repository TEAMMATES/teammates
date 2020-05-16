package teammates.common.exception;

/**
 * Runtime exception wrapper for {@link ExceedingRangeException}.
 */
@SuppressWarnings("serial")
public class RequestExceedingRangeException extends RuntimeException {

    public RequestExceedingRangeException(ExceedingRangeException e) {
        super(e.getMessage());
    }

}
