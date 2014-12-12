package teammates.common.datatransfer;

import teammates.common.util.Sanitizer;

public class FeedbackTextResponseDetails extends
        FeedbackResponseDetails {
    
    //For essay questions the response is saved as plain-text due to legacy format before there were multiple question types
    public String answer;
    
    public FeedbackTextResponseDetails(){
        super(FeedbackQuestionType.TEXT);
        this.answer = "";
    }
    
    public FeedbackTextResponseDetails(String answer) {
        super(FeedbackQuestionType.TEXT);
        this.answer = answer;
    }

    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType,
            FeedbackQuestionDetails questionDetails, String[] answer) {
        this.answer = answer[0];
    }

    @Override
    public String getAnswerString() {
        return answer;
    }

    @Override
    public String getAnswerHtml(FeedbackQuestionDetails questionDetails) {
        return Sanitizer.sanitizeForHtml(answer);
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        return Sanitizer.sanitizeForCsv(answer);
    }

}
