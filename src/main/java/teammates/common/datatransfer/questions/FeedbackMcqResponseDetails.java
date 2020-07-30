package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;

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

    @Override
    public List<String> validateResponseDetails(FeedbackQuestionAttributes correspondingQuestion) {
        List<String> errors = new ArrayList<>();
        List<String> mcqChoices = ((FeedbackMcqQuestionDetails) correspondingQuestion
                .getQuestionDetails()).getMcqChoices();

        // if other option is not selected and selected answer is not part of Mcq option list trigger this error.
        if (!isOther && !mcqChoices.contains(getAnswerString())) {
            errors.add(getAnswerString() + " " + Const.FeedbackQuestion.MCQ_ERROR_INVALID_OPTION);
        }

        // if other option is selected but not text is provided trigger this error
        if (isOther && getAnswerString().trim().equals("")) {
            errors.add(Const.FeedbackQuestion.MCQ_ERROR_OTHER_CONTENT_NOT_PROVIDED);
        }

        return errors;
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
