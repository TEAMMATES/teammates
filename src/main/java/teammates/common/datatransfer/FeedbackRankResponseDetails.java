package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Assumption;
import teammates.common.util.Sanitizer;

public class FeedbackRankResponseDetails extends FeedbackResponseDetails {
    private List<Integer> answers;

    public FeedbackRankResponseDetails() {
        super(FeedbackQuestionType.RANK);
    }
    
    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType, 
                                       FeedbackQuestionDetails questionDetails, 
                                       String[] answer) {
        List<Integer> rankAnswer = new ArrayList<Integer>();
        for (int i=0; i < answer.length ; i++){
            try{
                rankAnswer.add(Integer.parseInt(answer[i]));
            } catch (NumberFormatException e) {
                rankAnswer.add(0);
            }
        }
        FeedbackRankQuestionDetails rankQuestion = (FeedbackRankQuestionDetails) questionDetails;
        this.setRankResponseDetails(rankAnswer, rankQuestion.options, rankQuestion.distributeToRecipients);
    }

    /**
     * @return List of answers (for ranking recipients, there will only be one answer.)
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
    public String getAnswerHtml(FeedbackQuestionDetails questionDetails) {
        FeedbackRankQuestionDetails rankQuestion = (FeedbackRankQuestionDetails) questionDetails;
        if (rankQuestion.distributeToRecipients){
            return getAnswerString();
        } else {
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<ul>");
            for (int i=0 ; i<answers.size() ; i++) {
                String answerString = answers.get(i).toString();
                String optionString = rankQuestion.options.get(i);
                
                htmlBuilder.append("<li>");
                htmlBuilder.append( optionString + ": " + Sanitizer.sanitizeForHtml(answerString));
                htmlBuilder.append("</li>");
            }
            htmlBuilder.append("</ul>");
            return htmlBuilder.toString();
        }
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        StringBuilder csvBuilder = new StringBuilder();
        
        for (int i=0 ; i < answers.size(); i++) {
            if (!((FeedbackRankQuestionDetails) questionDetails).distributeToRecipients){
                csvBuilder.append(",");
            }
            csvBuilder.append(answers.get(i));
        }

        return csvBuilder.toString();
    }

    private void setRankResponseDetails(List<Integer> answers, List<String> options, boolean distributeToRecipients) {
        this.answers = answers;
        if (!distributeToRecipients) {
            Assumption.assertEquals("Rank question: number of responses does not match number of options. " 
                                    + answers.size() + "/" + options.size(), 
                                    answers.size(), options.size());
        }
    }

}
