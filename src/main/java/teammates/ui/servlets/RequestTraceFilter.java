package teammates.ui.servlets;

import java.io.IOException;
import java.util.Random;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpStatus;

import teammates.common.util.Config;
import teammates.common.util.Logger;
import teammates.common.util.RequestTracer;
import teammates.ui.webapi.JsonResult;

/**
 * Extracts trace ID of HTTP requests.
 */
public class RequestTraceFilter implements Filter {

    private static final Logger log = Logger.getLogger();

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) resp;

        response.setHeader("Strict-Transport-Security", "max-age=31536000");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");

        HttpServletRequest request = (HttpServletRequest) req;

        String requestId = request.getHeader("X-Cloud-Trace-Context");
        String traceId;
        String spanId = null;
        if (requestId == null) {
            // Generate random hexadecimal string of length 32
            byte[] resBuf = new byte[16];
            new Random().nextBytes(resBuf);
            traceId = Hex.encodeHexString(resBuf);
        } else {
            // X-Cloud-Trace-Context header is in form of TRACE_ID/SPAN_ID;o=TRACE_TRUE
            String[] traceAndSpan = requestId.split("/", 2);
            traceId = traceAndSpan[0];
            if (traceAndSpan.length == 2) {
                spanId = traceAndSpan[1].split(";")[0];
            }
        }

        // The header X-AppEngine-QueueName cannot be spoofed as GAE will strip any user-sent X-AppEngine-QueueName headers.
        // Reference: https://cloud.google.com/tasks/docs/creating-appengine-handlers#reading_task_request_headers
        boolean isRequestFromAppEngineQueue = request.getHeader("X-AppEngine-QueueName") != null;

        // GAE will terminate an instance if any request exceeds 10 minutes.
        // For GAE-invoked requests, we set the limit here minus a small grace period of 5 seconds
        // to ensure that the 10 minutes limit will not be exceeded.
        // For user-invoked requests, we keep the time limit at 1 minute (as how it was
        // in the previous GAE runtime environment) in order to not let user wait for excessively long,
        // as well as a reminder for us to keep optimizing our API response time.
        int timeoutInSeconds = isRequestFromAppEngineQueue ? 10 * 60 - 5 : 60;

        RequestTracer.init(traceId, spanId, timeoutInSeconds);

        if (Config.MAINTENANCE) {
            throwError(request, response, HttpStatus.SC_SERVICE_UNAVAILABLE,
                    "The server is currently undergoing some maintenance.");
            return;
        }

        try {
            // Make sure that all parameters are valid UTF-8
            request.getParameterMap();
        } catch (RuntimeException e) {
            if ("BadMessageException".equals(e.getClass().getSimpleName())) {
                throwError(request, response, HttpStatus.SC_BAD_REQUEST, e.getMessage());
                return;
            }
            throw e;
        }

        chain.doFilter(req, resp);
    }

    private void throwError(HttpServletRequest req, HttpServletResponse resp, int statusCode, String message)
            throws IOException {
        JsonResult result = new JsonResult(message, statusCode);
        result.send(resp);

        log.request(req, statusCode, message);
    }

}
