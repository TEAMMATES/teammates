package teammates.common.datatransfer;

/**
 * The submission status of a feedback session.
 */
public enum FeedbackSessionSubmissionStatus {

    /**
     * Feedback session has not yet opened.
     */
    NOT_VISIBLE,

    /**
     * Feedback session is open for submission.
     */
    OPEN,

    /**
     * Feedback session is in grace period.
     */
    GRACE_PERIOD,

    /**
     * Feedback session is closed for submission.
     */
    CLOSED
}
