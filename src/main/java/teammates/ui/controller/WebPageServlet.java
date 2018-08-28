package teammates.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that handles the single web page.
 */
public class WebPageServlet extends HttpServlet {

    private static final String CSP_POLICY;

    static {
        List<String> policies = new ArrayList<>();
        policies.add("default-src 'none'");
        policies.add("script-src 'self'");
        policies.add("style-src 'self' 'unsafe-inline'");
        policies.add("frame-src 'self' docs.google.com");
        policies.add("img-src 'self' data:");
        policies.add("connect-src 'self'");
        policies.add("form-action 'none'");
        policies.add("frame-ancestors 'self'");
        policies.add("base-uri 'self'");
        CSP_POLICY = String.join("; ", policies);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Security-Policy", CSP_POLICY);
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "SAMEORIGIN");
        resp.setHeader("X-XSS-Protection", "1; mode=block");
        resp.setHeader("Strict-Transport-Security", "max-age=31536000");
        req.getRequestDispatcher("/dist/index.html").forward(req, resp);
    }

}
