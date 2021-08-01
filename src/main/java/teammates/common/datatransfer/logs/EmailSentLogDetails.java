package teammates.common.datatransfer.logs;

/**
 * Contains specific structure and processing logic for email sent log.
 */
public class EmailSentLogDetails extends LogDetails {

    public EmailSentLogDetails() {
        super(LogEvent.EMAIL_SENT);
    }

    @Override
    public void hideSensitiveInformation() {
        // TODO
    }

}
