package teammates.common.datatransfer;


public class FeedbackRankRecipientsResponseDetails extends FeedbackRankResponseDetails {
    public int answer;
    
    public FeedbackRankRecipientsResponseDetails() {
        super(FeedbackQuestionType.RANK_RECIPIENTS);
    }
    
    @Override
    public void extractResponseDetails(final FeedbackQuestionType questionType, 
                                       final FeedbackQuestionDetails questionDetails, 
                                       final String[] answer) {
        this.setRankResponseDetails(Integer.parseInt(answer[0]));
    }

    
    @Override
    public String getAnswerString() {
        return Integer.toString(answer);
    }

    @Override
    public String getAnswerHtml(final FeedbackQuestionDetails questionDetails) {
        return getAnswerString();
    }

    @Override
    public String getAnswerCsv(final FeedbackQuestionDetails questionDetails) {
        StringBuilder csvBuilder = new StringBuilder();
        
        csvBuilder.append(answer);

        return csvBuilder.toString();
    }

    private void setRankResponseDetails(final int answer) {
        this.answer = answer;
    }

}
