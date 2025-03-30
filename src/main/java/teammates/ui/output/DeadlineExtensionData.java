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
    private final boolean sentClosingSoonEmail;
    private final long endTime;

    public DeadlineExtensionData(String courseId, String feedbackSessionName,
            String userEmail, boolean isInstructor, boolean sentClosingSoonEmail, Instant endTime) {
        this.courseId = courseId;
        this.feedbackSessionName = feedbackSessionName;
        this.userEmail = userEmail;
        this.isInstructor = isInstructor;
        this.sentClosingSoonEmail = sentClosingSoonEmail;
        this.endTime = endTime.toEpochMilli();
    }

    public DeadlineExtensionData(DeadlineExtensionAttributes deadlineExtension) {
        this.courseId = deadlineExtension.getCourseId();
        this.feedbackSessionName = deadlineExtension.getFeedbackSessionName();
        this.userEmail = deadlineExtension.getUserEmail();
        this.isInstructor = deadlineExtension.getIsInstructor();
        this.sentClosingSoonEmail = deadlineExtension.getSentClosingSoonEmail();
        this.endTime = deadlineExtension.getEndTime().toEpochMilli();
    }

    public DeadlineExtensionData(DeadlineExtension deadlineExtension) {
        this.courseId = deadlineExtension.getFeedbackSession().getCourse().getId();
        this.feedbackSessionName = deadlineExtension.getFeedbackSession().getName();
        this.userEmail = deadlineExtension.getUser().getEmail();
        this.isInstructor = deadlineExtension.getUser() instanceof Instructor;
        this.sentClosingSoonEmail = deadlineExtension.isClosingSoonEmailSent();
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

    public boolean getSentClosingSoonEmail() {
        return sentClosingSoonEmail;
    }

    public long getEndTime() {
        return endTime;
    }

}
