package teammates.common.exception;

/**
 * Exception thrown when error is encountered while performing search.
 */
public class SearchServiceException extends Exception {

    private final int statusCode;

    public SearchServiceException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public SearchServiceException(Throwable cause, int statusCode) {
        super(cause);
        this.statusCode = statusCode;
    }

    public SearchServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
