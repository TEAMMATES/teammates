package teammates.common.datatransfer.logs;

/**
 * Contains specific structure and processing logic for feedback session audit log.
 */
public class FeedbackSessionAuditLogDetails extends LogDetails {

    public FeedbackSessionAuditLogDetails() {
        super(LogEvent.FEEDBACK_SESSION_AUDIT);
    }

    @Override
    public void hideSensitiveInformation() {
        // TODO
    }

}
