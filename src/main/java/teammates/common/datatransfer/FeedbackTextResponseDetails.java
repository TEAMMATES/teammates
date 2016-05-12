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
    
    public FeedbackTextResponseDetails(final String answer) {
        super(FeedbackQuestionType.TEXT);
        this.answer = answer;
    }

    @Override
    public void extractResponseDetails(final FeedbackQuestionType questionType,
            final FeedbackQuestionDetails questionDetails, final String[] answer) {
        this.answer = answer[0];
    }

    @Override
    public String getAnswerString() {
        return answer;
    }

    @Override
    public String getAnswerHtml(final FeedbackQuestionDetails questionDetails) {
        return Sanitizer.sanitizeForHtml(answer);
    }

    @Override
    public String getAnswerCsv(final FeedbackQuestionDetails questionDetails) {
        return Sanitizer.sanitizeForCsv(answer);
    }

}
