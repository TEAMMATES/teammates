package teammates.common.datatransfer.questions;

import teammates.common.util.JsonUtils;

/**
 * A class holding the details for the response of a specific question type.
 *
 * <p>This abstract class is inherited by concrete Feedback*ResponseDetails
 * classes which provides the implementation for the various abstract methods
 * such that pages can render the correct information depending on the
 * question type.
 */
public abstract class FeedbackResponseDetails {
    private FeedbackQuestionType questionType;

    public FeedbackResponseDetails(FeedbackQuestionType questionType) {
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
