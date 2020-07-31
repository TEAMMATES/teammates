package teammates.common.datatransfer;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

/**
 * Represents details of a feedback session
 * Contains:
 * <br> * The basic info of the feedback session (as a {@link FeedbackSessionAttributes} object).
 * <br> * Feedback response statistics.
 */
public class FeedbackSessionDetailsBundle {

    public FeedbackSessionAttributes feedbackSession;
    public int submittedTotal;
    public int expectedTotal;

    public FeedbackSessionDetailsBundle(FeedbackSessionAttributes feedbackSession) {
        this.feedbackSession = feedbackSession;
    }

    @Override
    public String toString() {
        return "course:" + feedbackSession.getCourseId() + ", name:" + feedbackSession.getFeedbackSessionName()
                + System.lineSeparator() + "submitted/total: " + submittedTotal + "/" + expectedTotal;
    }
}
