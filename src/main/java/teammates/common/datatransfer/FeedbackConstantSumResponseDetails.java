package teammates.common.datatransfer;

import java.util.List;

public class FeedbackConstantSumResponseDetails extends
        FeedbackAbstractResponseDetails {
    private List<Integer> answers;    

    public FeedbackConstantSumResponseDetails() {
        super(FeedbackQuestionType.CONSTSUM);
    }
    
    public FeedbackConstantSumResponseDetails(List<Integer> answers) {
        super(FeedbackQuestionType.CONSTSUM);
        this.answers = answers;
    }
    
    @Override
    public String getAnswerString() {
        String listString = answers.toString();//[1, 2, 3] format
        return listString.substring(1, listString.length()-1);//remove []
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
