package teammates.common.datatransfer.logs;

import jakarta.annotation.Nullable;

/**
 * Holds the details for a specific log event.
 */
public abstract class LogDetails {

    private LogEvent event;
    @Nullable
    private String message;

    protected LogDetails(LogEvent event) {
        this.event = event;
    }

    public LogEvent getEvent() {
        return event;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Hides sensitive, confidential information, particularly those that contain user information.
     */
    public abstract void hideSensitiveInformation();

}
