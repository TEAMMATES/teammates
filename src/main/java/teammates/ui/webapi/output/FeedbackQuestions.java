package teammates.ui.webapi.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;

/**
 * The feedback questions response.
 */
public class FeedbackQuestions extends ApiOutput {
    private List<FeedbackQuestion> questions;

    public FeedbackQuestions(List<FeedbackQuestionAttributes> questionAttributesList) {
        questions = questionAttributesList.stream().map(FeedbackQuestion::new).collect(Collectors.toList());
    }

    public List<FeedbackQuestion> getQuestions() {
        return questions;
    }

    /**
     * Normalizes question number in questions by setting question number in sequence (i.e. 1, 2, 3, 4 ...).
     */
    public void normalizeQuestionNumber() {
        for (int i = 1; i <= questions.size(); i++) {
            questions.get(i - 1).setQuestionNumber(i);
        }
    }
}
