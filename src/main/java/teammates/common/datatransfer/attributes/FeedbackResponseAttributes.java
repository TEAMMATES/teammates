package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.FeedbackResponse;

public class FeedbackResponseAttributes extends EntityAttributes<FeedbackResponse> {
    public String feedbackSessionName;
    public String courseId;
    public String feedbackQuestionId;
    public FeedbackQuestionType feedbackQuestionType;
    /**
    * Depending on the question giver type, {@code giver} may contain the giver's email, the team name,
    * "anonymous", etc.
    */
    public String giver;
    /**
     * Depending on the question recipient type, {@code recipient} may contain the recipient's email, the team
     * name, "%GENERAL%", etc.
     */
    public String recipient;

    /** Contains the JSON formatted string that holds the information of the response details <br>
     * Don't use directly unless for storing/loading from data store <br>
     * To get the answer text use {@code getResponseDetails().getAnswerString()}
     *
     * <p>This is set to null to represent a missing response.
     */
    public Text responseMetaData;
    public String giverSection;
    public String recipientSection;
    protected transient Instant createdAt;
    protected transient Instant updatedAt;
    private String feedbackResponseId;

    public FeedbackResponseAttributes() {
        // attributes to be set after construction
    }

    public FeedbackResponseAttributes(String feedbackSessionName,
            String courseId, String feedbackQuestionId,
            FeedbackQuestionType feedbackQuestionType, String giver, String giverSection,
            String recipient, String recipientSection, Text responseMetaData) {
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;
        this.feedbackQuestionId = feedbackQuestionId;
        this.feedbackQuestionType = feedbackQuestionType;
        this.giver = giver;
        this.giverSection = giverSection;
        this.recipient = recipient;
        this.recipientSection = recipientSection;
        this.responseMetaData = responseMetaData;
    }

    public FeedbackResponseAttributes(FeedbackResponse fr) {
        this.feedbackResponseId = fr.getId();
        this.feedbackSessionName = fr.getFeedbackSessionName();
        this.courseId = fr.getCourseId();
        this.feedbackQuestionId = fr.getFeedbackQuestionId();
        this.feedbackQuestionType = fr.getFeedbackQuestionType();
        this.giver = fr.getGiverEmail();
        this.giverSection = fr.getGiverSection() == null ? Const.DEFAULT_SECTION : fr.getGiverSection();
        this.recipient = fr.getRecipientEmail();
        this.recipientSection = fr.getRecipientSection() == null ? Const.DEFAULT_SECTION : fr.getRecipientSection();
        this.responseMetaData = fr.getResponseMetaData();
        this.createdAt = fr.getCreatedAt();
        this.updatedAt = fr.getUpdatedAt();
    }

    public FeedbackResponseAttributes(FeedbackResponseAttributes copy) {
        this.feedbackResponseId = copy.getId();
        this.feedbackSessionName = copy.feedbackSessionName;
        this.courseId = copy.courseId;
        this.feedbackQuestionId = copy.feedbackQuestionId;
        this.feedbackQuestionType = copy.feedbackQuestionType;
        this.giver = copy.giver;
        this.giverSection = copy.giverSection;
        this.recipient = copy.recipient;
        this.recipientSection = copy.recipientSection;
        this.responseMetaData = copy.responseMetaData;
        this.createdAt = copy.createdAt;
        this.updatedAt = copy.updatedAt;
    }

    public String getId() {
        return feedbackResponseId;
    }

    public void setId(String feedbackResponseId) {
        this.feedbackResponseId = feedbackResponseId;
    }

    public Instant getCreatedAt() {
        return createdAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : updatedAt;
    }

    @Override
    public List<String> getInvalidityInfo() {

        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<>();

        addNonEmptyError(validator.getInvalidityInfoForFeedbackSessionName(feedbackSessionName), errors);

        addNonEmptyError(validator.getInvalidityInfoForCourseId(courseId), errors);

        return errors;
    }

    @Override
    public boolean isValid() {
        return getInvalidityInfo().isEmpty();
    }

    @Override
    public FeedbackResponse toEntity() {
        return new FeedbackResponse(feedbackSessionName, courseId,
                feedbackQuestionId, feedbackQuestionType,
                giver, giverSection, recipient, recipientSection, responseMetaData);
    }

    @Override
    public String getIdentificationString() {
        return feedbackQuestionId + "/" + giver + ":" + recipient;
    }

    @Override
    public String getEntityTypeAsString() {
        return "Feedback Response";
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId;
    }

    @Override
    public String toString() {
        return "FeedbackResponseAttributes [feedbackSessionName="
                + feedbackSessionName + ", courseId=" + courseId
                + ", feedbackQuestionId=" + feedbackQuestionId
                + ", feedbackQuestionType=" + feedbackQuestionType
                + ", giver=" + giver + ", recipient=" + recipient
                + ", answer=" + responseMetaData + "]";
    }

    @Override
    public String getJsonString() {
        return JsonUtils.toJson(this, FeedbackResponseAttributes.class);
    }

    @Override
    public void sanitizeForSaving() {
        // nothing to sanitize before saving
    }

    /**
     * Converts the given Feedback*ResponseDetails object to JSON for storing.
     */
    public void setResponseDetails(FeedbackResponseDetails responseDetails) {
        if (responseDetails == null) {
            // There was error extracting response data from http request
            responseMetaData = new Text("");
        } else if (responseDetails.questionType == FeedbackQuestionType.TEXT) {
            // For Text questions, the answer simply contains the response text, not a JSON
            // This is due to legacy data in the data store before there were multiple question types
            responseMetaData = new Text(responseDetails.getAnswerString());
        } else {
            responseMetaData = new Text(JsonUtils.toJson(responseDetails, getFeedbackResponseDetailsClass()));
        }
    }

    /**
     * Retrieves the Feedback*ResponseDetails object for this response.
     * @return The Feedback*ResponseDetails object representing the response's details
     */
    public FeedbackResponseDetails getResponseDetails() {

        if (isMissingResponse()) {
            return null;
        }

        Class<? extends FeedbackResponseDetails> responseDetailsClass = getFeedbackResponseDetailsClass();

        if (responseDetailsClass == FeedbackTextResponseDetails.class) {
            // For Text questions, the questionText simply contains the question, not a JSON
            // This is due to legacy data in the data store before there are multiple question types
            return new FeedbackTextResponseDetails(responseMetaData.getValue());
        }
        return JsonUtils.fromJson(responseMetaData.getValue(), responseDetailsClass);
    }

    /** This method gets the appropriate class type for the Feedback*ResponseDetails object
     * for this response.
     * @return The Feedback*ResponseDetails class type appropriate for this response.
     */
    private Class<? extends FeedbackResponseDetails> getFeedbackResponseDetailsClass() {
        return feedbackQuestionType.getResponseDetailsClass();
    }

    /**
     * Checks if this object represents a missing response.
     * A missing response should never be written to the database.
     * It should only be used as a representation.
     */
    public boolean isMissingResponse() {
        return responseMetaData == null;
    }

    public static void sortFeedbackResponses(List<FeedbackResponseAttributes> frs) {
        frs.sort(Comparator.comparing(FeedbackResponseAttributes::getId));
    }

}
