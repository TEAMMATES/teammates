package teammates.ui.webapi;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Logger;
import teammates.common.util.RequestTracer;

/**
 * Extracts trace ID of HTTP requests.
 */
public class RequestTraceFilter implements Filter {

    private static final Logger log = Logger.getLogger();

    @Override
    public void init(FilterConfig filterConfig) {
        // nothing to do
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // The header X-AppEngine-QueueName cannot be spoofed as GAE will strip any user-sent X-AppEngine-QueueName headers.
        // Reference: https://cloud.google.com/appengine/docs/standard/java/taskqueue/push/creating-handlers#reading_request_headers
        boolean isRequestFromAppEngineQueue = request.getHeader("X-AppEngine-QueueName") != null;

        // GAE will terminate an instance if any request exceeds 10 minutes.
        // For GAE-invoked requests, we set the limit here minus a small grace period of 5 seconds
        // to ensure that the 10 minutes limit will not be exceeded.
        // For user-invoked requests, we keep the time limit at 1 minute (as how it was
        // in the previous GAE runtime environment) in order to not let user wait for excessively long,
        // as well as a reminder for us to keep optimizing our API response time.
        int timeoutInSeconds = isRequestFromAppEngineQueue ? 10 * 60 - 5 : 60;

        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Void> f = es.submit(() -> {
            RequestTracer.init(request.getHeader("X-Cloud-Trace-Context"), timeoutInSeconds);
            chain.doFilter(req, resp);
            return null;
        });

        try {
            f.get(timeoutInSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.severe(e.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(e));
            throwError(response, HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (TimeoutException e) {
            log.severe("TimeoutException caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(e));
            throwError(response, HttpStatus.SC_GATEWAY_TIMEOUT,
                    "The request exceeded the server timeout limit. Please try again later.");
        } finally {
            f.cancel(true);
            es.shutdown();
        }
    }

    @Override
    public void destroy() {
        // nothing to do
    }

    private void throwError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        JsonResult result = new JsonResult(message, statusCode);
        result.send(resp);
    }

}
