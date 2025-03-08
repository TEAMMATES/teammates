package teammates.storage.entity;

import java.time.Instant;

import com.googlecode.objectify.annotation.AlsoLoad;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Translate;

/**
 * Represents an individual student's extended deadline.
 */
@Entity
@Index
public class DeadlineExtension extends BaseEntity {

    @Id
    private String id;

    private String courseId;

    private String feedbackSessionName;

    private String userEmail;

    private boolean isInstructor;

    @AlsoLoad("sentClosingEmail")
    private boolean sentClosingSoonEmail;

    @Translate(InstantTranslatorFactory.class)
    private Instant endTime;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    @Translate(InstantTranslatorFactory.class)
    private Instant updatedAt;

    @SuppressWarnings("unused")
    private DeadlineExtension() {
        // required by Objectify
    }

    public DeadlineExtension(String courseId, String feedbackSessionName, String userEmail, boolean isInstructor,
            boolean sentClosingSoonEmail, Instant endTime) {
        this.setCourseId(courseId);
        this.setFeedbackSessionName(feedbackSessionName);
        this.setUserEmail(userEmail);
        this.setIsInstructor(isInstructor);
        this.setSentClosingSoonEmail(sentClosingSoonEmail);
        this.setEndTime(endTime);
        this.setId(generateId(this.courseId, this.feedbackSessionName, this.userEmail, this.isInstructor));
        this.setCreatedAt(Instant.now());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean getIsInstructor() {
        return isInstructor;
    }

    public void setIsInstructor(boolean isInstructor) {
        this.isInstructor = isInstructor;
    }

    public boolean getSentClosingSoonEmail() {
        return sentClosingSoonEmail;
    }

    public void setSentClosingSoonEmail(boolean sentClosingSoonEmail) {
        this.sentClosingSoonEmail = sentClosingSoonEmail;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Updates the updatedAt timestamp when saving.
     */
    @OnSave
    public void updateLastUpdatedTimestamp() {
        setUpdatedAt(Instant.now());
    }

    /**
     * Generates a unique ID for the deadline.
     */
    public static String generateId(String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
        // Format: courseId%feedbackSessionName%userEmail%isInstructor
        return courseId + '%' + feedbackSessionName + '%' + userEmail + '%' + isInstructor;
    }
}
