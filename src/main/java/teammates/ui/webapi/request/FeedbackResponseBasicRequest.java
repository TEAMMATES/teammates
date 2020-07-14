package teammates.ui.webapi.request;

import java.util.Map;

import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.util.JsonUtils;

/**
 * The basic request of modifying a feedback response.
 */
public class FeedbackResponseBasicRequest extends BasicRequest {

    private String recipientIdentifier;

    private FeedbackQuestionType questionType;

    private Map<String, Object> responseDetails;

    public String getRecipientIdentifier() {
        return recipientIdentifier;
    }

    /**
     * Gets the question type of the response.
     */
    public FeedbackQuestionType getQuestionType() {
        // TODO remove this after migrate CONSTSUM to either CONSTSUM_OPTIONS or CONSTSUM_RECIPIENTS
        if (questionType == FeedbackQuestionType.CONSTSUM_OPTIONS
                || questionType == FeedbackQuestionType.CONSTSUM_RECIPIENTS) {
            return FeedbackQuestionType.CONSTSUM;
        }
        return questionType;
    }

    /**
     * Get the feedback response details in the request.
     */
    public FeedbackResponseDetails getResponseDetails() {
        FeedbackResponseDetails details =
                JsonUtils.fromJson(JsonUtils.toJson(responseDetails), getQuestionType().getResponseDetailsClass());
        details.setQuestionType(getQuestionType());
        return details;
    }

    public void setRecipientIdentifier(String recipientIdentifier) {
        this.recipientIdentifier = recipientIdentifier;
    }

    public void setQuestionType(FeedbackQuestionType feedbackQuestionType) {
        this.questionType = feedbackQuestionType;
    }

    public void setResponseDetails(FeedbackResponseDetails feedbackResponseDetails) {
        this.responseDetails = JsonUtils.fromJson(JsonUtils.toJson(feedbackResponseDetails), Map.class);
    }

    @Override
    public void validate() {
        assertTrue(recipientIdentifier != null, "recipientIdentifier cannot be null");
        assertTrue(questionType != null, "questionType cannot be null");
        assertTrue(responseDetails != null, "responseDetails cannot be null");
    }
}
