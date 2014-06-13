package teammates.common.datatransfer;

public class FeedbackConstantSumResponseDetails extends
        FeedbackAbstractResponseDetails {
    private int answer;

    public FeedbackConstantSumResponseDetails() {
        super(FeedbackQuestionType.CONSTSUM);
    }
    
    public FeedbackConstantSumResponseDetails(int answer) {
        super(FeedbackQuestionType.CONSTSUM);
        this.answer = answer;
    }
    
    @Override
    public String getAnswerString() {
        return Integer.toString(answer);
    }

    @Override
    public String getAnswerHtml() {
        return getAnswerString();
    }

    @Override
    public String getAnswerCsv(FeedbackAbstractQuestionDetails questionDetails) {
        return getAnswerString();
    }

}
