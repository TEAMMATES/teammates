package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.SanitizationHelper;

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
    public void extractResponseDetails(FeedbackQuestionType questionType,
            FeedbackQuestionDetails questionDetails, String[] answer) {
        /*
         * answer[0] contains the answer given by the student, answer[1] is "1"
         * if "other" is selected by the student, "0" if "other" is not
         * selected, null if "other" is disabled by the instructor
         */
        isOther = answer.length >= 2 && "1".equals(answer[1]);

        if (isOther) {
            this.answer = "Other";
            this.otherFieldContent = answer[0];
        } else {
            this.answer = answer[0];
            this.otherFieldContent = "";
        }
    }

    public void extractResponseDetails(FeedbackQuestionType questionType,
                                    FeedbackQuestionDetails questionDetails, String[] rawAnswer,
                                    Map<String, String[]> requestParameters, int questionIndx,
                                    int responseIndx) {

        String[] answer = appendOtherOptionFlagToAnswer(rawAnswer, requestParameters, questionIndx, responseIndx);
        /*
         * answer[0] contains the answer given by the student, answer[1] is "1"
         * if "other" is selected by the student, "0" if "other" is not
         * selected, null if "other" is disabled by the instructor
         */
        isOther = answer.length >= 2 && "1".equals(answer[1]);

        if (isOther) {
            this.answer = "Other";
            this.otherFieldContent = answer[0];
        } else {
            this.answer = answer[0];
            this.otherFieldContent = "";
        }
    }

    @Override
    public String getAnswerString() {
        if (isOther) {
            return otherFieldContent;
        }
        return answer;
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        return SanitizationHelper.sanitizeForCsv(getAnswerString());
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

    public Boolean isOtherOptionAnswer() {
        return isOther;
    }

    public String getOtherFieldContent() {
        return otherFieldContent;
    }

    private String[] appendOtherOptionFlagToAnswer(String[] answer, Map<String, String[]> requestParameters,
                                    int questionIndx, int responseIndx) {
        String isOtherOptionAnswer = HttpRequestHelper.getValueFromParamMap(
                                        requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MCQ_ISOTHEROPTIONANSWER
                                                           + "-" + questionIndx + "-" + responseIndx);
        if (answer != null) {
            String[] answerWithOtherOptionFlag = new String[answer.length + 1];

            answerWithOtherOptionFlag[0] = answer[0]; // answer given by the student
            answerWithOtherOptionFlag[1] = isOtherOptionAnswer; // "1" (other is selected) or "0" (other is not selected)
            return answerWithOtherOptionFlag;
        }
        return answer;
    }
}
