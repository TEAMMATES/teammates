package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;

public class FeedbackMsqResponseDetails extends FeedbackAbstractResponseDetails {
    public List<String> answers;
    
    public FeedbackMsqResponseDetails() {
        super(FeedbackQuestionType.MSQ);
        this.answers = new ArrayList<String>();
    }
    
    public FeedbackMsqResponseDetails(List<String> answers) {
        super(FeedbackQuestionType.MSQ);
        this.answers = answers;
    }
    

    public boolean contains(String candidateAnswer) {
        return answers.contains(candidateAnswer);
    }

    @Override
    public String getAnswerString() {
        return StringHelper.toString(answers, ", ");
    }
    
    public List<String> getAnswerStrings() {
        return answers;
    }

    @Override
    public String getAnswerHtml(FeedbackAbstractQuestionDetails questionDetails) {
        StringBuilder htmlBuilder = new StringBuilder();
        
        htmlBuilder.append("<ul class=\"selectedOptionsList\">");
        for (String answer : answers) {
            htmlBuilder.append("<li>");
            htmlBuilder.append(Sanitizer.sanitizeForHtml(answer));
            htmlBuilder.append("</li>");
        }
        htmlBuilder.append("</ul>");
        
        return htmlBuilder.toString();
    }

    @Override
    public String getAnswerCsv(FeedbackAbstractQuestionDetails questionDetails) {
        FeedbackMsqQuestionDetails msqDetails = (FeedbackMsqQuestionDetails) questionDetails;
        StringBuilder csvBuilder = new StringBuilder();
        
        for(String choice : msqDetails.msqChoices) {
            csvBuilder.append(",");
            if (this.contains(choice)) {
                csvBuilder.append(Sanitizer.sanitizeForCsv(choice));
            }
        }

        return csvBuilder.toString();
    }

}
