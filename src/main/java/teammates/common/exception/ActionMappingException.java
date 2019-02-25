package teammates.common.exception;

/**
 * Exception thrown when a controller class is failed to be instantiated from a given resource URL.
 */
@SuppressWarnings("serial")
public class ActionMappingException extends Exception {

    private final int statusCode;

    public ActionMappingException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
