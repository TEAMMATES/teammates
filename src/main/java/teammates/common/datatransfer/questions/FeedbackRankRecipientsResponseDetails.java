package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;

public class FeedbackRankRecipientsResponseDetails extends FeedbackRankResponseDetails {
    public int answer;

    public FeedbackRankRecipientsResponseDetails() {
        super(FeedbackQuestionType.RANK_RECIPIENTS);
    }

    @Override
    public String getAnswerString() {
        return Integer.toString(answer);
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        return Integer.toString(answer);
    }

    @Override
    public List<String> validateResponseDetails(FeedbackQuestionAttributes correspondingQuestion) {
        return new ArrayList<>();
    }

}
