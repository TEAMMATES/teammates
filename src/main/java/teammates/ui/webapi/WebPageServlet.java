package teammates.ui.webapi;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Logger;

/**
 * Servlet that handles the single web page.
 */
@SuppressWarnings("serial")
public class WebPageServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger();

    private static final String CSP_POLICY = String.join("; ", Arrays.asList(
            "default-src 'none'",
            "script-src 'self' https://www.google.com/recaptcha/ https://www.gstatic.com/recaptcha/ https://cdn.jsdelivr.net/",
            "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net/",
            "frame-src 'self' docs.google.com https://www.google.com/recaptcha/",
            "img-src 'self' data: http: https:",
            "font-src 'self' https://cdn.jsdelivr.net/",
            "connect-src 'self'",
            "manifest-src 'self'",
            "form-action 'none'",
            "frame-ancestors 'self'",
            "base-uri 'self'"
    ));

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Security-Policy", CSP_POLICY);
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "SAMEORIGIN");
        resp.setHeader("X-XSS-Protection", "1; mode=block");
        resp.setHeader("Strict-Transport-Security", "max-age=31536000");
        try {
            req.getRequestDispatcher("/index.html").forward(req, resp);
        } catch (IllegalArgumentException e) {
            if (e.getClass().getSimpleName().equals("NotUtf8Exception")) {
                log.warning(TeammatesException.toStringWithStackTrace(e));
                resp.setStatus(HttpStatus.SC_BAD_REQUEST);
            } else {
                throw e;
            }
        }
    }

}
