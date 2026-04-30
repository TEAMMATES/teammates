package teammates.common.datatransfer.questions;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FeedbackTextResponseDetails other)) {
            return false;
        }
        return getQuestionType() == other.getQuestionType()
                && Objects.equals(answer, other.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQuestionType(), answer);
    }
}
