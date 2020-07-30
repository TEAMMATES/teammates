package teammates.common.datatransfer;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

/**
 * Represents details of a feedback session
 * Contains:
 * <br> * The basic info of the feedback session (as a {@link FeedbackSessionAttributes} object).
 * <br> * Feedback response statistics (as a {@link FeedbackSessionStats} object).
 */
public class FeedbackSessionDetailsBundle {

    public FeedbackSessionStats stats;
    public FeedbackSessionAttributes feedbackSession;

    public FeedbackSessionDetailsBundle(FeedbackSessionAttributes feedbackSession) {
        this.feedbackSession = feedbackSession;
        this.stats = new FeedbackSessionStats();
    }

    @Override
    public String toString() {
        return "course:" + feedbackSession.getCourseId() + ", name:" + feedbackSession.getFeedbackSessionName()
                + System.lineSeparator() + "submitted/total: " + stats.submittedTotal + "/" + stats.expectedTotal;
    }
}
