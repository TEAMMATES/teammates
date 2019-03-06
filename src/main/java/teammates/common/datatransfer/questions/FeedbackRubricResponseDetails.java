package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Logger;

public class FeedbackRubricResponseDetails extends FeedbackResponseDetails {

    private static final Logger log = Logger.getLogger();

    /**
     * List of integers, the size of the list corresponds to the number of sub-questions.
     * Each integer at index i, represents the choice chosen for sub-question i.
     */
    public List<Integer> answer;

    public FeedbackRubricResponseDetails() {
        super(FeedbackQuestionType.RUBRIC);
    }

    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType,
                                       FeedbackQuestionDetails questionDetails, String[] answer) {

        /**
         * Example: a response in the form: "0-1,1-0"
         * means that for sub-question 0, choice 1 is chosen,
         * and for sub-question 1, choice 0 is chosen.
         */

        String rawResponses = answer[0];
        FeedbackRubricQuestionDetails fqd = (FeedbackRubricQuestionDetails) questionDetails;

        initializeEmptyAnswerList(fqd.getNumOfRubricSubQuestions());

        // Parse and extract answers
        String[] subQuestionResponses = rawResponses.split(Pattern.quote(","));
        for (String subQuestionResponse : subQuestionResponses) {
            String[] subQuestionIndexAndChoice = subQuestionResponse.split(Pattern.quote("-"));

            if (subQuestionIndexAndChoice.length != 2) {
                // Expected length is 2.
                // Failed to parse, ignore response.
                continue;
            }

            try {
                int subQuestionIndex = Integer.parseInt(subQuestionIndexAndChoice[0]);
                int subQuestionChoice = Integer.parseInt(subQuestionIndexAndChoice[1]);
                if (subQuestionIndex >= 0 && subQuestionIndex < fqd.getNumOfRubricSubQuestions()
                        && subQuestionChoice >= 0 && subQuestionChoice < fqd.getNumOfRubricChoices()) {
                    setAnswer(subQuestionIndex, subQuestionChoice);
                } // else the indexes are invalid.
            } catch (NumberFormatException e) {
                // Failed to parse, ignore response.
                log.warning(TeammatesException.toStringWithStackTrace(e));
            }
        }
    }

    /**
     * Initializes the answer list to have empty responses.
     */
    private void initializeEmptyAnswerList(int numSubQuestions) {
        answer = new ArrayList<>();
        for (int i = 0; i < numSubQuestions; i++) {
            // -1 indicates no choice chosen
            answer.add(-1);
        }
    }

    @Override
    public String getAnswerString() {
        return this.answer.toString();
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        return answer.toString();
    }

    @Override
    public List<String> validateResponseDetails(FeedbackQuestionAttributes correspondingQuestion) {
        return new ArrayList<>();
    }

    public int getAnswer(int subQuestionIndex) {
        return answer.get(subQuestionIndex);
    }

    public void setAnswer(int subQuestionIndex, int choice) {
        this.answer.set(subQuestionIndex, choice);
    }
}
