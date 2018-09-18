package teammates.ui.newcontroller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import teammates.common.util.JsonUtils;

/**
 * Action result in form of JSON key-value mappings.
 *
 * <p>This is the most common format for REST-ful back-end API response.
 */
public class JsonResult extends ActionResult {

    private final Object output;
    private final int statusCode;

    public JsonResult(Object output) {
        this.output = output;
        this.statusCode = 200;
    }

    public JsonResult(String message, int statusCode) {
        Map<String, String> output = new HashMap<>();
        output.put("message", message);
        this.output = output;
        this.statusCode = statusCode;
    }

    @Override
    public void send(HttpServletResponse resp) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        PrintWriter pw = resp.getWriter();
        pw.print(JsonUtils.toJson(output));
    }

}
