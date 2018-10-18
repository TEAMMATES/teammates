package teammates.ui.newcontroller;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;

/**
 * Checks and validates origin of HTTP requests.
 */
public class OriginCheckFilter implements Filter {

    private static final String ALLOWED_HTTP_METHODS = String.join(", ", Arrays.asList(
            HttpMethod.GET,
            HttpMethod.POST,
            HttpMethod.PUT,
            HttpMethod.DELETE,
            HttpMethod.OPTIONS
    ));

    @Override
    public void init(FilterConfig filterConfig) {
        // nothing to do
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (Config.isDevServer()) {
            response.setHeader("Access-Control-Allow-Origin", Config.APP_FRONTENDDEV_URL);
            response.setHeader("Access-Control-Allow-Methods", ALLOWED_HTTP_METHODS);
            response.setHeader("Access-Control-Allow-Headers", Const.CsrfConfig.TOKEN_HEADER_NAME);
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        if (Config.CSRF_KEY.equals(request.getParameter("csrfkey"))) {
            // Can bypass CSRF check with the correct key
            chain.doFilter(req, res);
            return;
        }

        String referrer = request.getHeader("referer");
        if (referrer == null) {
            // Requests with missing referrer information are given the benefit of the doubt
            // to accommodate users who choose to disable the HTTP referrer setting in their browser
            // for privacy reasons
        } else if (!isHttpReferrerValid(referrer, request.getRequestURL().toString())) {
            denyAccess("Invalid HTTP referrer.", response);
            return;
        }

        switch (request.getMethod()) {
        case HttpMethod.POST:
        case HttpMethod.PUT:
        case HttpMethod.DELETE:
            String message = getCsrfTokenErrorIfAny(request);
            if (message != null) {
                denyAccess(message, response);
                return;
            }
            break;
        default:
            break;
        }

        chain.doFilter(req, res);
    }

    /**
     * Validates the HTTP referrer against the request URL.
     * The origin is the base URL of the HTTP referrer, which includes the protocol and authority
     * (host name + port number if specified).
     * Similarly, the target is the base URL of the requested action URL.
     * For the referrer to be considered valid, origin and target must match exactly.
     * Otherwise, the request is likely to be a CSRF attack, and is considered invalid.
     *
     * <p>Example of malicious request originating from embedded image in email:
     * <pre>
     * Request URL: https://teammatesv4.appspot.com/page/instructorCourseDelete?courseid=abcdef
     * Referrer:    https://mail.google.com/mail/u/0/
     *
     * Target: https://teammatesv4.appspot.com
     * Origin: https://mail.google.com
     * </pre>
     * Origin does not match target. This request is invalid.</p>
     *
     * <p>Example of legitimate request originating from instructor courses page:
     * <pre>
     * Request URL: https://teammatesv4.appspot.com/page/instructorCourseDelete?courseid=abcdef
     * Referrer:    https://teammatesv4.appspot.com/page/instructorCoursesPage
     *
     * Target: https://teammatesv4.appspot.com
     * Origin: https://teammatesv4.appspot.com
     * </pre>
     * Origin matches target. This request is valid.</p>
     */
    private boolean isHttpReferrerValid(String referrer, String requestUrl) {
        String origin;
        try {
            origin = new Url(referrer).getBaseUrl();
        } catch (AssertionError e) { // due to MalformedURLException
            return false;
        }

        if (Config.isDevServer() && origin.equals(Config.APP_FRONTENDDEV_URL)) {
            // Exception to the rule: front-end dev server requesting data from back-end dev server
            return true;
        }

        String target = new Url(requestUrl).getBaseUrl();
        return origin.equals(target);
    }

    private String getCsrfTokenErrorIfAny(HttpServletRequest request) {
        String csrfToken = request.getHeader(Const.CsrfConfig.TOKEN_HEADER_NAME);
        if (csrfToken == null) {
            return "Missing CSRF token.";
        }

        String sessionId = request.getRequestedSessionId();
        if (sessionId == null) {
            // Newly-created session
            sessionId = request.getSession().getId();
        }

        try {
            return sessionId.equals(StringHelper.decrypt(csrfToken)) ? null : "Invalid CSRF token.";
        } catch (InvalidParametersException e) {
            return "Invalid CSRF token.";
        }
    }

    private void denyAccess(String message, HttpServletResponse response) throws IOException {
        JsonResult result = new JsonResult(message, 403);
        result.send(response);
    }

    @Override
    public void destroy() {
        // nothing to do
    }

}
