package teammates.ui.newcontroller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Config;

/**
 * Allow CORS for front-end development server.
 */
public class CorsFilter implements Filter {

    private static final String DEV_FRONTEND_URL = "http://localhost:4200";

    @Override
    public void init(FilterConfig filterConfig) {
        // nothing to do
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (Config.isDevServer()) {
            HttpServletResponse response = (HttpServletResponse) res;
            response.setHeader("Access-Control-Allow-Origin", DEV_FRONTEND_URL);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        // nothing to do
    }

}
