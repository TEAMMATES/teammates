package teammates.ui.output;

/**
 * The submission status of a feedback session.
 */
public enum FeedbackSessionSubmissionStatus {

    /**
     * Feedback session is not visible.
     */
    NOT_VISIBLE,

    /**
     * Feedback session is visible to view but not yet open for submission.
     */
    VISIBLE_NOT_OPEN,

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
