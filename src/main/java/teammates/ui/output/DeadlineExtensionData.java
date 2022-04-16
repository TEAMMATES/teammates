package teammates.ui.output;

import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;

/**
 * Output format of deadline extension data.
 */
public class DeadlineExtensionData extends ApiOutput {

    private final String courseId;
    private final String feedbackSessionName;
    private final String userEmail;
    private final boolean isInstructor;
    private final boolean sentClosingEmail;
    private final long endTime;

    public DeadlineExtensionData(DeadlineExtensionAttributes deadlineExtension) {
        this.courseId = deadlineExtension.getCourseId();
        this.feedbackSessionName = deadlineExtension.getFeedbackSessionName();
        this.userEmail = deadlineExtension.getUserEmail();
        this.isInstructor = deadlineExtension.getIsInstructor();
        this.sentClosingEmail = deadlineExtension.getSentClosingEmail();
        this.endTime = deadlineExtension.getEndTime().toEpochMilli();
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public boolean getIsInstructor() {
        return isInstructor;
    }

    public boolean getSentClosingEmail() {
        return sentClosingEmail;
    }

    public long getEndTime() {
        return endTime;
    }

}
