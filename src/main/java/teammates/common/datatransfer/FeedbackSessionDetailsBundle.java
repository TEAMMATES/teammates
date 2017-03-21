package teammates.common.datatransfer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;

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

    /**
     * Sorts feedback session based courseID (ascending), then by create time (ascending), deadline
     * (ascending), then by start time (ascending), then by feedback session name
     * (ascending). The sort by CourseID part is to cater the case when this
     * method is called with combined feedback sessions from many courses
     */
    public static void sortFeedbackSessionsByCreationTime(List<FeedbackSessionDetailsBundle> sessions) {
        Collections.sort(sessions, new Comparator<FeedbackSessionDetailsBundle>() {
            @Override
            public int compare(FeedbackSessionDetailsBundle fsd1, FeedbackSessionDetailsBundle fsd2) {
                FeedbackSessionAttributes session1 = fsd1.feedbackSession;
                FeedbackSessionAttributes session2 = fsd2.feedbackSession;
                int result = 0;
                if (result == 0) {
                    result = session1.getCourseId().compareTo(session2.getCourseId());
                }
                if (result == 0) {
                    result = session1.getCreatedTime().after(session2.getCreatedTime()) ? 1
                            : session1.getCreatedTime().before(session2.getCreatedTime()) ? -1 : 0;
                }
                if (result == 0) {
                    result = session1.getEndTime().after(session2.getEndTime()) ? 1
                            : session1.getEndTime().before(session2.getEndTime()) ? -1 : 0;
                }
                if (result == 0) {
                    result = session1.getStartTime().after(session2.getStartTime()) ? 1
                            : session1.getStartTime().before(session2.getStartTime()) ? -1 : 0;
                }
                if (result == 0) {
                    result = session1.getFeedbackSessionName().compareTo(session2.getFeedbackSessionName());
                }
                return result;
            }
        });
    }

    @Override
    public String toString() {
        return "course:" + feedbackSession.getCourseId() + ", name:" + feedbackSession.getFeedbackSessionName() + Const.EOL
               + "submitted/total: " + stats.submittedTotal + "/" + stats.expectedTotal;
    }
}
