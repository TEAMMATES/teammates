package teammates.ui.output;

import java.time.Instant;

import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.Instructor;

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

    public DeadlineExtensionData(String courseId, String feedbackSessionName,
            String userEmail, boolean isInstructor, boolean sentClosingEmail, Instant endTime) {
        this.courseId = courseId;
        this.feedbackSessionName = feedbackSessionName;
        this.userEmail = userEmail;
        this.isInstructor = isInstructor;
        this.sentClosingEmail = sentClosingEmail;
        this.endTime = endTime.toEpochMilli();
    }

    public DeadlineExtensionData(DeadlineExtensionAttributes deadlineExtension) {
        this.courseId = deadlineExtension.getCourseId();
        this.feedbackSessionName = deadlineExtension.getFeedbackSessionName();
        this.userEmail = deadlineExtension.getUserEmail();
        this.isInstructor = deadlineExtension.getIsInstructor();
        this.sentClosingEmail = deadlineExtension.getSentClosingEmail();
        this.endTime = deadlineExtension.getEndTime().toEpochMilli();
    }

    public DeadlineExtensionData(DeadlineExtension deadlineExtension) {
        this.courseId = deadlineExtension.getFeedbackSession().getCourse().getId();
        this.feedbackSessionName = deadlineExtension.getFeedbackSession().getName();
        this.userEmail = deadlineExtension.getUser().getEmail();
        this.isInstructor = deadlineExtension.getUser() instanceof Instructor;
        this.sentClosingEmail = deadlineExtension.isClosingSoonEmailSent();
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
