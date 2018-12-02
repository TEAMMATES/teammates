package teammates.ui.newcontroller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.util.JsonUtils;

/**
 * Action result in form of JSON object.
 *
 * <p>This is the most common format for REST-ful back-end API response.
 */
public class JsonResult extends ActionResult {

    private final ActionOutput output;

    public JsonResult(ActionOutput output) {
        super(HttpStatus.SC_OK);
        this.output = output;
    }

    public JsonResult(String message) {
        this(message, HttpStatus.SC_OK);
    }

    public JsonResult(String message, int statusCode) {
        super(statusCode);
        this.output = new MessageOutput(message);
    }

    public Object getOutput() {
        return output;
    }

    void setRequestId(String requestId) {
        this.output.setRequestId(requestId);
    }

    @Override
    public void send(HttpServletResponse resp) throws IOException {
        resp.setStatus(getStatusCode());
        resp.setContentType("application/json");
        PrintWriter pw = resp.getWriter();
        pw.print(JsonUtils.toJson(output));
    }

    /**
     * Generic output format for message-producing endpoint.
     */
    public static class MessageOutput extends ActionOutput {

        private final String message;

        public MessageOutput(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

    }

}
