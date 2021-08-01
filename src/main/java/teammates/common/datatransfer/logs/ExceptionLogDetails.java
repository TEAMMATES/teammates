package teammates.common.datatransfer.logs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains specific structure and processing logic for exception log.
 */
public class ExceptionLogDetails extends LogDetails {

    private String exceptionClass;
    private List<String> exceptionClasses;
    private List<List<String>> exceptionStackTraces;
    private List<String> exceptionMessages;
    private List<String> exceptionStackTrace;
    private SourceLocation loggerSourceLocation;

    public ExceptionLogDetails() {
        super(LogEvent.EXCEPTION_LOG);
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public List<String> getExceptionClasses() {
        return exceptionClasses;
    }

    public void setExceptionClasses(List<String> exceptionClasses) {
        this.exceptionClasses = exceptionClasses;
    }

    public List<List<String>> getExceptionStackTraces() {
        return exceptionStackTraces;
    }

    public void setExceptionStackTraces(List<List<String>> exceptionStackTraces) {
        this.exceptionStackTraces = exceptionStackTraces;
    }

    public List<String> getExceptionMessages() {
        return exceptionMessages;
    }

    public void setExceptionMessages(List<String> exceptionMessages) {
        this.exceptionMessages = exceptionMessages;
    }

    public List<String> getExceptionStackTrace() {
        return exceptionStackTrace;
    }

    public void setExceptionStackTrace(List<String> exceptionStackTrace) {
        this.exceptionStackTrace = exceptionStackTrace;
    }

    public SourceLocation getLoggerSourceLocation() {
        return loggerSourceLocation;
    }

    public void setLoggerSourceLocation(SourceLocation loggerSourceLocation) {
        this.loggerSourceLocation = loggerSourceLocation;
    }

    /**
     * Converts the internally-stored stack traces into readable format.
     *
     * @param showMessages decides whether exception messages are shown
     */
    public void convertStackTrace(boolean showMessages) {
        if (exceptionClasses == null || exceptionMessages == null || exceptionStackTraces == null
                || exceptionClasses.size() != exceptionStackTraces.size()
                || exceptionClasses.size() != exceptionMessages.size()) {
            return;
        }

        List<String> exceptionStackTrace = new ArrayList<>();
        for (int i = 0; i < exceptionClasses.size(); i++) {
            StringBuilder firstLine = new StringBuilder(exceptionClasses.get(i));
            if (showMessages) {
                firstLine.append(": ").append(exceptionMessages.get(i));
            }
            exceptionStackTrace.add(firstLine.toString());
            exceptionStackTrace.addAll(exceptionStackTraces.get(i).stream()
                    .map(line -> "    at " + line)
                    .collect(Collectors.toList()));
        }

        this.exceptionStackTrace = exceptionStackTrace;

        exceptionClasses = null;
        exceptionStackTraces = null;
        exceptionMessages = null;
    }

    @Override
    public void hideSensitiveInformation() {
        setMessage(null);
    }

}
