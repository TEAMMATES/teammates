package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;

public class FeedbackRubricResponseDetails extends FeedbackResponseDetails {
    /**
     * List of integers, the size of the list corresponds to the number of sub-questions
     * Each integer at index i, represents the choice chosen for sub-question i
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

        initializeEmptyAnswerList(fqd.numOfRubricSubQuestions);
        
        // Parse and extract answers
        String[] subQuestionResponses = rawResponses.split(Pattern.quote(","));
        for (int i=0 ; i<subQuestionResponses.length ; i++) {
            String[] subQuestionIndexAndChoice = subQuestionResponses[i].split(Pattern.quote("-"));
            
            if (subQuestionIndexAndChoice.length != 2) {
                // Expected length is 2.
                // Failed to parse, ignore response.
                continue;
            }
            
            try {
                int subQuestionIndex = Integer.parseInt(subQuestionIndexAndChoice[0]);
                int subQuestionChoice = Integer.parseInt(subQuestionIndexAndChoice[1]);
                if (subQuestionIndex >=0 && subQuestionIndex <fqd.numOfRubricSubQuestions &&
                        subQuestionChoice >=0 && subQuestionChoice <fqd.numOfRubricChoices) {
                    setAnswer(subQuestionIndex, subQuestionChoice);
                } // else the indexes are invalid.
            } catch (NumberFormatException e) {
                // Failed to parse, ignore response.
            } 
        }
    }
    
    /**
     * Initializes the answer list to have empty responses
     * -1 indicates no choice chosen
     * @param numSubQuestions
     */
    private void initializeEmptyAnswerList(int numSubQuestions) {
        answer = new ArrayList<Integer>();
        for (int i=0 ; i<numSubQuestions ; i++) {
            answer.add(-1);
        }
    }

    @Override
    public String getAnswerString() {
        return this.answer.toString();
    }

    @Override
    public String getAnswerHtml(FeedbackQuestionDetails questionDetails) {
        FeedbackRubricQuestionDetails fqd = (FeedbackRubricQuestionDetails) questionDetails;
        String html = "";
        for (int i=0 ; i<answer.size() ; i++) {
            int chosenIndex = answer.get(i);
            String chosenChoice = "";
            if (chosenIndex == -1) {
                chosenChoice = "<span class=\"color_neutral\"><i>" + 
                        Const.INSTRUCTOR_FEEDBACK_RESULTS_MISSING_RESPONSE + 
                        "</i></span>";
                html += StringHelper.integerToLowerCaseAlphabeticalIndex(i+1) + ") " + chosenChoice + "<br>";
            } else {
                chosenChoice = Sanitizer.sanitizeForHtml(fqd.rubricChoices.get(answer.get(i)));
                html += StringHelper.integerToLowerCaseAlphabeticalIndex(i+1) + ") " + chosenChoice
                        + " <span class=\"color_neutral\"><i>(Choice " + (chosenIndex+1) + ")</i></span><br>";
            }
            
        }
        
        return html;
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        return answer.toString();
    }
    
    public int getAnswer(int subQuestionIndex) {
        return answer.get(subQuestionIndex);
    }

    public void setAnswer(int subQuestionIndex, int choice) {
        this.answer.set(subQuestionIndex, choice);
    }
}
