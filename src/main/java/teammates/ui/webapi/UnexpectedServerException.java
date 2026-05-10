package teammates.ui.webapi;

/**
 * Exception thrown when an action catches a checked exception that should not occur given upstream
 * validation, e.g. an {@link teammates.common.exception.InvalidParametersException} bubbling up
 * from a code path whose parameters were already validated.
 *
 * <p>Routing this through the centralised {@link teammates.ui.servlets.WebApiServlet} error handler
 * (rather than returning a {@link JsonResult} with HTTP 500 directly) keeps logging and the response
 * body consistent with the other named exceptions defined in this package.
 *
 * <p>This corresponds to HTTP 500 error.
 */
public class UnexpectedServerException extends RuntimeException {

    public UnexpectedServerException(String message) {
        super(message);
    }

    public UnexpectedServerException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public UnexpectedServerException(String message, Throwable cause) {
        super(message, cause);
    }

}
