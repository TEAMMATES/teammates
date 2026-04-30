package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Contains specific structure and processing logic for constant sum feedback responses.
 */
public class FeedbackConstantSumResponseDetails extends FeedbackResponseDetails {
    private List<Integer> answers;

    public FeedbackConstantSumResponseDetails() {
        super(FeedbackQuestionType.CONSTSUM);
        answers = new ArrayList<>();
    }

    @Override
    public String getAnswerString() {
        String listString = answers.toString(); //[1, 2, 3] format
        return listString.substring(1, listString.length() - 1); //remove []
    }

    public List<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FeedbackConstantSumResponseDetails other)) {
            return false;
        }
        return getQuestionType() == other.getQuestionType()
                && Objects.equals(answers, other.answers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQuestionType(), answers);
    }
}
