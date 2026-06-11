package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.util.FeedbackConstantSumQuestionDetailsHelper;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Contains specific structure and processing logic for constant sum recipients feedback questions.
 */
public class FeedbackConstantSumRecipientsQuestionDetails extends FeedbackQuestionDetails {

    private boolean pointsPerOption;
    private boolean forceUnevenDistribution;
    private String distributePointsFor;
    private int points;

    public FeedbackConstantSumRecipientsQuestionDetails() {
        this(null);
    }

    public FeedbackConstantSumRecipientsQuestionDetails(String questionText) {
        super(FeedbackQuestionType.CONSTSUM_RECIPIENTS, questionText);
        this.pointsPerOption = false;
        this.points = 100;
        this.forceUnevenDistribution = false;
        this.distributePointsFor = FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption();
    }

    @Override
    public String getQuestionResultStatisticsJson(
            FeedbackQuestion question, UUID currentUserId, SessionResultsBundle bundle) {
        return "";
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackConstantSumRecipientsQuestionDetails newConstSumDetails =
                (FeedbackConstantSumRecipientsQuestionDetails) newDetails;

        if (this.points != newConstSumDetails.points) {
            return true;
        }

        if (this.pointsPerOption != newConstSumDetails.pointsPerOption) {
            return true;
        }

        if (this.forceUnevenDistribution != newConstSumDetails.forceUnevenDistribution) {
            return true;
        }

        return !this.distributePointsFor.equals(newConstSumDetails.distributePointsFor);
    }

    @Override
    public List<String> validateQuestionDetails() {
        return FeedbackConstantSumQuestionDetailsHelper.validateRecipientsQuestionDetails(points);
    }

    @Override
    public List<String> validateResponsesDetails(List<FeedbackResponseDetails> responses, int numRecipients) {
        List<Integer> givenPoints = new ArrayList<>();
        int totalPoints = pointsPerOption ? points * numRecipients : points;

        for (FeedbackResponseDetails response : responses) {
            FeedbackConstantSumRecipientsResponseDetails details =
                    (FeedbackConstantSumRecipientsResponseDetails) response;

            List<String> errors = new ArrayList<>();

            if (details.getAnswers().size() != 1) {
                errors.add(FeedbackConstantSumQuestionDetailsHelper.CONST_SUM_ANSWER_RECIPIENT_NOT_MATCH);
            }

            if (!errors.isEmpty()) {
                return errors;
            }

            int givenPoint = details.getAnswers().get(0);
            givenPoints.add(givenPoint);
        }

        return FeedbackConstantSumQuestionDetailsHelper.getErrors(
                givenPoints, totalPoints, forceUnevenDistribution, distributePointsFor);
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestion feedbackQuestion) {
        return "";
    }

    public boolean isPointsPerOption() {
        return pointsPerOption;
    }

    public void setPointsPerOption(boolean pointsPerOption) {
        this.pointsPerOption = pointsPerOption;
    }

    public boolean isForceUnevenDistribution() {
        return forceUnevenDistribution;
    }

    public void setForceUnevenDistribution(boolean forceUnevenDistribution) {
        this.forceUnevenDistribution = forceUnevenDistribution;
    }

    public String getDistributePointsFor() {
        return distributePointsFor;
    }

    public void setDistributePointsFor(String distributePointsFor) {
        this.distributePointsFor = distributePointsFor;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FeedbackConstantSumRecipientsQuestionDetails other)) {
            return false;
        }
        return getQuestionType() == other.getQuestionType()
                && Objects.equals(getQuestionText(), other.getQuestionText())
                && pointsPerOption == other.pointsPerOption
                && forceUnevenDistribution == other.forceUnevenDistribution
                && points == other.points
                && Objects.equals(distributePointsFor, other.distributePointsFor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getQuestionType(),
                getQuestionText(),
                pointsPerOption,
                forceUnevenDistribution,
                distributePointsFor,
                points);
    }
}
