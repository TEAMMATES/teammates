package teammates.ui.webapi;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import teammates.common.util.RequestTracer;

/**
 * Extracts trace ID of HTTP requests.
 */
public class RequestTraceFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // nothing to do
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        RequestTracer.init(request.getHeader("X-Cloud-Trace-Context"));

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
        // nothing to do
    }

}
