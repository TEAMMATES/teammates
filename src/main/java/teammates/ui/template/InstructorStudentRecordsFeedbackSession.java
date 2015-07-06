package teammates.ui.template;

import teammates.common.util.Sanitizer;

public class InstructorStudentRecordsFeedbackSession {
    
    private String courseId;
    private String studentEmail;
    private String googleId;
    private String feedbackSessionName;
    private String sanitizedFsName;
    
    public InstructorStudentRecordsFeedbackSession(String courseId, String studentEmail, String googleId,
                                                   String feedbackSessionName) {
        this.courseId = courseId;
        this.studentEmail = studentEmail;
        this.googleId = googleId;
        this.feedbackSessionName = feedbackSessionName;
        this.sanitizedFsName = Sanitizer.sanitizeForHtml(feedbackSessionName);
    }

    public String getCourseId() {
        return courseId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getSanitizedFsName() {
        return sanitizedFsName;
    }

}
