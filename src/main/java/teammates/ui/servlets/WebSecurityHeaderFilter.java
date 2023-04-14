package teammates.ui.servlets;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Config;

/**
 * Filter to add web security headers.
 */
public class WebSecurityHeaderFilter implements Filter {

    private static final String IMG_SRC_CSP = Config.IS_DEV_SERVER
            ? "'self' data: http: https:"
            : "'self' data: https:";

    private static final String CSP_POLICY = String.join("; ", Arrays.asList(
            "default-src 'none'",
            "script-src 'self' https://www.google.com/recaptcha/ https://www.gstatic.com/recaptcha/ https://cdn.jsdelivr.net/  https://apis.google.com/",
            "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net/ https://fonts.googleapis.com/",
            "frame-src 'self' docs.google.com https://www.google.com/recaptcha/ https://*.firebaseapp.com/",
            "img-src " + IMG_SRC_CSP,
            "font-src 'self' https://cdn.jsdelivr.net/ https://fonts.gstatic.com/",
            "connect-src 'self' https://*.googleapis.com/",
            "manifest-src 'self'",
            "form-action 'none'",
            "frame-ancestors 'self'",
            "base-uri 'self'"
    ));

    @Override
    public void init(FilterConfig filterConfig) {
        // nothing to do
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("Content-Security-Policy", CSP_POLICY);
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "SAMEORIGIN");
        resp.setHeader("X-XSS-Protection", "1; mode=block");
        resp.setHeader("Strict-Transport-Security", "max-age=31536000");

        chain.doFilter(request, resp);
    }

    @Override
    public void destroy() {
        // nothing to do
    }

}
