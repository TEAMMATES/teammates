package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Assumption;
import teammates.common.util.SanitizationHelper;

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
    public String getAnswerHtmlInstructorView(FeedbackQuestionDetails questionDetails) {
        FeedbackConstantSumQuestionDetails csQd = (FeedbackConstantSumQuestionDetails) questionDetails;
        if (csQd.isDistributeToRecipients()) {
            return getAnswerString();
        }
        StringBuilder htmlBuilder = new StringBuilder(100);
        htmlBuilder.append("<ul>");
        for (int i = 0; i < answers.size(); i++) {
            String answerString = answers.get(i).toString();
            String optionString = csQd.getConstSumOptions().get(i);

            htmlBuilder.append("<li>");
            htmlBuilder.append(optionString).append(": ").append(SanitizationHelper.sanitizeForHtml(answerString));
            htmlBuilder.append("</li>");
        }
        htmlBuilder.append("</ul>");
        return htmlBuilder.toString();
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        StringBuilder csvBuilder = new StringBuilder();

        for (int i = 0; i < answers.size(); i++) {
            if (!((FeedbackConstantSumQuestionDetails) questionDetails).isDistributeToRecipients()) {
                csvBuilder.append(',');
            }
            csvBuilder.append(answers.get(i));
        }

        return csvBuilder.toString();
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
