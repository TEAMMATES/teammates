package teammates.common.datatransfer.questions;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import teammates.common.util.JsonUtils;

/**
 * A class holding the details for the response of a specific question type.
 *
 * <p>This abstract class is inherited by concrete Feedback*ResponseDetails
 * classes which provides the implementation for the various abstract methods
 * such that pages can render the correct information depending on the
 * question type.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "questionType",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FeedbackTextResponseDetails.class, name = "TEXT"),
        @JsonSubTypes.Type(value = FeedbackMcqResponseDetails.class, name = "MCQ"),
        @JsonSubTypes.Type(value = FeedbackMsqResponseDetails.class, name = "MSQ"),
        @JsonSubTypes.Type(value = FeedbackNumericalScaleResponseDetails.class, name = "NUMSCALE"),
        @JsonSubTypes.Type(
                value = FeedbackConstantSumResponseDetails.class,
                names = {"CONSTSUM", "CONSTSUM_OPTIONS", "CONSTSUM_RECIPIENTS"}),
        @JsonSubTypes.Type(value = FeedbackContributionResponseDetails.class, name = "CONTRIB"),
        @JsonSubTypes.Type(value = FeedbackRubricResponseDetails.class, name = "RUBRIC"),
        @JsonSubTypes.Type(value = FeedbackRankOptionsResponseDetails.class, name = "RANK_OPTIONS"),
        @JsonSubTypes.Type(value = FeedbackRankRecipientsResponseDetails.class, name = "RANK_RECIPIENTS")
})
@JsonPropertyOrder({ "questionType", "answer" })
public abstract class FeedbackResponseDetails {
    private FeedbackQuestionType questionType;

    protected FeedbackResponseDetails(FeedbackQuestionType questionType) {
        this.questionType = questionType;
    }

    /**
     * Returns a string representation of the response.
     */
    public abstract String getAnswerString();

    /**
     * Returns a JSON string representation of the response details.
     */
    public String getJsonString() {
        assert questionType != null;
        if (questionType == FeedbackQuestionType.TEXT) {
            // For Text questions, the answer simply contains the response text, not a JSON
            return getAnswerString();
        }
        return JsonUtils.toJson(this, questionType.getResponseDetailsClass());
    }

    /**
     * Returns a deep copy of the response details.
     */
    public FeedbackResponseDetails getDeepCopy() {
        assert questionType != null;
        if (questionType == FeedbackQuestionType.TEXT) {
            return new FeedbackTextResponseDetails(getAnswerString());
        }
        String serializedResponseDetails = getJsonString();
        return JsonUtils.fromJson(serializedResponseDetails, questionType.getResponseDetailsClass());
    }

    public void setQuestionType(FeedbackQuestionType questionType) {
        this.questionType = questionType;
    }

    public FeedbackQuestionType getQuestionType() {
        return questionType;
    }
}
