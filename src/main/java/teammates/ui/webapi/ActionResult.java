package teammates.ui.webapi;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Represents the result of executing an {@link Action}.
 */
public abstract class ActionResult {

    private final int statusCode;

    ActionResult(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Packages and forwards the action result to the HTTP response.
     */
    public abstract void send(HttpServletResponse resp) throws IOException;

    public int getStatusCode() {
        return statusCode;
    }

}
