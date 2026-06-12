package teammates.ui.output;

import java.util.UUID;

/**
 * The API output format for a submission link under a feedback session.
 */
public class SessionSubmissionLinkData implements ApiOutput {

    private final UUID feedbackSessionId;
    private final String name;
    private final long submissionStartTimestamp;
    private final long submissionEndTimestamp;
    private final String timeZone;
    private final FeedbackSessionSubmissionStatus submissionStatus;
    private final String url;

    public SessionSubmissionLinkData(UUID feedbackSessionId, String name,
            long submissionStartTimestamp, long submissionEndTimestamp,
            String timeZone, FeedbackSessionSubmissionStatus submissionStatus, String url) {
        this.feedbackSessionId = feedbackSessionId;
        this.name = name;
        this.submissionStartTimestamp = submissionStartTimestamp;
        this.submissionEndTimestamp = submissionEndTimestamp;
        this.timeZone = timeZone;
        this.submissionStatus = submissionStatus;
        this.url = url;
    }

    public UUID getFeedbackSessionId() {
        return feedbackSessionId;
    }

    public String getName() {
        return name;
    }

    public long getSubmissionStartTimestamp() {
        return submissionStartTimestamp;
    }

    public long getSubmissionEndTimestamp() {
        return submissionEndTimestamp;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public FeedbackSessionSubmissionStatus getSubmissionStatus() {
        return submissionStatus;
    }

    public String getUrl() {
        return url;
    }
}
