package teammates.common.datatransfer.questions;

import teammates.common.util.SanitizationHelper;

public class FeedbackTextResponseDetails extends FeedbackResponseDetails {

    //For essay questions the response is saved as plain-text due to legacy format before there were multiple question types
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
