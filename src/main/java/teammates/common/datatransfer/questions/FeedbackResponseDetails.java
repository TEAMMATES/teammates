package teammates.common.datatransfer.questions;

import teammates.common.util.Assumption;
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

    public abstract String getAnswerString();

    public String getJsonString() {
        Assumption.assertNotNull(questionType);
        if (questionType == FeedbackQuestionType.TEXT) {
            // For Text questions, the answer simply contains the response text, not a JSON
            // This is due to legacy data in the data store before there were multiple question types
            return getAnswerString();
        }
        return JsonUtils.toJson(this, questionType.getResponseDetailsClass());
    }

    public FeedbackResponseDetails getDeepCopy() {
        Assumption.assertNotNull(questionType);
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
