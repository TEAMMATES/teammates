package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.FeedbackResponse;

public class FeedbackResponseAttributes extends EntityAttributes<FeedbackResponse> {

    private static final String FEEDBACK_RESPONSE_BACKUP_LOG_MSG = "Recently modified feedback response::";
    private static final String ATTRIBUTE_NAME = "Feedback Response";

    public String feedbackSessionName;
    public String courseId;
    public String feedbackQuestionId;
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
    public FeedbackResponseDetails responseDetails;
    public String giverSection;
    public String recipientSection;
    protected transient Instant createdAt;
    protected transient Instant updatedAt;
    private String feedbackResponseId;

    public FeedbackResponseAttributes() {
        // attributes to be set after construction
    }

    public FeedbackResponseAttributes(String feedbackSessionName,
            String courseId, String feedbackQuestionId, String giver, String giverSection,
            String recipient, String recipientSection, FeedbackResponseDetails responseDetails) {
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;
        this.feedbackQuestionId = feedbackQuestionId;
        this.giver = giver;
        this.giverSection = giverSection;
        this.recipient = recipient;
        this.recipientSection = recipientSection;
        this.responseDetails = responseDetails.getDeepCopy();
    }

    public FeedbackResponseAttributes(FeedbackResponse fr) {
        this.feedbackResponseId = fr.getId();
        this.feedbackSessionName = fr.getFeedbackSessionName();
        this.courseId = fr.getCourseId();
        this.feedbackQuestionId = fr.getFeedbackQuestionId();
        this.giver = fr.getGiverEmail();
        this.giverSection = fr.getGiverSection() == null ? Const.DEFAULT_SECTION : fr.getGiverSection();
        this.recipient = fr.getRecipientEmail();
        this.recipientSection = fr.getRecipientSection() == null ? Const.DEFAULT_SECTION : fr.getRecipientSection();
        this.responseDetails = deserializeResponseFromSerializedString(fr.getResponseMetaData(),
                                                                       fr.getFeedbackQuestionType());
        this.createdAt = fr.getCreatedAt();
        this.updatedAt = fr.getUpdatedAt();
    }

    public FeedbackResponseAttributes(FeedbackResponseAttributes copy) {
        this.feedbackResponseId = copy.getId();
        this.feedbackSessionName = copy.feedbackSessionName;
        this.courseId = copy.courseId;
        this.feedbackQuestionId = copy.feedbackQuestionId;
        this.giver = copy.giver;
        this.giverSection = copy.giverSection;
        this.recipient = copy.recipient;
        this.recipientSection = copy.recipientSection;
        this.createdAt = copy.createdAt;
        this.updatedAt = copy.updatedAt;
        this.responseDetails = copy.getResponseDetails();
    }

    public FeedbackQuestionType getFeedbackQuestionType() {
        return responseDetails.questionType;
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

        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForFeedbackSessionName(feedbackSessionName), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseId(courseId), errors);

        return errors;
    }

    @Override
    public boolean isValid() {
        return getInvalidityInfo().isEmpty();
    }

    @Override
    public FeedbackResponse toEntity() {
        return new FeedbackResponse(feedbackSessionName, courseId,
                feedbackQuestionId, getFeedbackQuestionType(),
                giver, giverSection, recipient, recipientSection, getSerializedFeedbackResponseDetail());
    }

    @Override
    public String getIdentificationString() {
        return feedbackQuestionId + "/" + giver + ":" + recipient;
    }

    @Override
    public String getEntityTypeAsString() {
        return ATTRIBUTE_NAME;
    }

    @Override
    public String getBackupIdentifier() {
        return FEEDBACK_RESPONSE_BACKUP_LOG_MSG + getId();
    }

    @Override
    public String toString() {
        return "FeedbackResponseAttributes [feedbackSessionName="
                + feedbackSessionName + ", courseId=" + courseId
                + ", feedbackQuestionId=" + feedbackQuestionId
                + ", feedbackQuestionType=" + getFeedbackQuestionType()
                + ", giver=" + giver + ", recipient=" + recipient
                + ", answer=" + getSerializedFeedbackResponseDetail() + "]";
    }

    @Override
    public String getJsonString() {
        return JsonUtils.toJson(this, FeedbackResponseAttributes.class);
    }

    @Override
    public void sanitizeForSaving() {
        // nothing to sanitize before saving
    }

    public String getSerializedFeedbackResponseDetail() {
        return responseDetails.getJsonString();
    }

    public FeedbackResponseDetails getResponseDetails() {
        return responseDetails.getDeepCopy();
    }

    public void setResponseDetails(FeedbackResponseDetails newFeedbackResponseDetails) {
        responseDetails = newFeedbackResponseDetails.getDeepCopy();
    }

    private FeedbackResponseDetails deserializeResponseFromSerializedString(String serializedResponseDetails,
                                                                            FeedbackQuestionType questionType) {
        if (questionType == FeedbackQuestionType.TEXT) {
            // For Text questions, the questionText simply contains the question, not a JSON
            // This is due to legacy data in the data store before there are multiple question types
            return new FeedbackTextResponseDetails(serializedResponseDetails);
        }
        return JsonUtils.fromJson(serializedResponseDetails, questionType.getResponseDetailsClass());
    }

    /**
     * Checks if this object represents a missing response.
     * A missing response should never be written to the database.
     * It should only be used as a representation.
     */
    public boolean isMissingResponse() {
        return responseDetails == null;
    }

    public static void sortFeedbackResponses(List<FeedbackResponseAttributes> frs) {
        frs.sort(Comparator.comparing(FeedbackResponseAttributes::getId));
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.giverOption.ifPresent(s -> giver = s);
        updateOptions.giverSectionOption.ifPresent(s -> giverSection = s);
        updateOptions.recipientOption.ifPresent(s -> recipient = s);
        updateOptions.recipientSectionOption.ifPresent(s -> recipientSection = s);
        updateOptions.responseDetailsUpdateOption.ifPresent(this::setResponseDetails);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a response.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String feedbackResponseId) {
        return new UpdateOptions.Builder(feedbackResponseId);
    }

    /**
     * Helper class to specific the fields to update in {@link FeedbackResponseAttributes}.
     */
    public static class UpdateOptions {
        private String feedbackResponseId;

        private UpdateOption<String> giverOption = UpdateOption.empty();
        private UpdateOption<String> giverSectionOption = UpdateOption.empty();
        private UpdateOption<String> recipientOption = UpdateOption.empty();
        private UpdateOption<String> recipientSectionOption = UpdateOption.empty();
        private UpdateOption<FeedbackResponseDetails> responseDetailsUpdateOption = UpdateOption.empty();

        private UpdateOptions(String feedbackResponseId) {
            Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, feedbackResponseId);

            this.feedbackResponseId = feedbackResponseId;
        }

        public String getFeedbackResponseId() {
            return feedbackResponseId;
        }

        @Override
        public String toString() {
            return "FeedbackResponseAttributes.UpdateOptions ["
                    + "feedbackResponseId = " + feedbackResponseId
                    + ", giver = " + giverOption
                    + ", giverSection = " + giverSectionOption
                    + ", recipient = " + recipientOption
                    + ", recipientSection = " + recipientSectionOption
                    + ", responseDetails = " + JsonUtils.toJson(responseDetailsUpdateOption)
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder {
            private UpdateOptions updateOptions;

            private Builder(String feedbackResponseId) {
                updateOptions = new UpdateOptions(feedbackResponseId);
            }

            public Builder withGiver(String giver) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, giver);

                updateOptions.giverOption = UpdateOption.of(giver);
                return this;
            }

            public Builder withGiverSection(String giverSection) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, giverSection);

                updateOptions.giverSectionOption = UpdateOption.of(giverSection);
                return this;
            }

            public Builder withRecipient(String recipient) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, recipient);

                updateOptions.recipientOption = UpdateOption.of(recipient);
                return this;
            }

            public Builder withRecipientSection(String recipientSection) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, recipientSection);

                updateOptions.recipientSectionOption = UpdateOption.of(recipientSection);
                return this;
            }

            public Builder withResponseDetails(FeedbackResponseDetails responseDetails) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, responseDetails);

                updateOptions.responseDetailsUpdateOption = UpdateOption.of(responseDetails);
                return this;
            }

            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

}
