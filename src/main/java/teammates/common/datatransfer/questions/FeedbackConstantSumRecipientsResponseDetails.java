package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Contains specific structure and processing logic for constant sum recipients feedback responses.
 */
public class FeedbackConstantSumRecipientsResponseDetails extends FeedbackResponseDetails {
    private List<Integer> answers;

    public FeedbackConstantSumRecipientsResponseDetails() {
        super(FeedbackQuestionType.CONSTSUM_RECIPIENTS);
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
        if (!(obj instanceof FeedbackConstantSumRecipientsResponseDetails other)) {
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
