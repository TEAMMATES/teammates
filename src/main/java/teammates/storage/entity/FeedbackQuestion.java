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

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Const;

/**
 * Represents a feedback question.
 */
@PersistenceCapable
public class FeedbackQuestion extends Entity implements StoreCallback {

    // TODO: where applicable, we should specify fields as "gae.unindexed" to prevent GAE from building unnecessary indexes.

    /**
     * The name of the primary key of this entity type.
     */
    @NotPersistent
    public static final String PRIMARY_KEY_NAME = getFieldWithPrimaryKeyAnnotation(FeedbackQuestion.class);

    /**
     * Setting this to true prevents changes to the lastUpdate time stamp. Set
     * to true when using scripts to update entities when you want to preserve
     * the lastUpdate time stamp.
     **/
    @NotPersistent
    public boolean keepUpdateTimestamp;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
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

    private Text questionDescription;

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

    @Persistent
    private List<FeedbackPath> feedbackPaths;

    @Persistent
    private Date createdAt;

    @Persistent
    private Date updatedAt;

    public FeedbackQuestion(
            String feedbackSessionName, String courseId, String creatorEmail,
            Text questionText, Text questionDescription, int questionNumber, FeedbackQuestionType questionType,
            FeedbackParticipantType giverType,
            FeedbackParticipantType recipientType,
            int numberOfEntitiesToGiveFeedbackTo,
            List<FeedbackParticipantType> showResponsesTo,
            List<FeedbackParticipantType> showGiverNameTo,
            List<FeedbackParticipantType> showRecipientNameTo,
            List<FeedbackPath> feedbackPaths) {

        this.feedbackQuestionId = null; // Allow GAE to generate key.
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;
        this.creatorEmail = creatorEmail;
        this.questionText = questionText;
        this.questionDescription = questionDescription;
        this.questionNumber = questionNumber;
        this.questionType = questionType;
        this.giverType = giverType;
        this.recipientType = recipientType;
        this.numberOfEntitiesToGiveFeedbackTo = numberOfEntitiesToGiveFeedbackTo;
        this.showResponsesTo = showResponsesTo;
        this.showGiverNameTo = showGiverNameTo;
        this.showRecipientNameTo = showRecipientNameTo;
        this.feedbackPaths = feedbackPaths;
        this.setCreatedAt(new Date());
    }

    public Date getCreatedAt() {
        return createdAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : updatedAt;
    }

    public void setCreatedAt(Date newDate) {
        this.createdAt = newDate;
        setLastUpdate(newDate);
    }

    public void setLastUpdate(Date newDate) {
        if (!keepUpdateTimestamp) {
            this.updatedAt = newDate;
        }
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

    public Text getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(Text questionDescription) {
        this.questionDescription = questionDescription;
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

    public void setFeedbackPaths(List<FeedbackPath> feedbackPaths) {
        this.feedbackPaths = feedbackPaths;
    }

    public List<FeedbackPath> getFeedbackPaths() {
        return feedbackPaths;
    }

    /**
     * Called by jdo before storing takes place.
     */
    @Override
    public void jdoPreStore() {
        this.setLastUpdate(new Date());
    }
}
