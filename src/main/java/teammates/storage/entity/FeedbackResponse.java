package teammates.storage.entity;

import java.time.Instant;
import java.util.Date;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;

import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;

/**
 * Represents a feedback response.
 */
@Entity
@Index
public class FeedbackResponse extends BaseEntity {

    /**
     * Setting this to true prevents changes to the lastUpdate time stamp. Set
     * to true when using scripts to update entities when you want to preserve
     * the lastUpdate time stamp.
     **/
    @Ignore
    public boolean keepUpdateTimestamp;

    // Format is feedbackQuestionId%giverEmail%receiver
    // i.e. if response is feedback for team: qnId%giver@gmail.com%Team1
    //         if response is feedback for person: qnId%giver@gmail.com%reciever@email.com
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

    private Text answer; //TODO: rename to responseMetaData, will require database conversion

    private Date createdAt;

    private Date updatedAt;

    @SuppressWarnings("unused")
    private FeedbackResponse() {
        // required by Objectify
    }

    public FeedbackResponse(String feedbackSessionName, String courseId,
            String feedbackQuestionId, FeedbackQuestionType feedbackQuestionType,
            String giverEmail, String giverSection, String recipient, String recipientSection, Text answer) {
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;
        this.feedbackQuestionId = feedbackQuestionId;
        this.feedbackQuestionType = feedbackQuestionType;
        this.giverEmail = giverEmail;
        this.giverSection = giverSection;
        this.receiver = recipient;
        this.receiverSection = recipientSection;
        this.answer = answer;

        this.feedbackResponseId = feedbackQuestionId + "%" + giverEmail + "%" + receiver;

        this.setCreatedAt(Instant.now());
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

    public Text getResponseMetaData() {
        return answer;
    }

    public void setAnswer(Text answer) {
        this.answer = answer;
    }

    public Instant getCreatedAt() {
        return createdAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : TimeHelper.convertDateToInstant(createdAt);
    }

    public Instant getUpdatedAt() {
        return updatedAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : TimeHelper.convertDateToInstant(updatedAt);
    }

    public void setCreatedAt(Instant newDate) {
        this.createdAt = TimeHelper.convertInstantToDate(newDate);
        setLastUpdate(newDate);
    }

    public void setLastUpdate(Instant newDate) {
        if (!keepUpdateTimestamp) {
            this.updatedAt = TimeHelper.convertInstantToDate(newDate);
        }
    }

    @OnSave
    public void updateLastUpdateTimestamp() {
        this.setLastUpdate(Instant.now());
    }
}
