package teammates.common.datatransfer.questions;

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
}
