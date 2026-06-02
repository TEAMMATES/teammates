package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.util.FeedbackConstantSumQuestionDetailsHelper;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Contains specific structure and processing logic for constant sum options feedback questions.
 */
public class FeedbackConstantSumOptionsQuestionDetails extends FeedbackQuestionDetails {

    private List<String> constSumOptions;
    private boolean pointsPerOption;
    private boolean forceUnevenDistribution;
    private String distributePointsFor;
    private int points;
    @Nullable
    private Integer minPoint;
    @Nullable
    private Integer maxPoint;

    public FeedbackConstantSumOptionsQuestionDetails() {
        this(null);
    }

    public FeedbackConstantSumOptionsQuestionDetails(String questionText) {
        super(FeedbackQuestionType.CONSTSUM_OPTIONS, questionText);
        this.constSumOptions = new ArrayList<>();
        this.pointsPerOption = false;
        this.points = 100;
        this.forceUnevenDistribution = false;
        this.distributePointsFor = FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption();
        this.maxPoint = null;
        this.minPoint = null;
    }

    @Override
    public String getQuestionResultStatisticsJson(
            FeedbackQuestion question, String studentEmail, SessionResultsBundle bundle) {
        return "";
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackConstantSumOptionsQuestionDetails newConstSumDetails =
                (FeedbackConstantSumOptionsQuestionDetails) newDetails;

        if (!this.constSumOptions.containsAll(newConstSumDetails.constSumOptions)
                || !newConstSumDetails.constSumOptions.containsAll(this.constSumOptions)) {
            return true;
        }

        if (this.points != newConstSumDetails.points) {
            return true;
        }

        if (this.pointsPerOption != newConstSumDetails.pointsPerOption) {
            return true;
        }

        if (this.forceUnevenDistribution != newConstSumDetails.forceUnevenDistribution) {
            return true;
        }

        if (!Objects.equals(this.maxPoint, newConstSumDetails.maxPoint)) {
            return true;
        }

        if (!Objects.equals(this.minPoint, newConstSumDetails.minPoint)) {
            return true;
        }

        return !this.distributePointsFor.equals(newConstSumDetails.distributePointsFor);
    }

    @Override
    public List<String> validateQuestionDetails() {
        int totalPoints = pointsPerOption ? points * constSumOptions.size() : points;
        return FeedbackConstantSumQuestionDetailsHelper.validateOptionsQuestionDetails(
                constSumOptions, totalPoints, minPoint, maxPoint);
    }

    @Override
    public List<String> validateResponsesDetails(List<FeedbackResponseDetails> responses, int numRecipients) {
        int totalPoints = pointsPerOption ? points * constSumOptions.size() : points;

        for (FeedbackResponseDetails response : responses) {
            List<String> errors = new ArrayList<>();

            FeedbackConstantSumOptionsResponseDetails details = (FeedbackConstantSumOptionsResponseDetails) response;

            if (details.getAnswers().size() != constSumOptions.size()) {
                errors.add(FeedbackConstantSumQuestionDetailsHelper.CONST_SUM_ANSWER_OPTIONS_NOT_MATCH);
                return errors;
            }

            errors = FeedbackConstantSumQuestionDetailsHelper.getMinMaxPointErrors(
                    details.getAnswers(), minPoint, maxPoint);
            if (!errors.isEmpty()) {
                return errors;
            }

            errors = FeedbackConstantSumQuestionDetailsHelper.getErrors(
                    details.getAnswers(), totalPoints, forceUnevenDistribution, distributePointsFor);
            if (!errors.isEmpty()) {
                return errors;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestion feedbackQuestion) {
        return "";
    }

    public int getNumOfConstSumOptions() {
        return constSumOptions.size();
    }

    public List<String> getConstSumOptions() {
        return constSumOptions;
    }

    public void setConstSumOptions(List<String> constSumOptions) {
        this.constSumOptions = constSumOptions;
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

    public int getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(int minPoint) {
        this.minPoint = minPoint;
    }

    public int getMaxPoint() {
        return maxPoint;
    }

    public void setMaxPoint(int maxPoint) {
        this.maxPoint = maxPoint;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FeedbackConstantSumOptionsQuestionDetails other)) {
            return false;
        }
        return getQuestionType() == other.getQuestionType()
                && Objects.equals(getQuestionText(), other.getQuestionText())
                && pointsPerOption == other.pointsPerOption
                && forceUnevenDistribution == other.forceUnevenDistribution
                && points == other.points
                && Objects.equals(constSumOptions, other.constSumOptions)
                && Objects.equals(distributePointsFor, other.distributePointsFor)
                && Objects.equals(minPoint, other.minPoint)
                && Objects.equals(maxPoint, other.maxPoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getQuestionType(),
                getQuestionText(),
                constSumOptions,
                pointsPerOption,
                forceUnevenDistribution,
                distributePointsFor,
                points,
                minPoint,
                maxPoint);
    }
}
