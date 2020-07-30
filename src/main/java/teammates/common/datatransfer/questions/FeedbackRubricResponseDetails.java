package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;

public class FeedbackRubricResponseDetails extends FeedbackResponseDetails {

    /**
     * List of integers, the size of the list corresponds to the number of sub-questions.
     * Each integer at index i, represents the choice chosen for sub-question i.
     */
    private List<Integer> answer;

    public FeedbackRubricResponseDetails() {
        super(FeedbackQuestionType.RUBRIC);
    }

    @Override
    public String getAnswerString() {
        return this.answer.toString();
    }

    @Override
    public List<String> validateResponseDetails(FeedbackQuestionAttributes correspondingQuestion) {
        List<String> errors = new ArrayList<>();

        FeedbackRubricQuestionDetails questionDetails =
                (FeedbackRubricQuestionDetails) correspondingQuestion.getQuestionDetails();

        if (answer.isEmpty()) {
            errors.add(Const.FeedbackQuestion.RUBRIC_EMPTY_ANSWER);
        }

        if (answer.size() != questionDetails.getNumOfRubricSubQuestions()) {
            errors.add(Const.FeedbackQuestion.RUBRIC_INVALID_ANSWER);
        }

        if (answer.stream().anyMatch(choice ->
                choice != Const.FeedbackQuestion.RUBRIC_ANSWER_NOT_CHOSEN
                        && (choice < 0 || choice >= questionDetails.getNumOfRubricChoices()))) {
            errors.add(Const.FeedbackQuestion.RUBRIC_INVALID_ANSWER);
        }

        if (answer.stream().allMatch(choice -> choice == Const.FeedbackQuestion.RUBRIC_ANSWER_NOT_CHOSEN)) {
            errors.add(Const.FeedbackQuestion.RUBRIC_INVALID_ANSWER);
        }

        return errors;
    }

    public void setAnswer(List<Integer> answer) {
        this.answer = answer;
    }
}
