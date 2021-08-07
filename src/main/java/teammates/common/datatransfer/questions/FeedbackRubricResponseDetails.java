package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains specific structure and processing logic for rubric feedback responses.
 */
public class FeedbackRubricResponseDetails extends FeedbackResponseDetails {

    /**
     * List of integers, the size of the list corresponds to the number of sub-questions.
     * Each integer at index i, represents the choice chosen for sub-question i.
     */
    private List<Integer> answer;

    public FeedbackRubricResponseDetails() {
        super(FeedbackQuestionType.RUBRIC);
        answer = new ArrayList<>();
    }

    @Override
    public String getAnswerString() {
        return this.answer.toString();
    }

    public List<Integer> getAnswer() {
        return answer;
    }

    public void setAnswer(List<Integer> answer) {
        this.answer = answer;
    }
}
