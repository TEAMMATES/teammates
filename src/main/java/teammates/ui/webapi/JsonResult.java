package teammates.ui.webapi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.util.Config;
import teammates.common.util.JsonUtils;
import teammates.common.util.RequestTracer;
import teammates.ui.output.ApiOutput;
import teammates.ui.output.MessageOutput;

/**
 * Action result in form of JSON object.
 *
 * <p>This is the most common format for REST-ful back-end API response.
 */
public class JsonResult extends ActionResult {

    private final ApiOutput output;
    private List<Cookie> cookies;

    JsonResult(ApiOutput output) {
        super(HttpStatus.SC_OK);
        this.output = output;
        this.cookies = new ArrayList<>();
    }

    JsonResult(ApiOutput output, List<Cookie> cookies) {
        this(output);
        this.cookies = cookies;
    }

    JsonResult(String message) {
        this(message, HttpStatus.SC_OK);
    }

    public JsonResult(String message, int statusCode) {
        super(statusCode);
        this.output = new MessageOutput(message);
        this.cookies = new ArrayList<>();
    }

    public JsonResult(ApiOutput output, int statusCode) {
        super(statusCode);
        this.output = output;
        this.cookies = new ArrayList<>();
    }

    ApiOutput getOutput() {
        return output;
    }

    @Override
    public void send(HttpServletResponse resp) throws IOException {
        output.setRequestId(RequestTracer.getTraceId());
        for (Cookie cookie : cookies) {
            cookie.setSecure(!Config.IS_DEV_SERVER);
            resp.addCookie(cookie);
        }
        resp.setStatus(getStatusCode());
        resp.setContentType("application/json");
        PrintWriter pw = resp.getWriter();
        JsonUtils.toCompactJson(output, pw);
    }

    List<Cookie> getCookies() {
        return cookies;
    }

}
