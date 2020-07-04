package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;

public class FeedbackTextQuestionDetails extends FeedbackQuestionDetails {

    private int recommendedLength;

    public FeedbackTextQuestionDetails() {
        super(FeedbackQuestionType.TEXT);
        recommendedLength = 0;
    }

    public FeedbackTextQuestionDetails(String questionText) {
        super(FeedbackQuestionType.TEXT, questionText);
        recommendedLength = 0;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        return false;
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();
        if (recommendedLength < 0) {
            errors.add(Const.FeedbackQuestion.TEXT_ERROR_INVALID_RECOMMENDED_LENGTH);
        }
        return errors;
    }

    @Override
    public boolean isFeedbackParticipantCommentsOnResponsesAllowed() {
        return false;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

    public int getRecommendedLength() {
        return recommendedLength;
    }

    public void setRecommendedLength(int recommendedLength) {
        this.recommendedLength = recommendedLength;
    }
}
