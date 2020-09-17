package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;

/**
 * The API output format of a list of {@link FeedbackQuestionAttributes}.
 */
public class FeedbackQuestionsData extends ApiOutput {
    private List<FeedbackQuestionData> questions;

    public FeedbackQuestionsData(List<FeedbackQuestionAttributes> questionAttributesList) {
        questions = questionAttributesList.stream().map(FeedbackQuestionData::new).collect(Collectors.toList());
    }

    public List<FeedbackQuestionData> getQuestions() {
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
