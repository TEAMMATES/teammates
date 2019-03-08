package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Assumption;

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
        return new ArrayList<>();
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

}
