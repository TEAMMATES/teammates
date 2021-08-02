package teammates.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.logs.LogEvent;
import teammates.common.datatransfer.logs.LogSeverity;
import teammates.common.datatransfer.logs.RequestLogUser;
import teammates.common.datatransfer.logs.SourceLocation;

/**
 * Allows any component of the application to log messages at appropriate levels.
 */
@SuppressWarnings("PMD.MoreThanOneLogger") // class is designed as a facade for two different loggers
public final class Logger {

    private final java.util.logging.Logger standardLog;
    private final java.util.logging.Logger errorLog;

    private Logger() {
        StackTraceElement logRequester = getLoggerSource();
        String loggerName = logRequester == null ? "null" : logRequester.getClassName();
        this.standardLog = java.util.logging.Logger.getLogger(loggerName + "-out");
        this.standardLog.setUseParentHandlers(false);
        this.standardLog.addHandler(new StdOutConsoleHandler());

        this.errorLog = java.util.logging.Logger.getLogger(loggerName + "-err");
    }

    public static Logger getLogger() {
        return new Logger();
    }

    /**
     * Logs a message at FINE level.
     */
    public void fine(String message) {
        standardLog.fine(formatLogMessage(message, LogSeverity.DEBUG));
    }

    /**
     * Logs a message at INFO level.
     */
    public void info(String message) {
        standardLog.info(formatLogMessage(message, LogSeverity.INFO));
    }

    /**
     * Logs an HTTP request.
     */
    public void request(HttpServletRequest request, int statusCode, String message) {
        request(request, statusCode, message, new RequestLogUser(), null);
    }

    /**
     * Logs an HTTP request.
     */
    public void request(HttpServletRequest request, int statusCode, String message,
                        RequestLogUser userInfo, String actionClass) {
        long timeElapsed = RequestTracer.getTimeElapsedMillis();
        String method = request.getMethod();
        String requestUrl = request.getRequestURI();
        Map<String, Object> requestDetails = new HashMap<>();
        requestDetails.put("responseStatus", statusCode);
        requestDetails.put("responseTime", timeElapsed);
        requestDetails.put("requestMethod", method);
        requestDetails.put("requestUrl", requestUrl);
        requestDetails.put("userAgent", request.getHeader("User-Agent"));
        requestDetails.put("requestParams", HttpRequestHelper.getRequestParameters(request));
        requestDetails.put("requestHeaders", HttpRequestHelper.getRequestHeaders(request));

        if (request.getParameter(Const.ParamsNames.REGKEY) != null && userInfo.getRegkey() == null) {
            userInfo.setRegkey(request.getParameter(Const.ParamsNames.REGKEY));
        }
        requestDetails.put("userInfo", userInfo);
        requestDetails.put("actionClass", actionClass);

        String logMessage = String.format("[%s] [%sms] [%s %s] %s",
                statusCode, timeElapsed, method, requestUrl, message);

        event(LogEvent.REQUEST_LOG, logMessage, requestDetails);
    }

    /**
     * Logs a particular event at INFO level.
     */
    public void event(LogEvent event, String message, Map<String, Object> details) {
        String logMessage;
        if (Config.isDevServer()) {
            logMessage = formatLogMessageForHumanDisplay(message) + " extra_info: "
                    + JsonUtils.toCompactJson(details);
        } else {
            Map<String, Object> payload = getBaseCloudLoggingPayload(message, LogSeverity.INFO);
            payload.putAll(details);
            payload.put("event", event);

            logMessage = JsonUtils.toCompactJson(payload);
        }
        standardLog.info(logMessage);
    }

    /**
     * Logs a message at WARNING level.
     */
    public void warning(String message) {
        standardLog.warning(formatLogMessage(message, LogSeverity.WARNING));
    }

    /**
     * Logs a message at WARNING level.
     */
    public void warning(String message, Throwable t) {
        String logMessage = getLogMessageWithStackTrace(message, t, LogSeverity.WARNING);
        standardLog.warning(logMessage);
    }

    /**
     * Logs a message at SEVERE level.
     */
    public void severe(String message) {
        errorLog.severe(formatLogMessage(message, LogSeverity.ERROR));
    }

    /**
     * Logs a message at SEVERE level.
     */
    public void severe(String message, Throwable t) {
        String logMessage = getLogMessageWithStackTrace(message, t, LogSeverity.ERROR);
        errorLog.severe(logMessage);
    }

    private String getLogMessageWithStackTrace(String message, Throwable t, LogSeverity severity) {
        String logMessage;
        if (Config.isDevServer()) {
            StringWriter sw = new StringWriter();
            try (PrintWriter pw = new PrintWriter(sw)) {
                t.printStackTrace(pw);
            }

            logMessage = formatLogMessageForHumanDisplay(message) + " stack_trace: "
                    + System.lineSeparator() + sw.toString();
        } else {
            StackTraceElement tSource = t.getStackTrace()[0];
            SourceLocation tSourceLocation = new SourceLocation(
                    tSource.getClassName(), (long) tSource.getLineNumber(), tSource.getMethodName());

            List<String> exceptionClasses = new ArrayList<>();
            List<List<String>> exceptionStackTraces = new ArrayList<>();
            List<String> exceptionMessages = new ArrayList<>();

            Throwable currentT = t;
            while (currentT != null) {
                exceptionClasses.add(currentT.getClass().getName());
                exceptionStackTraces.add(getStackTraceToDisplay(currentT));
                exceptionMessages.add(currentT.getMessage());

                currentT = currentT.getCause();
            }

            Map<String, Object> payload = getBaseCloudLoggingPayload(message, severity);

            // Replace the source location with the Throwable's source location instead
            Object loggerSourceLocation = payload.get("logging.googleapis.com/sourceLocation");
            payload.put("logging.googleapis.com/sourceLocation", tSourceLocation);
            payload.put("loggerSourceLocation", loggerSourceLocation);

            payload.put("exceptionClass", t.getClass().getSimpleName());
            payload.put("exceptionClasses", exceptionClasses);
            payload.put("exceptionStackTraces", exceptionStackTraces);
            payload.put("exceptionMessages", exceptionMessages);
            payload.put("event", LogEvent.EXCEPTION_LOG);

            logMessage = JsonUtils.toCompactJson(payload);
        }

        return logMessage;
    }

    private List<String> getStackTraceToDisplay(Throwable t) {
        List<String> stackTraceToDisplay = new ArrayList<>();
        for (StackTraceElement ste : t.getStackTrace()) {
            String stClass = ste.getClassName();
            if (stClass.startsWith("org.eclipse.jetty.servlet")) {
                // Everything past this line is the internal workings of Jetty
                // and does not provide anything useful for debugging
                stackTraceToDisplay.add("...");
                break;
            }
            stackTraceToDisplay.add(String.format("%s.%s(%s:%s)",
                    ste.getClassName(), ste.getMethodName(), ste.getFileName(), ste.getLineNumber()));
        }
        return stackTraceToDisplay;
    }

    private String formatLogMessage(String message, LogSeverity severity) {
        if (Config.isDevServer()) {
            return formatLogMessageForHumanDisplay(message);
        }
        return formatLogMessageForCloudLogging(message, severity);
    }

    private String formatLogMessageForHumanDisplay(String message) {
        StringBuilder prefix = new StringBuilder();

        StackTraceElement source = getLoggerSource();
        if (source != null) {
            prefix.append(source.getClassName()).append(':')
                    .append(source.getMethodName()).append(':')
                    .append(source.getLineNumber()).append(':');
        }
        prefix.append(' ');

        if (RequestTracer.getTraceId() == null) {
            return prefix.toString() + message;
        }
        return prefix.toString() + "[" + RequestTracer.getTraceId() + "] " + message;
    }

    private String formatLogMessageForCloudLogging(String message, LogSeverity severity) {
        return JsonUtils.toCompactJson(getBaseCloudLoggingPayload(message, severity));
    }

    private Map<String, Object> getBaseCloudLoggingPayload(String message, LogSeverity severity) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", message);
        payload.put("severity", severity);

        StackTraceElement source = getLoggerSource();
        if (source != null) {
            SourceLocation sourceLocation = new SourceLocation(
                    source.getClassName(), (long) source.getLineNumber(), source.getMethodName());
            payload.put("logging.googleapis.com/sourceLocation", sourceLocation);
        }

        if (RequestTracer.getTraceId() != null) {
            payload.put("logging.googleapis.com/trace",
                    "projects/" + Config.APP_ID + "/traces/" + RequestTracer.getTraceId());
        }

        if (RequestTracer.getSpanId() != null) {
            payload.put("logging.googleapis.com/spanId", RequestTracer.getSpanId());
        }

        return payload;
    }

    private StackTraceElement getLoggerSource() {
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stes.length; i++) {
            StackTraceElement ste = stes[i];
            if (ste.getClassName().equals(Logger.class.getName()) && i + 1 < stes.length) {
                StackTraceElement nextSte = stes[i + 1];
                if (!nextSte.getClassName().equals(Logger.class.getName())) {
                    return nextSte;
                }
            }
        }
        return null;
    }

}
