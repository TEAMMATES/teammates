package teammates.common.datatransfer.questions;

public class FeedbackRankRecipientsResponseDetails extends FeedbackRankResponseDetails {
    public int answer;

    public FeedbackRankRecipientsResponseDetails() {
        super(FeedbackQuestionType.RANK_RECIPIENTS);
    }

    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType,
                                       FeedbackQuestionDetails questionDetails,
                                       String[] answer) {
        this.setRankResponseDetails(Integer.parseInt(answer[0]));
    }

    @Override
    public String getAnswerString() {
        return Integer.toString(answer);
    }

    @Override
    public String getAnswerHtmlInstructorView(FeedbackQuestionDetails questionDetails) {
        return getAnswerString();
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        StringBuilder csvBuilder = new StringBuilder();

        csvBuilder.append(answer);

        return csvBuilder.toString();
    }

    private void setRankResponseDetails(int answer) {
        this.answer = answer;
    }

}
