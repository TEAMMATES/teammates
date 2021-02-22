package teammates.ui.output;

import java.util.List;

/**
 * The response log of a single feedback session.
 */
public class FeedbackResponseLog {
    private final FeedbackSessionData feedbackSessionData;

    private final List<StudentResponseAccessLog> studentResponseAccessLogs;

    public FeedbackResponseLog(FeedbackSessionData feedbackSessionData,
                               List<StudentResponseAccessLog> studentResponseAccessLogs) {
        this.feedbackSessionData = feedbackSessionData;
        this.studentResponseAccessLogs = studentResponseAccessLogs;
    }

    public FeedbackSessionData getFeedbackSessionData() {
        return feedbackSessionData;
    }

    public List<StudentResponseAccessLog> getStudentResponseAccessLogs() {
        return studentResponseAccessLogs;
    }
}
