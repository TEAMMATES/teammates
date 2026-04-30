package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FeedbackRubricResponseDetails other)) {
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
