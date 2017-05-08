package teammates.storage.entity;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Represents the response giver to recipient path of a feedback question with custom feedback paths.
 */
@PersistenceCapable
public class FeedbackPath {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    private String feedbackPathId;

    @Persistent
    private String courseId;

    @Persistent
    private String giver;

    @Persistent
    private String recipient;

    public FeedbackPath(String courseId, String giver, String recipient) {
        this.courseId = courseId;
        this.giver = giver;
        this.recipient = recipient;
    }

    public String getFeedbackPathId() {
        return feedbackPathId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getGiver() {
        return giver;
    }

    public void setGiver(String giver) {
        this.giver = giver;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecpient(String recipient) {
        this.recipient = recipient;
    }
}
