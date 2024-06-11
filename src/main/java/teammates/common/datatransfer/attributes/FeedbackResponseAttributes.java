package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.FeedbackResponse;

/**
 * The data transfer object for {@link FeedbackResponse} entities.
 */
public final class FeedbackResponseAttributes extends EntityAttributes<FeedbackResponse> {

    private String feedbackQuestionId;
    /**
     * Depending on the question giver type, {@code giver} may contain the giver's email, the team name,
     * "anonymous", etc.
     */
    private String giver;
    /**
     * Depending on the question recipient type, {@code recipient} may contain the recipient's email, the team
     * name, "%GENERAL%", etc.
     */
    private String recipient;
    private String feedbackSessionName;
    private String courseId;
    private FeedbackResponseDetails responseDetails;
    private String giverSection;
    private String recipientSection;
    private transient Instant createdAt;
    private transient Instant updatedAt;
    private transient String feedbackResponseId;

    private FeedbackResponseAttributes(String feedbackQuestionId, String giver, String recipient) {
        this.feedbackQuestionId = feedbackQuestionId;
        this.giver = giver;
        this.recipient = recipient;

        this.giverSection = Const.DEFAULT_SECTION;
        this.recipientSection = Const.DEFAULT_SECTION;
        this.feedbackResponseId = FeedbackResponse.generateId(feedbackQuestionId, giver, recipient);
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
        this.responseDetails = copy.getResponseDetailsCopy();
    }

    /**
     * Gets the {@link FeedbackResponseAttributes} instance of the given {@link FeedbackResponse}.
     */
    public static FeedbackResponseAttributes valueOf(FeedbackResponse fr) {
        FeedbackResponseAttributes fra =
                new FeedbackResponseAttributes(
                        fr.getFeedbackQuestionId(), fr.getGiverEmail(), fr.getRecipientEmail());

        fra.feedbackResponseId = fr.getId();
        fra.feedbackSessionName = fr.getFeedbackSessionName();
        fra.courseId = fr.getCourseId();
        if (fr.getGiverSection() != null) {
            fra.giverSection = fr.getGiverSection();
        }
        if (fr.getRecipientSection() != null) {
            fra.recipientSection = fr.getRecipientSection();
        }
        fra.responseDetails = deserializeResponseFromSerializedString(fr.getAnswer(), fr.getFeedbackQuestionType());
        fra.createdAt = fr.getCreatedAt();
        fra.updatedAt = fr.getUpdatedAt();

        return fra;
    }

    public FeedbackQuestionType getFeedbackQuestionType() {
        return responseDetails.getQuestionType();
    }

    public String getId() {
        return feedbackResponseId;
    }

    public void setId(String feedbackResponseId) {
        this.feedbackResponseId = feedbackResponseId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
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

    public String getGiver() {
        return giver;
    }

    public void setGiver(String giver) {
        this.giver = giver;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getGiverSection() {
        return giverSection;
    }

    public String getRecipientSection() {
        return recipientSection;
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
    public String toString() {
        return "FeedbackResponseAttributes [feedbackSessionName="
                + feedbackSessionName + ", courseId=" + courseId
                + ", feedbackQuestionId=" + feedbackQuestionId
                + ", feedbackQuestionType=" + getFeedbackQuestionType()
                + ", giver=" + giver + ", recipient=" + recipient
                + ", answer=" + getSerializedFeedbackResponseDetail() + "]";
    }

    @Override
    public int hashCode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.feedbackSessionName).append(this.courseId)
                .append(this.feedbackQuestionId).append(this.giver).append(this.recipient);
        return stringBuilder.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackResponseAttributes otherFeedbackResponse = (FeedbackResponseAttributes) other;
            return Objects.equals(this.feedbackSessionName, otherFeedbackResponse.feedbackSessionName)
                    && Objects.equals(this.courseId, otherFeedbackResponse.courseId)
                    && Objects.equals(this.feedbackQuestionId, otherFeedbackResponse.feedbackQuestionId)
                    && Objects.equals(this.giver, otherFeedbackResponse.giver)
                    && Objects.equals(this.recipient, otherFeedbackResponse.recipient);
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        // nothing to sanitize before saving
    }

    public FeedbackResponseDetails getResponseDetails() {
        return responseDetails;
    }

    public void setResponseDetails(FeedbackResponseDetails newFeedbackResponseDetails) {
        responseDetails = newFeedbackResponseDetails.getDeepCopy();
    }

    public String getSerializedFeedbackResponseDetail() {
        return responseDetails.getJsonString();
    }

    public FeedbackResponseDetails getResponseDetailsCopy() {
        return responseDetails.getDeepCopy();
    }

    private static FeedbackResponseDetails deserializeResponseFromSerializedString(
            String serializedResponseDetails, FeedbackQuestionType questionType) {
        if (questionType == FeedbackQuestionType.TEXT) {
            // For Text questions, the answer simply contains the response text, not a JSON
            return new FeedbackTextResponseDetails(serializedResponseDetails);
        }
        return JsonUtils.fromJson(serializedResponseDetails, questionType.getResponseDetailsClass());
    }

    /**
     * Returns a builder for {@link FeedbackResponseAttributes}.
     */
    public static Builder builder(String feedbackQuestionId, String giver, String recipient) {
        return new Builder(feedbackQuestionId, giver, recipient);
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
     * A builder for {@link FeedbackResponseCommentAttributes}.
     */
    public static final class Builder extends BasicBuilder<FeedbackResponseAttributes, Builder> {

        private FeedbackResponseAttributes fra;

        private Builder(String feedbackQuestionId, String giver, String recipient) {
            super(new UpdateOptions(""));
            thisBuilder = this;

            assert feedbackQuestionId != null;
            assert giver != null;
            assert recipient != null;
            fra = new FeedbackResponseAttributes(feedbackQuestionId, giver, recipient);
        }

        public Builder withCourseId(String courseId) {
            assert courseId != null;
            fra.courseId = courseId;

            return this;
        }

        public Builder withFeedbackSessionName(String feedbackSessionName) {
            assert feedbackSessionName != null;
            fra.feedbackSessionName = feedbackSessionName;

            return this;
        }

        @Override
        public FeedbackResponseAttributes build() {
            fra.update(updateOptions);

            return fra;
        }
    }

    /**
     * Helper class to specific the fields to update in {@link FeedbackResponseAttributes}.
     */
    public static final class UpdateOptions {
        private String feedbackResponseId;

        private UpdateOption<String> giverOption = UpdateOption.empty();
        private UpdateOption<String> giverSectionOption = UpdateOption.empty();
        private UpdateOption<String> recipientOption = UpdateOption.empty();
        private UpdateOption<String> recipientSectionOption = UpdateOption.empty();
        private UpdateOption<FeedbackResponseDetails> responseDetailsUpdateOption = UpdateOption.empty();

        private UpdateOptions(String feedbackResponseId) {
            assert feedbackResponseId != null;

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
        public static final class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(String feedbackResponseId) {
                super(new UpdateOptions(feedbackResponseId));
                thisBuilder = this;
            }

            public Builder withGiver(String giver) {
                assert giver != null;

                updateOptions.giverOption = UpdateOption.of(giver);
                return thisBuilder;
            }

            public Builder withRecipient(String recipient) {
                assert recipient != null;

                updateOptions.recipientOption = UpdateOption.of(recipient);
                return thisBuilder;
            }

            public Builder withFeedbackResponseDetails(FeedbackResponseDetails responseDetails) {
                assert responseDetails != null;

                updateOptions.responseDetailsUpdateOption = UpdateOption.of(responseDetails);
                return thisBuilder;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

    /**
     * Basic builder to build {@link FeedbackResponseAttributes} related classes.
     *
     * @param <T> type to be built
     * @param <B> type of the builder
     */
    private abstract static class BasicBuilder<T, B extends BasicBuilder<T, B>> {

        UpdateOptions updateOptions;
        B thisBuilder;

        BasicBuilder(UpdateOptions updateOptions) {
            this.updateOptions = updateOptions;
        }

        public B withGiverSection(String giverSection) {
            assert giverSection != null;

            updateOptions.giverSectionOption = UpdateOption.of(giverSection);
            return thisBuilder;
        }

        public B withRecipientSection(String recipientSection) {
            assert recipientSection != null;

            updateOptions.recipientSectionOption = UpdateOption.of(recipientSection);
            return thisBuilder;
        }

        public B withResponseDetails(FeedbackResponseDetails responseDetails) {
            assert responseDetails != null;

            updateOptions.responseDetailsUpdateOption = UpdateOption.of(responseDetails.getDeepCopy());
            return thisBuilder;
        }

        public abstract T build();

    }

}
