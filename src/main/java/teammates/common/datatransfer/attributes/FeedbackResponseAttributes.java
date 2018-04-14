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

    /**
     * Empty constructor for {@code Builder} to construct the object and for
     * {@code FeedbackResponseAttributesWithModifiableTimestamp} class that
     * inherits from this class to construct.
     */
    protected FeedbackResponseAttributes() {}

    public static Builder builder() {
        return new Builder();
    }

    public static FeedbackResponseAttributes valueOf(FeedbackResponse fr) {
        return builder()
                .withFeedbackResponseId(fr.getId())
                .withFeedbackSessionName(fr.getFeedbackSessionName())
                .withCourseId(fr.getCourseId())
                .withFeedbackQuestionId(fr.getFeedbackQuestionId())
                .withFeedbackQuestionType(fr.getFeedbackQuestionType())
                .withGiver(fr.getGiverEmail())
                .withGiverSection(fr.getGiverSection() == null ? Const.DEFAULT_SECTION : fr.getGiverSection())
                .withRecipient(fr.getRecipientEmail())
                .withRecipientSection(fr.getRecipientSection() == null ? Const.DEFAULT_SECTION : fr.getRecipientSection())
                .withCreatedAt(fr.getCreatedAt())
                .withUpdatedAt(fr.getUpdatedAt())
                .withResponseMetaData(fr.getResponseMetaData())
                .build();
    }

    public FeedbackResponseAttributes getCopy() {
        return builder()
                .withFeedbackResponseId(feedbackResponseId)
                .withFeedbackSessionName(feedbackSessionName)
                .withCourseId(courseId)
                .withFeedbackQuestionId(feedbackQuestionId)
                .withFeedbackQuestionType(feedbackQuestionType)
                .withGiver(giver)
                .withGiverSection(giverSection)
                .withRecipient(recipient)
                .withRecipientSection(recipientSection)
                .withResponseMetaData(responseMetaData)
                .withCreatedAt(createdAt)
                .withUpdatedAt(updatedAt)
                .build();
    }

    /**
     * A Builder class for {@link FeedbackResponseAttributes}.
     */
    public static class Builder {
        private final FeedbackResponseAttributes feedbackResponseAttributes;

        public Builder() {
            feedbackResponseAttributes = new FeedbackResponseAttributes();
        }

        public Builder withFeedbackResponseId(String feedbackResponseId) {
            feedbackResponseAttributes.setId(feedbackResponseId);
            return this;
        }

        public Builder withFeedbackSessionName(String feedbackSessionName) {
            feedbackResponseAttributes.feedbackSessionName = feedbackSessionName;
            return this;
        }

        public Builder withCourseId(String courseId) {
            feedbackResponseAttributes.courseId = courseId;
            return this;
        }

        public Builder withFeedbackQuestionId(String feedbackQuestionId) {
            feedbackResponseAttributes.feedbackQuestionId = feedbackQuestionId;
            return this;
        }

        public Builder withFeedbackQuestionType(FeedbackQuestionType feedbackQuestionType) {
            feedbackResponseAttributes.feedbackQuestionType = feedbackQuestionType;
            return this;
        }

        public Builder withGiver(String giver) {
            feedbackResponseAttributes.giver = giver;
            return this;
        }

        public Builder withGiverSection(String giverSection) {
            feedbackResponseAttributes.giverSection = giverSection;
            return this;
        }

        public Builder withRecipient(String recipient) {
            feedbackResponseAttributes.recipient = recipient;
            return this;
        }

        public Builder withRecipientSection(String recipientSection) {
            feedbackResponseAttributes.recipientSection = recipientSection;
            return this;
        }

        public Builder withResponseMetaData(Text responseMetaData) {
            feedbackResponseAttributes.responseMetaData = responseMetaData;
            return this;
        }

        public Builder withReponseMetaDataFromFeedbackResponseDetails(FeedbackResponseDetails responseDetails) {
            feedbackResponseAttributes.setResponseDetails(responseDetails);
            return this;
        }

        public Builder withCreatedAt(Instant createdAt) {
            feedbackResponseAttributes.createdAt = createdAt;
            return this;
        }

        public Builder withUpdatedAt(Instant updatedAt) {
            feedbackResponseAttributes.updatedAt = updatedAt;
            return this;
        }

        public FeedbackResponseAttributes build() {
            return feedbackResponseAttributes;
        }
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
