package teammates.common.datatransfer;

import teammates.common.util.Sanitizer;

public class FeedbackTextResponseDetails extends
        FeedbackAbstractResponseDetails {
    //TODO use this instead of plain text for essay questions
    //will involve converting the existing database
    
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
