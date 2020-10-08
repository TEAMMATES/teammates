package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;

public class FeedbackTextQuestionDetails extends FeedbackQuestionDetails {

    @Nullable
    private Integer recommendedLength;

    private boolean allowRichText;

    public FeedbackTextQuestionDetails() {
        super(FeedbackQuestionType.TEXT);
        recommendedLength = null;
        allowRichText = true;
    }

    public FeedbackTextQuestionDetails(String questionText) {
        super(FeedbackQuestionType.TEXT, questionText);
        recommendedLength = null;
        allowRichText = true;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        return false;
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();
        if (recommendedLength != null && recommendedLength < 1) {
            errors.add(Const.FeedbackQuestion.TEXT_ERROR_INVALID_RECOMMENDED_LENGTH);
        }
        return errors;
    }

    @Override
    public List<String> validateResponsesDetails(List<FeedbackResponseDetails> responses, int numRecipients) {
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

    public Integer getRecommendedLength() {
        return recommendedLength;
    }

    public void setRecommendedLength(Integer recommendedLength) {
        this.recommendedLength = recommendedLength;
    }

    public boolean getAllowRichText() {
        return allowRichText;
    }

    public void setAllowRichText(boolean allowRichText) {
        this.allowRichText = allowRichText;
    }
}
