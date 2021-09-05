package teammates.common.datatransfer.questions;

import teammates.common.util.SanitizationHelper;

/**
 * Contains specific structure and processing logic for text feedback responses.
 */
public class FeedbackTextResponseDetails extends FeedbackResponseDetails {

    private String answer;

    public FeedbackTextResponseDetails() {
        super(FeedbackQuestionType.TEXT);
        this.answer = "";
    }

    public FeedbackTextResponseDetails(String answer) {
        super(FeedbackQuestionType.TEXT);
        this.answer = SanitizationHelper.sanitizeForRichText(answer);
    }

    @Override
    public String getAnswerString() {
        return SanitizationHelper.sanitizeForRichText(answer);
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
