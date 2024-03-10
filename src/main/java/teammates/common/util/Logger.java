package teammates.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import com.google.common.reflect.TypeToken;

import teammates.common.datatransfer.logs.ExceptionLogDetails;
import teammates.common.datatransfer.logs.InstanceLogDetails;
import teammates.common.datatransfer.logs.LogDetails;
import teammates.common.datatransfer.logs.LogSeverity;
import teammates.common.datatransfer.logs.RequestLogDetails;
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
     * Logs an instance startup event.
     */
    public void startup() {
        instance("STARTUP");
    }

    /**
     * Logs an instance shutdown event.
     */
    public void shutdown() {
        instance("SHUTDOWN");
    }

    @SuppressWarnings("PMD.SystemPrintln")
    private void instance(String instanceEvent) {
        String instanceId = Config.getInstanceId();
        String shortenedInstanceId = instanceId;
        if (shortenedInstanceId.length() > 32) {
            shortenedInstanceId = shortenedInstanceId.substring(0, 32);
        }

        InstanceLogDetails details = new InstanceLogDetails();
        details.setInstanceId(instanceId);
        details.setInstanceEvent(instanceEvent);

        String message = "Instance " + instanceEvent.toLowerCase() + ": " + shortenedInstanceId;

        Map<String, Object> payload = new HashMap<>();
        payload.put("message", message);
        payload.put("severity", LogSeverity.INFO);

        Map<String, Object> detailsSpecificPayload =
                JsonUtils.fromJson(JsonUtils.toCompactJson(details), new TypeToken<Map<String, Object>>(){}.getType());
        payload.putAll(detailsSpecificPayload);

        // Need to use println as the logger is disabled when the instance is shutting down
        System.out.println(JsonUtils.toCompactJson(payload));
    }

    /**
     * Logs an HTTP request.
     */
    public void request(HttpServletRequest request, int statusCode, String message) {
        request(request, statusCode, message, new RequestLogUser(), null, null);
    }

    /**
     * Logs an HTTP request.
     */
    public void request(HttpServletRequest request, int statusCode, String message,
                        RequestLogUser userInfo, String requestBody, String actionClass) {
        long timeElapsed = RequestTracer.getTimeElapsedMillis();
        String method = request.getMethod();
        String requestUrl = request.getRequestURI();
        RequestLogDetails details = new RequestLogDetails();
        details.setResponseStatus(statusCode);
        details.setResponseTime(timeElapsed);
        details.setRequestMethod(method);
        details.setRequestUrl(requestUrl);
        details.setUserAgent(request.getHeader("User-Agent"));
        details.setWebVersion(request.getHeader(Const.HeaderNames.WEB_VERSION));
        details.setReferrer(request.getHeader("referer"));
        details.setInstanceId(Config.getInstanceId());
        details.setRequestParams(HttpRequestHelper.getRequestParameters(request));
        details.setRequestHeaders(HttpRequestHelper.getRequestHeaders(request));

        if (request.getParameter(Const.ParamsNames.REGKEY) != null && userInfo.getRegkey() == null) {
            userInfo.setRegkey(request.getParameter(Const.ParamsNames.REGKEY));
        }
        details.setUserInfo(userInfo);
        details.setRequestBody(requestBody);
        details.setActionClass(actionClass);

        String logMessage = String.format("[%s] [%sms] [%s %s] %s",
                statusCode, timeElapsed, method, requestUrl, message);

        event(logMessage, details);
    }

    /**
     * Logs a particular event at INFO level.
     */
    public void event(String message, LogDetails details) {
        String logMessage;
        if (Config.IS_DEV_SERVER) {
            logMessage = formatLogMessageForHumanDisplay(message) + " extra_info: "
                    + JsonUtils.toCompactJson(details);
        } else {
            Map<String, Object> payload = getBaseCloudLoggingPayload(message, LogSeverity.INFO);
            Map<String, Object> detailsSpecificPayload =
                    JsonUtils.fromJson(JsonUtils.toCompactJson(details), new TypeToken<Map<String, Object>>(){}.getType());
            payload.putAll(detailsSpecificPayload);

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
        if (Config.IS_DEV_SERVER) {
            StringWriter sw = new StringWriter();
            try (PrintWriter pw = new PrintWriter(sw)) {
                t.printStackTrace(pw);
            }

            return formatLogMessageForHumanDisplay(message) + " stack_trace: "
                    + System.lineSeparator() + sw.toString();
        }

        Map<String, Object> payload = getBaseCloudLoggingPayload(message, severity);

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

        ExceptionLogDetails details = new ExceptionLogDetails();
        details.setExceptionClass(t.getClass().getSimpleName());
        details.setExceptionClasses(exceptionClasses);
        details.setExceptionStackTraces(exceptionStackTraces);
        details.setExceptionMessages(exceptionMessages);

        StackTraceElement tSource = getFirstInternalStackTrace(t);
        if (tSource != null) {
            SourceLocation tSourceLocation = new SourceLocation(
                    tSource.getClassName(), (long) tSource.getLineNumber(), tSource.getMethodName());

            // Replace the source location with the Throwable's source location instead
            SourceLocation loggerSourceLocation = (SourceLocation) payload.get("logging.googleapis.com/sourceLocation");
            payload.put("logging.googleapis.com/sourceLocation", tSourceLocation);

            details.setLoggerSourceLocation(loggerSourceLocation);
        }

        Map<String, Object> detailsSpecificPayload =
                JsonUtils.fromJson(JsonUtils.toCompactJson(details), new TypeToken<Map<String, Object>>(){}.getType());
        payload.putAll(detailsSpecificPayload);

        return JsonUtils.toCompactJson(payload);
    }

    /**
     * Returns the first stack trace for the throwable that originates from an internal class
     * (i.e. package name starting with teammates).
     * If no such stack trace is found, return the first element of the stack trace list.
     */
    private StackTraceElement getFirstInternalStackTrace(Throwable t) {
        StackTraceElement[] stackTraces = t.getStackTrace();
        if (stackTraces.length == 0) {
            return null;
        }
        return Arrays.stream(stackTraces)
                .filter(ste -> ste.getClassName().startsWith("teammates"))
                .findFirst()
                .orElse(stackTraces[0]);
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
        if (Config.IS_DEV_SERVER) {
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
