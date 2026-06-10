package teammates.common.util;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Utility class for handling HTTP responses.
 */
public final class HttpResponseHelper {

    private static final Logger log = Logger.getLogger();

    private HttpResponseHelper() {
        // Utility class.
    }

    /**
     * Logs the given error message and prints it in the HTTP response.
     */
    public static void logAndPrintError(HttpServletRequest req, HttpServletResponse resp, int status, String message)
            throws IOException {
        resp.setStatus(status);
        resp.getWriter().print(message);

        log.request(req, status, message);
    }

}
