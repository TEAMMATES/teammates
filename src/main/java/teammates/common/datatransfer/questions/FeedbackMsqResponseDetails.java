package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

public class FeedbackMsqResponseDetails extends FeedbackResponseDetails {
    public List<String> answers; // answers contain the "other" answer, if any
    private boolean isOther;
    private String otherFieldContent; //content of other field if "other" is selected as the answer

    public FeedbackMsqResponseDetails() {
        super(FeedbackQuestionType.MSQ);
        this.answers = new ArrayList<>();
        isOther = false;
        otherFieldContent = "";
    }

    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType,
                                       FeedbackQuestionDetails questionDetails, String[] answer) {
        this.answers = Arrays.asList(answer);
    }

    public void extractResponseDetails(FeedbackQuestionType questionType,
                                    FeedbackQuestionDetails questionDetails, String[] answer,
                                    Map<String, String[]> requestParameters, int questionIndx,
                                    int responseIndx) {

        // "1" if other is selected, "0" if other is not selected, null if other is disabled by the instructor
        String isOtherOptionAnswer = HttpRequestHelper.getValueFromParamMap(
                                        requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ISOTHEROPTIONANSWER
                                        + "-" + questionIndx + "-" + responseIndx);

        if ("1".equals(isOtherOptionAnswer)) {
            isOther = true;
            try {
                otherFieldContent = answer[answer.length - 1];
            } catch (IndexOutOfBoundsException e) {
                otherFieldContent = "";
            }
        }

        extractResponseDetails(questionType, questionDetails, answer);
    }

    public boolean contains(String candidateAnswer) {
        return answers.contains(candidateAnswer);
    }

    @Override
    public String getAnswerString() {
        return StringHelper.toString(answers, ", ");
    }

    public List<String> getAnswerStrings() {
        return answers;
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        FeedbackMsqQuestionDetails msqDetails = (FeedbackMsqQuestionDetails) questionDetails;
        StringBuilder csvBuilder = new StringBuilder();

        if (isAnswerBlank()) {
            csvBuilder.append("");
        } else {
            for (String choice : msqDetails.getMsqChoices()) {
                csvBuilder.append(',');
                if (this.contains(choice)) {
                    csvBuilder.append(SanitizationHelper.sanitizeForCsv(choice));
                }
            }
        }

        return csvBuilder.toString();
    }

    @Override
    public List<String> validateResponseDetails(FeedbackQuestionAttributes correspondingQuestion) {
        List<String> errors = new ArrayList<>();
        FeedbackMsqQuestionDetails msqQuestionDetails = (FeedbackMsqQuestionDetails) correspondingQuestion
                .getQuestionDetails();
        List<String> msqChoices = msqQuestionDetails.getMsqChoices();
        int maxSelectableChoices = msqQuestionDetails.getMaxSelectableChoices();
        int minSelectableChoices = msqQuestionDetails.getMinSelectableChoices();
        boolean isOtherEnabled = msqQuestionDetails.getOtherEnabled();

        // number of Msq options selected including other option
        int totalChoicesSelected = answers.size() + (isOther ? 1 : 0);
        boolean isMaxSelectableEnabled = maxSelectableChoices != -1;
        boolean isMinSelectableEnabled = minSelectableChoices != -1;
        boolean isNoneOfTheAboveOptionEnabled = answers.contains(Const.FeedbackQuestion.MSQ_ANSWER_NONE_OF_THE_ABOVE);

        // if other is not enabled and other is selected as an answer trigger this error
        if (isOther && !isOtherEnabled) {
            errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_OPTION);
        }

        // if selected answers are not a part of the Msq option list trigger this error
        boolean isAnswersPartOfChoices = msqChoices.containsAll(answers);
        if (!isAnswersPartOfChoices && !isNoneOfTheAboveOptionEnabled) {
            errors.add(getAnswerString() + " " + Const.FeedbackQuestion.MSQ_ERROR_INVALID_OPTION);
        }

        // if other option is selected but no text is provided trigger this error
        if (isOther && getOtherFieldContent().trim().equals("")) {
            errors.add(Const.FeedbackQuestion.MSQ_ERROR_OTHER_CONTENT_NOT_PROVIDED);
        }

        // if total choices selected exceed maximum choices allowed trigger this error
        if (isMaxSelectableEnabled && totalChoicesSelected > maxSelectableChoices) {
            errors.add(Const.FeedbackQuestion.MSQ_ERROR_NUM_SELECTED_MORE_THAN_MAXIMUM + maxSelectableChoices);
        }

        if (isMinSelectableEnabled) {
            // if total choices selected is less than the minimum required choices
            if (totalChoicesSelected < minSelectableChoices) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_NUM_SELECTED_LESS_THAN_MINIMUM + minSelectableChoices);
            }
            // if minimumSelectableChoices is enabled and None of the Above is selected as an answer trigger this error
            if (isNoneOfTheAboveOptionEnabled) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_OPTION);
            }
        } else {
            // if none of the above is selected AND other options are selected trigger this error
            if ((answers.size() > 1 || isOther) && isNoneOfTheAboveOptionEnabled) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_NONE_OF_THE_ABOVE_ANSWER);
            }
        }
        return errors;
    }

    protected boolean isAnswerBlank() {
        return answers.size() == 1 && answers.get(0).isEmpty();
    }

    public Boolean isOtherOptionAnswer() {
        return isOther;
    }

    public String getOtherFieldContent() {
        return otherFieldContent;
    }

}
