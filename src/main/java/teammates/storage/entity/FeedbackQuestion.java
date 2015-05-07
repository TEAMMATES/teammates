package teammates.storage.entity;

import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionType;

@PersistenceCapable
public class FeedbackQuestion {
    // TODO: where applicable, we should specify fields as "gae.unindexed" to prevent GAE from building unnecessary indexes. 
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private transient String feedbackQuestionId;
        
    @Persistent
    private String feedbackSessionName;
    
    @Persistent
    private String courseId;
    
    // TODO: Do we need this field since creator of FS = creator of qn? (can be removed -damith)
    @Persistent
    private String creatorEmail;
    
    // TODO: rename to questionMetaData, will require database conversion
    private Text questionText;
    
    @Persistent
    private int questionNumber;
    
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

    public FeedbackQuestion(
            String feedbackSessionName, String courseId, String creatorEmail,
            Text questionText, int questionNumber, FeedbackQuestionType questionType,
            FeedbackParticipantType giverType,
            FeedbackParticipantType recipientType,
            int numberOfEntitiesToGiveFeedbackTo,
            List<FeedbackParticipantType> showResponsesTo,
            List<FeedbackParticipantType> showGiverNameTo,
            List<FeedbackParticipantType> showRecipientNameTo) {
        
        this.feedbackQuestionId = null; // Allow GAE to generate key.
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;
        this.creatorEmail = creatorEmail;
        this.questionText = questionText;
        this.questionNumber = questionNumber;
        this.questionType = questionType;
        this.giverType = giverType;
        this.recipientType = recipientType;
        this.numberOfEntitiesToGiveFeedbackTo = numberOfEntitiesToGiveFeedbackTo;
        this.showResponsesTo = showResponsesTo;
        this.showGiverNameTo = showGiverNameTo;
        this.showRecipientNameTo = showRecipientNameTo;
    }
    
    public String getId() {
        return feedbackQuestionId;
    }

    /* Auto generated. Don't set this.
    public void setFeedbackQuestionId(String feedbackQuestionId) {
        this.feedbackQuestionId = feedbackQuestionId;
    }*/

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

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public Text getQuestionMetaData() {
        return questionText;
    }

    public void setQuestionText(Text questionText) {
        this.questionText = questionText;
    }

    public FeedbackQuestionType getQuestionType() {
        return questionType;
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
}
