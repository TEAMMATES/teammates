package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

public class FeedbackConstantSumResponseDetails extends
        FeedbackResponseDetails {
    private List<Integer> answers;

    public FeedbackConstantSumResponseDetails() {
        super(FeedbackQuestionType.CONSTSUM);
    }

    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType,
                                       FeedbackQuestionDetails questionDetails, String[] answer) {
        List<Integer> constSumAnswer = new ArrayList<>();
        for (String answerPart : answer) {
            try {
                constSumAnswer.add(Integer.parseInt(answerPart));
            } catch (NumberFormatException e) {
                constSumAnswer.add(0);
            }
        }
        FeedbackConstantSumQuestionDetails constSumQd = (FeedbackConstantSumQuestionDetails) questionDetails;
        this.setConstantSumResponseDetails(constSumAnswer,
                                           constSumQd.getConstSumOptions(),
                                           constSumQd.isDistributeToRecipients());
    }

    /**
     * Returns the list of answers (for constant sum to recipients, there will only be one answer).
     */
    public List<Integer> getAnswerList() {
        return answers;
    }

    @Override
    public String getAnswerString() {
        String listString = answers.toString(); //[1, 2, 3] format
        return listString.substring(1, listString.length() - 1); //remove []
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        StringBuilder csvBuilder = new StringBuilder();

        for (Integer answer : answers) {
            if (!((FeedbackConstantSumQuestionDetails) questionDetails).isDistributeToRecipients()) {
                csvBuilder.append(',');
            }
            csvBuilder.append(answer);
        }

        return csvBuilder.toString();
    }

    @Override
    public List<String> validateResponseDetails(FeedbackQuestionAttributes correspondingQuestion) {
        FeedbackConstantSumQuestionDetails questionDetails =
                (FeedbackConstantSumQuestionDetails) correspondingQuestion.getQuestionDetails();

        List<String> errors = new ArrayList<>();

        if (questionDetails.isDistributeToRecipients()) {
            if (answers.size() != 1) {
                // distribute to recipient must have array size one
                errors.add(Const.FeedbackQuestion.CONST_SUM_ANSWER_RECIPIENT_NOT_MATCH);
                return errors;
            }

            if (answers.get(0) < 0) {
                errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_NEGATIVE);
                return errors;
            }

            // difficult to do cross-responses validation
            return errors;
        }

        if (answers.size() != questionDetails.getNumOfConstSumOptions()) {
            errors.add(Const.FeedbackQuestion.CONST_SUM_ANSWER_OPTIONS_NOT_MATCH);
            return errors;
        }

        //Check that all points are >= 0
        int sum = 0;
        for (int i : answers) {
            if (i < 0) {
                errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_NEGATIVE);
                return errors;
            }

            sum += i;
        }

        int numOptions = questionDetails.getNumOfConstSumOptions();
        int totalPoints = questionDetails.isPointsPerOption()
                ? questionDetails.getPoints() * numOptions : questionDetails.getPoints();

        //Check that points sum up properly
        if (sum != totalPoints) {
            errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_MISMATCH);
            return errors;
        }

        //Check that points are given unevenly for all/at least some options as per the question settings
        Set<Integer> answerSet = new HashSet<>();
        if (questionDetails.getDistributePointsFor().equals(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY.getDisplayedOption())) {
            boolean hasDifferentPoints = false;
            for (int i : answers) {
                if (!answerSet.isEmpty() && !answerSet.contains(i)) {
                    hasDifferentPoints = true;
                    break;
                }
                answerSet.add(i);
            }

            if (!hasDifferentPoints) {
                errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_SOME_UNIQUE);
                return errors;
            }
        }

        if (questionDetails.getDistributePointsFor().equals(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption())) {
            for (int i : answers) {
                if (answerSet.contains(i)) {
                    errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_UNIQUE);
                    return errors;
                }
                answerSet.add(i);
            }
        }

        return errors;
    }

    private void setConstantSumResponseDetails(List<Integer> answers, List<String> constSumOptions,
                                               boolean distributeToRecipients) {
        this.answers = answers;
        if (!distributeToRecipients) {
            Assumption.assertEquals("ConstSum num response does not match num of options. "
                                            + answers.size() + "/" + constSumOptions.size(),
                                    answers.size(), constSumOptions.size());
        }
    }

    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }
}
