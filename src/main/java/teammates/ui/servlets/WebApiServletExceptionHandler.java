package teammates.ui.servlets;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.hibernate.HibernateException;

import teammates.common.exception.DeadlineExceededException;
import teammates.common.util.Logger;
import teammates.ui.exception.ActionMappingException;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.JsonResult;

/**
 * Maps servlet-layer exceptions to HTTP responses. Extracted from {@link WebApiServlet} for unit testing.
 */
final class WebApiServletExceptionHandler {

    private static final Logger log = Logger.getLogger();

    private WebApiServletExceptionHandler() {
    }

    /**
     * Handles an exception thrown while processing {@link WebApiServlet} and sends the HTTP response.
     *
     * @return the HTTP status code produced for request logging in {@link WebApiServlet}.
     */
    static int handleException(HttpServletResponse resp, Throwable t) throws IOException {
        if (t instanceof ActionMappingException) {
            ActionMappingException e = (ActionMappingException) t;
            int statusCode = e.getStatusCode();
            log.warning(e.getClass().getSimpleName() + " caught by WebApiServlet: " + e.getMessage(), e);
            throwError(resp, statusCode, e.getMessage());
            return statusCode;
        }
        if (t instanceof InvalidHttpRequestBodyException || t instanceof InvalidHttpParameterException) {
            int statusCode = HttpStatus.SC_BAD_REQUEST;
            Exception e = (Exception) t;
            log.warning(e.getClass().getSimpleName() + " caught by WebApiServlet: " + e.getMessage(), e);
            throwError(resp, statusCode, e.getMessage());
            return statusCode;
        }
        if (t instanceof UnauthorizedAccessException) {
            UnauthorizedAccessException uae = (UnauthorizedAccessException) t;
            int statusCode = HttpStatus.SC_FORBIDDEN;
            log.warning(uae.getClass().getSimpleName() + " caught by WebApiServlet: " + uae.getMessage(), uae);
            throwError(resp, statusCode,
                    uae.isShowErrorMessage() ? uae.getMessage() : "You are not authorized to access this resource.");
            return statusCode;
        }
        if (t instanceof EntityNotFoundException) {
            EntityNotFoundException enfe = (EntityNotFoundException) t;
            int statusCode = HttpStatus.SC_NOT_FOUND;
            log.warning(enfe.getClass().getSimpleName() + " caught by WebApiServlet: " + enfe.getMessage(), enfe);
            throwError(resp, statusCode, enfe.getMessage());
            return statusCode;
        }
        if (t instanceof InvalidOperationException) {
            InvalidOperationException ioe = (InvalidOperationException) t;
            int statusCode = HttpStatus.SC_CONFLICT;
            log.warning(ioe.getClass().getSimpleName() + " caught by WebApiServlet: " + ioe.getMessage(), ioe);
            throwError(resp, statusCode, ioe.getMessage());
            return statusCode;
        }
        if (t instanceof DeadlineExceededException) {
            DeadlineExceededException dee = (DeadlineExceededException) t;
            int statusCode = HttpStatus.SC_GATEWAY_TIMEOUT;
            log.severe(dee.getClass().getSimpleName() + " caught by WebApiServlet", dee);
            throwError(resp, statusCode, "The request exceeded the server timeout limit. Please try again later.");
            return statusCode;
        }
        if (t instanceof HibernateException) {
            HibernateException e = (HibernateException) t;
            int statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            log.severe(e.getClass().getSimpleName() + " caught by WebApiServlet: " + e.getMessage(), e);
            throwError(resp, statusCode, e.getMessage());
            return statusCode;
        }
        int statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
        log.severe(t.getClass().getSimpleName() + " caught by WebApiServlet: " + t.getMessage(), t);
        throwError(resp, statusCode, "The server encountered an error when processing your request.");
        return statusCode;
    }

    private static void throwError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        JsonResult result = new JsonResult(message, statusCode);
        result.send(resp);
    }

}
