package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackQuestion;

/**
 * Contains specific structure and processing logic for rank recipients feedback questions.
 */
public class FeedbackRankRecipientsQuestionDetails extends FeedbackRankQuestionDetails {

    public FeedbackRankRecipientsQuestionDetails() {
        this(null);
    }

    public FeedbackRankRecipientsQuestionDetails(String questionText) {
        super(FeedbackQuestionType.RANK_RECIPIENTS, questionText);
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
    public List<String> validateResponsesDetails(List<FeedbackResponseDetails> responses, int numRecipients) {
        List<String> errors = new ArrayList<>();

        boolean isMinOptionsEnabled = minOptionsToBeRanked != Const.POINTS_NO_VALUE;
        boolean isMaxOptionsEnabled = maxOptionsToBeRanked != Const.POINTS_NO_VALUE;

        Set<Integer> responseRank = new HashSet<>();
        for (FeedbackResponseDetails response : responses) {
            FeedbackRankRecipientsResponseDetails details = (FeedbackRankRecipientsResponseDetails) response;

            if (responseRank.contains(details.getAnswer()) && !areDuplicatesAllowed) {
                errors.add("Duplicate rank " + details.getAnswer() + " in question");
            } else if (details.getAnswer() > numRecipients || details.getAnswer() < 1) {
                errors.add("Invalid rank " + details.getAnswer() + " in question");
            }
            responseRank.add(details.getAnswer());
        }
        // if number of options ranked is less than the minimum required trigger this error
        if (isMinOptionsEnabled && responses.size() < minOptionsToBeRanked) {
            errors.add("You must rank at least " + minOptionsToBeRanked + " options.");
        }
        // if number of options ranked is more than the maximum possible trigger this error
        if (isMaxOptionsEnabled && responses.size() > maxOptionsToBeRanked) {
            errors.add("You can rank at most " + maxOptionsToBeRanked + " options.");
        }

        return errors;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestion feedbackQuestion) {
        return "";
    }
}
