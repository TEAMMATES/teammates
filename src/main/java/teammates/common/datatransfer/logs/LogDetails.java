package teammates.common.datatransfer.logs;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Holds the details for a specific log event.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "event",
        visible = true,
        defaultImpl = DefaultLogDetails.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RequestLogDetails.class, name = "REQUEST_LOG"),
        @JsonSubTypes.Type(value = ExceptionLogDetails.class, name = "EXCEPTION_LOG"),
        @JsonSubTypes.Type(value = InstanceLogDetails.class, name = "INSTANCE_LOG"),
        @JsonSubTypes.Type(value = EmailSentLogDetails.class, name = "EMAIL_SENT"),
        @JsonSubTypes.Type(value = FeedbackSessionAuditLogDetails.class, name = "FEEDBACK_SESSION_AUDIT"),
        @JsonSubTypes.Type(value = DefaultLogDetails.class, name = "DEFAULT_LOG")
})
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
