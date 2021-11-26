package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

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
}
