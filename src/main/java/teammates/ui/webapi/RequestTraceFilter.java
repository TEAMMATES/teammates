package teammates.ui.webapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.LogEvent;
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
        HttpServletResponse response = (HttpServletResponse) resp;

        response.setHeader("Strict-Transport-Security", "max-age=31536000");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");

        if (Config.MAINTENANCE) {
            throwError(response, HttpStatus.SC_SERVICE_UNAVAILABLE,
                    "The server is currently undergoing some maintenance.");
            return;
        }

        HttpServletRequest request = (HttpServletRequest) req;

        try {
            // Make sure that all parameters are valid UTF-8
            request.getParameterMap();
        } catch (RuntimeException e) {
            if (e.getClass().getSimpleName().equals("BadMessageException")) {
                throwError(response, HttpStatus.SC_BAD_REQUEST, e.getMessage());
                return;
            }
            throw e;
        }

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
            String requestId = request.getHeader("X-Cloud-Trace-Context");
            String traceId;
            String spanId = null;
            if (requestId == null) {
                traceId = RandomStringUtils.randomAlphanumeric(32);
            } else {
                // X-Cloud-Trace-Context header is in form of TRACE_ID/SPAN_ID;o=TRACE_TRUE
                String[] traceAndSpan = requestId.split("/", 2);
                traceId = traceAndSpan[0];
                if (traceAndSpan.length == 2) {
                    spanId = traceAndSpan[1].split(";")[0];
                }
            }
            RequestTracer.init(traceId, spanId, timeoutInSeconds);

            Map<String, Object> requestDetails = new HashMap<>();
            requestDetails.put("requestMethod", request.getMethod());
            requestDetails.put("requestUrl", request.getRequestURI());
            requestDetails.put("userAgent", request.getHeader("User-Agent"));
            requestDetails.put("requestParams", HttpRequestHelper.getRequestParameters(request));
            requestDetails.put("requestHeaders", HttpRequestHelper.getRequestHeaders(request));

            String message = "Request " + RequestTracer.getTraceId() + " received: " + request.getRequestURI();
            log.event(LogEvent.REQUEST_RECEIVED, message, requestDetails);

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

        long timeElapsed = RequestTracer.getTimeElapsedMillis();
        Map<String, Object> requestDetails = new HashMap<>();
        requestDetails.put("responseStatus", statusCode);
        requestDetails.put("responseTime", timeElapsed);

        String logMessage = "Response " + RequestTracer.getTraceId() + " dispatched with "
                + statusCode + " in " + timeElapsed + "ms";
        log.event(LogEvent.RESPONSE_DISPATCHED, logMessage, requestDetails);
    }

}
