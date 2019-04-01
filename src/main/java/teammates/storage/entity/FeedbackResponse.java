package teammates.storage.entity;

import java.time.Instant;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Translate;
import com.googlecode.objectify.annotation.Unindex;

import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Const;

/**
 * Represents a feedback response.
 */
@Entity
@Index
public class FeedbackResponse extends BaseEntity {

    /**
     * The unique id of the entity.
     *
     * @see #generateId(String, String, String)
     */
    @Id
    private String feedbackResponseId;

    private String feedbackSessionName;

    private String courseId;

    private String feedbackQuestionId;

    private FeedbackQuestionType feedbackQuestionType;

    private String giverEmail;

    private String giverSection;

    private String receiver;

    private String receiverSection;

    /**
     * Serialized {@link teammates.common.datatransfer.questions.FeedbackResponseDetails} stored as a string.
     *
     * @see teammates.common.datatransfer.attributes.FeedbackResponseAttributes#getResponseDetails()
     */
    @Unindex
    private Text answer;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    @Translate(InstantTranslatorFactory.class)
    private Instant updatedAt;

    @SuppressWarnings("unused")
    private FeedbackResponse() {
        // required by Objectify
    }

    public FeedbackResponse(String feedbackSessionName, String courseId,
            String feedbackQuestionId, FeedbackQuestionType feedbackQuestionType,
            String giverEmail, String giverSection, String recipient, String recipientSection, String answer) {
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;
        this.feedbackQuestionId = feedbackQuestionId;
        this.feedbackQuestionType = feedbackQuestionType;
        this.giverEmail = giverEmail;
        this.giverSection = giverSection;
        this.receiver = recipient;
        this.receiverSection = recipientSection;
        setAnswer(answer);

        this.feedbackResponseId = generateId(feedbackQuestionId, giverEmail, receiver);

        this.setCreatedAt(Instant.now());
    }

    /**
     * Generates an unique ID for the feedback response.
     */
    public static String generateId(String feedbackQuestionId, String giver, String receiver) {
        // Format is feedbackQuestionId%giverEmail%receiver
        // i.e. if response is feedback for team: qnId%giver@gmail.com%Team1
        //         if response is feedback for person: qnId%giver@gmail.com%reciever@email.com
        return feedbackQuestionId + '%' + giver + '%' + receiver;
    }

    public String getId() {
        return feedbackResponseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getFeedbackQuestionId() {
        return feedbackQuestionId;
    }

    public void setFeedbackQuestionId(String feedbackQuestionId) {
        this.feedbackQuestionId = feedbackQuestionId;
    }

    public FeedbackQuestionType getFeedbackQuestionType() {
        return feedbackQuestionType;
    }

    public void setFeedbackQuestionType(FeedbackQuestionType feedbackQuestionType) {
        this.feedbackQuestionType = feedbackQuestionType;
    }

    public String getGiverEmail() {
        return giverEmail;
    }

    public void setGiverEmail(String giverEmail) {
        this.giverEmail = giverEmail;
    }

    public String getGiverSection() {
        return giverSection;
    }

    public void setGiverSection(String giverSection) {
        this.giverSection = giverSection;
    }

    public String getRecipientEmail() {
        return receiver;
    }

    public void setRecipientEmail(String receiverEmail) {
        this.receiver = receiverEmail;
    }

    public String getRecipientSection() {
        return receiverSection;
    }

    public void setRecipientSection(String recipientSection) {
        this.receiverSection = recipientSection;
    }

    public String getResponseMetaData() {
        return answer == null ? null : answer.getValue();
    }

    public void setAnswer(String answer) {
        this.answer = answer == null ? null : new Text(answer);
    }

    public Instant getCreatedAt() {
        return createdAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : updatedAt;
    }

    /**
     * Sets the createdAt timestamp of the response.
     */
    public void setCreatedAt(Instant newDate) {
        this.createdAt = newDate;
        setLastUpdate(newDate);
    }

    public void setLastUpdate(Instant newDate) {
        this.updatedAt = newDate;
    }

    /**
     * Updates the updatedAt timestamp when saving.
     */
    @OnSave
    public void updateLastUpdateTimestamp() {
        this.setLastUpdate(Instant.now());
    }
}
