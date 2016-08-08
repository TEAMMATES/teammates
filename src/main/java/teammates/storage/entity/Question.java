package teammates.storage.entity;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.listener.StoreCallback;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.util.Const;

import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class Question implements StoreCallback {

    /**
     * Setting this to true prevents changes to the lastUpdate time stamp. Set
     * to true when using scripts to update entities when you want to preserve
     * the lastUpdate time stamp.
     **/
    @NotPersistent
    public boolean keepUpdateTimestamp;
    
    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    private String encodedKey;
    
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.pk-name", value = "true")
    private String feedbackQuestionId;
        
    @Persistent
    private String feedbackSessionName;
    
    @Persistent
    private String courseId;
    
    // TODO: Consider removing creatorEmail since it's not necessary
    @Persistent
    private String creatorEmail;
    
    private Text questionMetaData;
    
    @Persistent
    private int questionNumber;
    
    private Text questionDescription;
    
    @Persistent
    private FeedbackQuestionType questionType;

    @Persistent
    private FeedbackParticipantType giverType;
    
    @Persistent
    private FeedbackParticipantType recipientType;
    
    // Check for consistency in questionLogic/questionAttributes.
    // (i.e. if type is own team, numberOfEntities must = 1).
    @Persistent
    private int numberOfEntitiesToGiveFeedbackTo;
    
    // We can actually query the list in JDOQL if needed.
    @Persistent
    private List<FeedbackParticipantType> showResponsesTo;
    
    @Persistent
    private List<FeedbackParticipantType> showGiverNameTo;
    
    @Persistent
    private List<FeedbackParticipantType> showRecipientNameTo;
    
    @Persistent
    private Date createdAt;
    
    @Persistent
    private Date updatedAt;
    
    public Question(
            String questionId,
            String feedbackSessionName, String courseId, String creatorEmail,
            Text questionText, Text questionDescription, int questionNumber,
            FeedbackQuestionType questionType,
            FeedbackParticipantType giverType,
            FeedbackParticipantType recipientType,
            int numberOfEntitiesToGiveFeedbackTo,
            List<FeedbackParticipantType> showResponsesTo,
            List<FeedbackParticipantType> showGiverNameTo,
            List<FeedbackParticipantType> showRecipientNameTo) {
        
        this.feedbackQuestionId = questionId;
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;
        this.creatorEmail = creatorEmail;
        this.questionMetaData = questionText;
        this.questionDescription = questionDescription;
        this.questionNumber = questionNumber;
        this.questionType = questionType;
        this.giverType = giverType;
        this.recipientType = recipientType;
        this.numberOfEntitiesToGiveFeedbackTo = numberOfEntitiesToGiveFeedbackTo;
        this.showResponsesTo = showResponsesTo;
        this.showGiverNameTo = showGiverNameTo;
        this.showRecipientNameTo = showRecipientNameTo;
        this.setCreatedAt(new Date());
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        
        if (other == null) {
            return false;
        }
        if (!(other instanceof Question)) {
            return false;
        }
        
        final Question question = (Question) other;
        return feedbackQuestionId.equals(question.feedbackQuestionId);
    }
    
    @Override
    public int hashCode() {
        return feedbackQuestionId.hashCode();
    }

    public Date getCreatedAt() {
        return (createdAt == null) ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : createdAt;
    }
    
    public Date getUpdatedAt() {
        return (updatedAt == null) ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : updatedAt;
    }
    
    public void setCreatedAt(Date newDate) {
        this.createdAt = newDate;
        setLastUpdate(newDate);
    }
    
    public String getCreatorEmail() {
        return creatorEmail;
    }
    
    public void setLastUpdate(Date newDate) {
        if (!keepUpdateTimestamp) {
            this.updatedAt = newDate;
        }
    }
    
    public String getId() {
        return feedbackQuestionId;
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

    public Text getQuestionMetaData() {
        return questionMetaData;
    }

    public void setQuestionText(Text questionText) {
        this.questionMetaData = questionText;
    }

    public FeedbackQuestionType getQuestionType() {
        return questionType;
    }
    
    public void setQuestionDescription(Text questionDescription) {
        this.questionDescription = questionDescription;
    }

    public Text getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionType(FeedbackQuestionType questionType) {
        this.questionType = questionType;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public FeedbackParticipantType getGiverType() {
        return giverType;
    }

    public void setGiverType(FeedbackParticipantType giverType) {
        this.giverType = giverType;
    }

    public FeedbackParticipantType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(FeedbackParticipantType recipientType) {
        this.recipientType = recipientType;
    }
    
    public int getNumberOfEntitiesToGiveFeedbackTo() {
        return numberOfEntitiesToGiveFeedbackTo;
    }

    public void setNumberOfEntitiesToGiveFeedbackTo(
            int numberOfEntitiesToGiveFeedbackTo) {
        this.numberOfEntitiesToGiveFeedbackTo = numberOfEntitiesToGiveFeedbackTo;
    }

    public List<FeedbackParticipantType> getShowResponsesTo() {
        return showResponsesTo;
    }

    public void setShowResponsesTo(List<FeedbackParticipantType> showResponsesTo) {
        this.showResponsesTo = showResponsesTo;
    }

    public List<FeedbackParticipantType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public void setShowGiverNameTo(List<FeedbackParticipantType> showGiverNameTo) {
        this.showGiverNameTo = showGiverNameTo;
    }

    public List<FeedbackParticipantType> getShowRecipientNameTo() {
        return showRecipientNameTo;
    }

    public void setShowRecipientNameTo(
            List<FeedbackParticipantType> showRecipientNameTo) {
        this.showRecipientNameTo = showRecipientNameTo;
    }
    
    /**
     * Called by jdo before storing takes place.
     */
    @Override
    public void jdoPreStore() {
        this.setLastUpdate(new Date());
    }
}
