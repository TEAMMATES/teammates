package teammates.ui.output;

import java.util.List;

/**
 * The API output format of a list of feedback questions.
 */
public final class FeedbackQuestionsData extends ApiOutput {
    private List<FeedbackQuestionData> questions;

    private FeedbackQuestionsData() {
        // Default constructor is required for JSON deserialization
    }

    public FeedbackQuestionsData(List<FeedbackQuestionData> feedbackQuestionsData) {
        this.questions = feedbackQuestionsData;
    }

    public List<FeedbackQuestionData> getQuestions() {
        return questions;
    }

    public void setQuestions(List<FeedbackQuestionData> questions) {
        this.questions = questions;
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
