package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;

public class FeedbackMsqResponseDetails extends FeedbackResponseDetails {
    public List<String> answers;
    
    public FeedbackMsqResponseDetails() {
        super(FeedbackQuestionType.MSQ);
        this.answers = new ArrayList<String>();
    }
    
    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType,
            FeedbackQuestionDetails questionDetails, String[] answer) {
        this.answers = Arrays.asList(answer);
    }

    public boolean contains(String candidateAnswer) {
        return answers.contains(candidateAnswer);
    }

    @Override
    public String getAnswerString() {
        boolean isBlank = answers.size() == 1 && 
                          answers.get(0).equals("");
        return isBlank? "None of the above" : StringHelper.toString(answers, ", ");
    }
    
    public List<String> getAnswerStrings() {
        return answers;
    }

    @Override
    public String getAnswerHtml(FeedbackQuestionDetails questionDetails) {
        StringBuilder htmlBuilder = new StringBuilder();
        
        boolean isBlank = answers.size() == 1 && 
                answers.get(0).equals("");
        if (isBlank) {
            htmlBuilder.append("None of the above");
        } else {
            htmlBuilder.append("<ul class=\"selectedOptionsList\">");
            for (String answer : answers) {
                htmlBuilder.append("<li>");
                
                String answerText = Sanitizer.sanitizeForHtml(answer);
                htmlBuilder.append(Sanitizer.sanitizeForHtml(answerText));
                
                htmlBuilder.append("</li>");
            }
            htmlBuilder.append("</ul>");
        }
        
        return htmlBuilder.toString();
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        FeedbackMsqQuestionDetails msqDetails = (FeedbackMsqQuestionDetails) questionDetails;
        StringBuilder csvBuilder = new StringBuilder();
        
        boolean isBlank = answers.size() == 1 && 
                          answers.get(0).equals("");
        if (isBlank) {
            csvBuilder.append("None of the above");
        } else {
            for(String choice : msqDetails.msqChoices) {
                csvBuilder.append(",");
                if (this.contains(choice)) {
                    csvBuilder.append(Sanitizer.sanitizeForCsv(choice));
                }
            }
        }

        return csvBuilder.toString();
    }
    
    /**
     * Checks if the question has been skipped. 
     * This function is different from FeedbackResponseDetails#isQuestionSkipped 
     * as it allows empty strings
     */
    @Override
    public boolean isQuestionSkipped(Map<String, String[]> requestParameters, int questionIndx, int responseIndx) {
        String[] answer = HttpRequestHelper.getValuesFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-"+questionIndx+"-"+responseIndx);
        
        boolean allAnswersEmpty = true;
        if(answer!=null){
            for(int i=0 ; i<answer.length ; i++){
                if(answer[i]!=null){
                    allAnswersEmpty = false;
                }
            }
        }
        
        return answer != null && !allAnswersEmpty;
    }

}
