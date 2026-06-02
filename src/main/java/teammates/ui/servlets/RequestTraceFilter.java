package teammates.ui.servlets;

import java.io.IOException;
import java.security.SecureRandom;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpStatus;
import org.eclipse.jetty.http.BadMessageException;

import teammates.common.util.AutomatedRequestAuth;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.RequestTracer;
import teammates.ui.webapi.JsonResult;

/**
 * Extracts request ID of HTTP requests.
 */
public class RequestTraceFilter implements Filter {

    private static final Logger log = Logger.getLogger();
    private final SecureRandom random = new SecureRandom();

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) resp;

        response.setHeader("Strict-Transport-Security", "max-age=31536000");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");

        HttpServletRequest request = (HttpServletRequest) req;

        String requestId = request.getHeader("X-Cloud-Trace-Context");
        String traceId; // TODO: rename to requestId and remove dependency on Cloud Trace format
        if (requestId == null) {
            // Generate random hexadecimal string of length 32
            byte[] resBuf = new byte[16];
            random.nextBytes(resBuf);
            traceId = Hex.encodeHexString(resBuf);
        } else {
            // X-Cloud-Trace-Context header is in form of TRACE_ID/SPAN_ID;o=TRACE_TRUE
            String[] traceAndSpan = requestId.split("/", 2);
            traceId = traceAndSpan[0];
        }

        response.setHeader(Const.HeaderNames.REQUEST_ID, traceId);

        // Worker / Cron requests (from Cloud Tasks with bearer token) may run longer.
        // For these requests, we set the limit to 10 minutes minus a small grace period.
        // For other requests, we keep the time limit at 1 minute.
        boolean isAutomatedWorkerOrCronRequest = AutomatedRequestAuth.isTrustedCronOrWorkerRequest(request);
        int timeoutInSeconds = isAutomatedWorkerOrCronRequest ? 10 * 60 - 5 : 60;

        RequestTracer.init(traceId, timeoutInSeconds);

        if (Config.MAINTENANCE) {
            throwError(request, response, HttpStatus.SC_SERVICE_UNAVAILABLE,
                    "The server is currently undergoing some maintenance.");
            return;
        }

        try {
            // Make sure that all parameters are valid UTF-8
            request.getParameterMap();
        } catch (BadMessageException e) {
            throwError(request, response, HttpStatus.SC_BAD_REQUEST, e.getMessage());
            return;
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
