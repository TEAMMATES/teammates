package teammates.ui.webapi;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Represents the result of executing an {@link Action}.
 */
abstract class ActionResult {

    String requestId;
    private final int statusCode;

    ActionResult(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Packages and forwards the action result to the HTTP response.
     */
    abstract void send(HttpServletResponse resp) throws IOException;

    int getStatusCode() {
        return statusCode;
    }

    void setRequestId(String requestId) {
        this.requestId = requestId;
    }

}
