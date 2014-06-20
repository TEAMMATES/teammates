package teammates.common.datatransfer;

import teammates.common.util.Sanitizer;

public class FeedbackMcqResponseDetails extends FeedbackAbstractResponseDetails {
    private String answer;
    private boolean isOther;
    private String otherFieldContent; //content of other field if "other" is selected as the answer
    
    public FeedbackMcqResponseDetails() {
        super(FeedbackQuestionType.MCQ);
        answer = "";
        isOther = false;
        otherFieldContent = "";
    }
    
    /** Creates a new FeedbackMcqResponseDetails object
     * 
     * @param answer The answer to the question or the content of other field if other is chosen
     * @param isOther Whether or not other is chosen as the answer
     */
    public FeedbackMcqResponseDetails(String answer, boolean isOther) {
        super(FeedbackQuestionType.MCQ);
        
        this.isOther = isOther;
        if(isOther){
            this.answer = "Other";
            this.otherFieldContent = answer;
        } else {
            this.answer = answer;
            this.otherFieldContent = "";
        }
    }

    @Override
    public String getAnswerString() {
        if(isOther){
            return otherFieldContent;
        } else {
            return answer;
        }
    }

    @Override
    public String getAnswerHtml(FeedbackAbstractQuestionDetails questionDetails) {
        return Sanitizer.sanitizeForHtml(getAnswerString());
    }

    @Override
    public String getAnswerCsv(FeedbackAbstractQuestionDetails questionDetails) {
        return Sanitizer.sanitizeForCsv(getAnswerString());
    }
}
