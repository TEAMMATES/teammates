package teammates.common.datatransfer.logs;

import jakarta.annotation.Nullable;

/**
 * Contains specific structure and processing logic for feedback session audit log.
 */
public class FeedbackSessionAuditLogDetails extends LogDetails {

    @Nullable
    private String courseId;
    @Nullable
    private String feedbackSessionId;
    @Nullable
    private String feedbackSessionName;
    @Nullable
    private String studentId;
    @Nullable
    private String studentEmail;
    private String accessType;

    public FeedbackSessionAuditLogDetails() {
        super(LogEvent.FEEDBACK_SESSION_AUDIT);
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getFeedbackSessionId() {
        return feedbackSessionId;
    }

    public void setFeedbackSessionId(String feedbackSessionId) {
        this.feedbackSessionId = feedbackSessionId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @Override
    public void hideSensitiveInformation() {
        courseId = null;
        feedbackSessionName = null;
        studentEmail = null;
        studentId = null;
        feedbackSessionId = null;
    }

}
