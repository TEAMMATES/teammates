package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;

public class FeedbackRankRecipientsQuestionDetails extends FeedbackRankQuestionDetails {

    public FeedbackRankRecipientsQuestionDetails() {
        super(FeedbackQuestionType.RANK_RECIPIENTS);
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        return false;
    }

    @Override
    public List<String> validateQuestionDetails() {
        return new ArrayList<>();
    }

    @Override
    public List<String> validateResponsesDetails(List<FeedbackResponseDetails> responses) {
        return new ArrayList<>();
    }

    @Override
    public boolean isFeedbackParticipantCommentsOnResponsesAllowed() {
        return false;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }
}
