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
import teammates.ui.output.ApiOutput;
import teammates.ui.output.MessageOutput;

/**
 * Action result in form of JSON object.
 *
 * <p>This is the most common format for REST-ful back-end API response.
 */
class JsonResult extends ActionResult {

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

    JsonResult(String message, int statusCode) {
        super(statusCode);
        this.output = new MessageOutput(message);
        this.cookies = new ArrayList<>();
    }

    ApiOutput getOutput() {
        return output;
    }

    @Override
    void send(HttpServletResponse resp) throws IOException {
        output.setRequestId(Config.getRequestId());
        for (Cookie cookie : cookies) {
            cookie.setSecure(!Config.isDevServer());
            resp.addCookie(cookie);
        }
        resp.setStatus(getStatusCode());
        resp.setContentType("application/json");
        PrintWriter pw = resp.getWriter();
        pw.print(JsonUtils.toCompactJson(output));
    }

    List<Cookie> getCookies() {
        return cookies;
    }

}
