package teammates.common.datatransfer;

import java.util.List;

import teammates.common.util.Assumption;
import teammates.common.util.Sanitizer;

public class FeedbackConstantSumResponseDetails extends
        FeedbackAbstractResponseDetails {
    private List<Integer> answers;

    public FeedbackConstantSumResponseDetails() {
        super(FeedbackQuestionType.CONSTSUM);
    }
    
    public FeedbackConstantSumResponseDetails(List<Integer> answers, List<String> constSumOptions, boolean distributeToRecipients) {
        super(FeedbackQuestionType.CONSTSUM);
        this.answers = answers;
        if(!distributeToRecipients){
            Assumption.assertEquals("ConstSum num response does not match num of options. "+ answers.size() + "/" + constSumOptions.size(), answers.size(), constSumOptions.size());
        }
    }
    
    /**
     * @return List of answers (for constant sum to recipients, there will only be one answer.)
     */
    public List<Integer> getAnswerList() {
        return answers;
    }
    
    @Override
    public String getAnswerString() {
        String listString = answers.toString();//[1, 2, 3] format
        return listString.substring(1, listString.length()-1);//remove []
    }

    @Override
    public String getAnswerHtml(FeedbackAbstractQuestionDetails questionDetails) {
        FeedbackConstantSumQuestionDetails csQd = (FeedbackConstantSumQuestionDetails) questionDetails;
        if(csQd.distributeToRecipients){
            return getAnswerString();
        } else {
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<ul>");
            for (int i=0 ; i<answers.size() ; i++) {
                String answerString = answers.get(i).toString();
                String optionString = csQd.constSumOptions.get(i);
                
                htmlBuilder.append("<li>");
                htmlBuilder.append( optionString + ": " + Sanitizer.sanitizeForHtml(answerString));
                htmlBuilder.append("</li>");
            }
            htmlBuilder.append("</ul>");
            return htmlBuilder.toString();
        }
    }

    @Override
    public String getAnswerCsv(FeedbackAbstractQuestionDetails questionDetails) {
        StringBuilder csvBuilder = new StringBuilder();
        
        for(int i=0 ; i<answers.size() ; i++) {
            if(!((FeedbackConstantSumQuestionDetails) questionDetails).distributeToRecipients){
                csvBuilder.append(",");
            }
            csvBuilder.append(answers.get(i));
        }

        return csvBuilder.toString();
    }

}
