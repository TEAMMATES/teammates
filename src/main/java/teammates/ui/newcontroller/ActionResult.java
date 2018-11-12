package teammates.ui.newcontroller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Represents the result of executing an {@link Action}.
 */
public abstract class ActionResult {

    private final int statusCode;

    protected ActionResult(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Packages and forwards the action result to the HTTP response.
     */
    public abstract void send(HttpServletResponse resp) throws IOException;

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Generic output format for all API requests.
     */
    static class ActionOutput {

        private String requestId;

        public String getRequestId() {
            return requestId;
        }

        void setRequestId(String requestId) {
            this.requestId = requestId;
        }

    }

}
