package teammates.common.datatransfer;

import teammates.common.util.Sanitizer;

public class FeedbackTextResponseDetails extends
        FeedbackAbstractResponseDetails {
    
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
    public boolean extractResponseDetails(FeedbackQuestionType questionType,
            FeedbackAbstractQuestionDetails questionDetails, String[] answer) {
        this.answer = answer[0];
        return true;
    }

    @Override
    public String getAnswerString() {
        return answer;
    }

    @Override
    public String getAnswerHtml(FeedbackAbstractQuestionDetails questionDetails) {
        return Sanitizer.sanitizeForHtml(answer);
    }

    @Override
    public String getAnswerCsv(FeedbackAbstractQuestionDetails questionDetails) {
        return Sanitizer.sanitizeForCsv(answer);
    }

}
