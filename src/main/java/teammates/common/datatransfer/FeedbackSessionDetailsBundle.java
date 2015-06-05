package teammates.common.datatransfer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
     * 
     * @param sessions
     */
    public static void sortFeedbackSessionsByCreationTime(List<FeedbackSessionDetailsBundle> sessions) {
        Collections.sort(sessions, new Comparator<FeedbackSessionDetailsBundle>() {
            public int compare(FeedbackSessionDetailsBundle fsd1, FeedbackSessionDetailsBundle fsd2) {
                FeedbackSessionAttributes session1 = fsd1.feedbackSession;
                FeedbackSessionAttributes session2 = fsd2.feedbackSession;
                int result = 0;
                if (result == 0) {
                    result = session1.courseId.compareTo(session2.courseId);
                }
                if (result == 0) {
                    result = session1.createdTime.after(session2.createdTime) ? 1
                            : (session1.createdTime.before(session2.createdTime) ? -1 : 0);
                }
                if (result == 0) {
                    result = session1.endTime.after(session2.endTime) ? 1
                            : (session1.endTime.before(session2.endTime) ? -1 : 0);
                }
                if (result == 0) {
                    result = session1.startTime.after(session2.startTime) ? 1
                            : (session1.startTime.before(session2.startTime) ? -1 : 0);
                }
                if (result == 0) {
                    result = session1.feedbackSessionName.compareTo(session2.feedbackSessionName);
                }
                return result;
            }
        });
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("course:" + feedbackSession.courseId + ", name:" + feedbackSession.feedbackSessionName
                + Const.EOL);
        sb.append("submitted/total: " + stats.submittedTotal + "/" + stats.expectedTotal);
        return sb.toString();
    }
}