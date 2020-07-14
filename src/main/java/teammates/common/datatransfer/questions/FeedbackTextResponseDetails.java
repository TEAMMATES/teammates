package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
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

    @Override
    public List<String> validateResponseDetails(FeedbackQuestionAttributes correspondingQuestion) {
        // no need to do validation
        return new ArrayList<>();
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
