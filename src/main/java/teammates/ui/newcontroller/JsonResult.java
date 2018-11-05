package teammates.ui.newcontroller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.util.JsonUtils;

/**
 * Action result in form of JSON object.
 *
 * <p>This is the most common format for REST-ful back-end API response.
 */
public class JsonResult extends ActionResult {

    private final Object output;

    public JsonResult(Object output) {
        super(HttpStatus.SC_OK);
        this.output = output;
    }

    public JsonResult(String message, int statusCode) {
        super(statusCode);

        Map<String, String> output = new HashMap<>();
        output.put("message", message);
        this.output = output;
    }

    public Object getOutput() {
        return output;
    }

    @Override
    public void send(HttpServletResponse resp) throws IOException {
        resp.setStatus(getStatusCode());
        resp.setContentType("application/json");
        PrintWriter pw = resp.getWriter();
        pw.print(JsonUtils.toJson(output));
    }

}
