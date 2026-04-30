package teammates.common.datatransfer.questions;

import java.util.Objects;

/**
 * Contains specific structure and processing logic for MCQ feedback responses.
 */
public class FeedbackMcqResponseDetails extends FeedbackResponseDetails {
    private String answer;
    private boolean isOther;
    private String otherFieldContent; //content of other field if "other" is selected as the answer

    public FeedbackMcqResponseDetails() {
        super(FeedbackQuestionType.MCQ);
        answer = "";
        isOther = false;
        otherFieldContent = "";
    }

    @Override
    public String getAnswerString() {
        if (isOther) {
            return otherFieldContent;
        }
        return answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isOther() {
        return isOther;
    }

    public void setOther(boolean other) {
        isOther = other;
    }

    public String getOtherFieldContent() {
        return otherFieldContent;
    }

    public void setOtherFieldContent(String otherFieldContent) {
        this.otherFieldContent = otherFieldContent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FeedbackMcqResponseDetails other)) {
            return false;
        }
        return getQuestionType() == other.getQuestionType()
                && isOther == other.isOther
                && Objects.equals(answer, other.answer)
                && Objects.equals(otherFieldContent, other.otherFieldContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQuestionType(), answer, isOther, otherFieldContent);
    }
}
