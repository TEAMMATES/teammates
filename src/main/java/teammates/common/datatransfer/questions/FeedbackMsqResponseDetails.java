package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

public class FeedbackMsqResponseDetails extends FeedbackResponseDetails {
    private List<String> answers; // answers contain the "other" answer, if any
    private boolean isOther;
    private String otherFieldContent; //content of other field if "other" is selected as the answer

    public FeedbackMsqResponseDetails() {
        super(FeedbackQuestionType.MSQ);
        this.answers = new ArrayList<>();
        isOther = false;
        otherFieldContent = "";
    }

    @Override
    public String getAnswerString() {
        return StringHelper.toString(answers, ", ");
    }

    @Override
    public List<String> validateResponseDetails(FeedbackQuestionAttributes correspondingQuestion) {
        List<String> errors = new ArrayList<>();
        FeedbackMsqQuestionDetails msqQuestionDetails = (FeedbackMsqQuestionDetails) correspondingQuestion
                .getQuestionDetails();
        List<String> msqChoices = msqQuestionDetails.getMsqChoices();
        int maxSelectableChoices = msqQuestionDetails.getMaxSelectableChoices();
        int minSelectableChoices = msqQuestionDetails.getMinSelectableChoices();
        boolean isOtherEnabled = msqQuestionDetails.isOtherEnabled();

        // number of Msq options selected including other option
        int totalChoicesSelected = answers.size() + (isOther ? 1 : 0);
        boolean isMaxSelectableEnabled = maxSelectableChoices != Integer.MIN_VALUE;
        boolean isMinSelectableEnabled = minSelectableChoices != Integer.MIN_VALUE;
        boolean isNoneOfTheAboveOptionEnabled = answers.contains(Const.FeedbackQuestion.MSQ_ANSWER_NONE_OF_THE_ABOVE);

        // if other is not enabled and other is selected as an answer trigger this error
        if (isOther && !isOtherEnabled) {
            errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_OPTION);
        }

        // if other is not chosen while other field is not empty trigger this error
        if (isOtherEnabled && !isOther && !getOtherFieldContent().isEmpty()) {
            errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_OPTION);
        }

        List<String> validChoices = new ArrayList<>(msqChoices);
        if (isOtherEnabled && isOther) {
            // other field content becomes a valid choice if other is enabled
            validChoices.add(getOtherFieldContent());
        }
        // if selected answers are not a part of the Msq option list trigger this error
        boolean isAnswersPartOfChoices = validChoices.containsAll(answers);
        if (!isAnswersPartOfChoices && !isNoneOfTheAboveOptionEnabled) {
            errors.add(getAnswerString() + " " + Const.FeedbackQuestion.MSQ_ERROR_INVALID_OPTION);
        }

        // if other option is selected but no text is provided trigger this error
        if (isOther && getOtherFieldContent().trim().equals("")) {
            errors.add(Const.FeedbackQuestion.MSQ_ERROR_OTHER_CONTENT_NOT_PROVIDED);
        }

        // if other option is selected but not in the answer list trigger this error
        if (isOther && !answers.contains(otherFieldContent)) {
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

    public Boolean isOtherOptionAnswer() {
        return isOther;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
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
