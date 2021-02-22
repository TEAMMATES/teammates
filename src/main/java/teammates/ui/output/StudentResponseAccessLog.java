package teammates.ui.output;

/**
 * The response access log of a student for a single feedback response session.
 */
public class StudentResponseAccessLog {
    private final StudentData studentData;

    private final FeedbackResponseAccessType feedbackResponseAccessType;

    private final long accessTimestamp;

    public StudentResponseAccessLog(StudentData studentData,
                                    FeedbackResponseAccessType feedbackResponseAccessType,
                                    long accessTimestamp) {
        this.studentData = studentData;
        this.feedbackResponseAccessType = feedbackResponseAccessType;
        this.accessTimestamp = accessTimestamp;
    }

    public StudentData getStudentData() {
        return studentData;
    }

    public FeedbackResponseAccessType getFeedbackResponseAccessType() {
        return feedbackResponseAccessType;
    }

    public long getAccessTimestamp() {
        return accessTimestamp;
    }
}
