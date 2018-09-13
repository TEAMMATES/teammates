package teammates.ui.newcontroller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import teammates.common.util.JsonUtils;

/**
 * Action result in form of JSON key-value mappings.
 *
 * <p>This is the most common format for REST-ful back-end API response.
 */
public class JsonResult extends ActionResult {

    private final Map<String, Object> output;

    public JsonResult(Map<String, Object> output) {
        this.output = output;
    }

    @Override
    public void send(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter pw = resp.getWriter();
        pw.print(JsonUtils.toJson(output));
    }

}
