package teammates.common.datatransfer.logs;

import java.util.List;

import jakarta.annotation.Nullable;

/**
 * Contains specific structure and processing logic for exception log.
 */
public class ExceptionLogDetails extends LogDetails {

    private String exceptionClass;
    private List<String> exceptionClasses;
    private List<List<String>> exceptionStackTraces;
    @Nullable
    private List<String> exceptionMessages;
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

    public SourceLocation getLoggerSourceLocation() {
        return loggerSourceLocation;
    }

    public void setLoggerSourceLocation(SourceLocation loggerSourceLocation) {
        this.loggerSourceLocation = loggerSourceLocation;
    }

    @Override
    public void hideSensitiveInformation() {
        exceptionMessages = null;
        setMessage(null);
    }

}
