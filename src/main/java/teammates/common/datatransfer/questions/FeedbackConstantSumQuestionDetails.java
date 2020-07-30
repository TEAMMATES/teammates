package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;

public class FeedbackConstantSumQuestionDetails extends FeedbackQuestionDetails {
    private int numOfConstSumOptions;
    private List<String> constSumOptions;
    private boolean distributeToRecipients;
    private boolean pointsPerOption;
    private boolean forceUnevenDistribution;
    private String distributePointsFor;
    private int points;

    public FeedbackConstantSumQuestionDetails() {
        super(FeedbackQuestionType.CONSTSUM);

        this.numOfConstSumOptions = 0;
        this.constSumOptions = new ArrayList<>();
        this.distributeToRecipients = false;
        this.pointsPerOption = false;
        this.points = 100;
        this.forceUnevenDistribution = false;
        this.distributePointsFor = FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption();
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(
            FeedbackQuestionDetails newDetails) {
        FeedbackConstantSumQuestionDetails newConstSumDetails = (FeedbackConstantSumQuestionDetails) newDetails;

        if (this.numOfConstSumOptions != newConstSumDetails.numOfConstSumOptions
                || !this.constSumOptions.containsAll(newConstSumDetails.constSumOptions)
                || !newConstSumDetails.constSumOptions.containsAll(this.constSumOptions)) {
            return true;
        }

        if (this.distributeToRecipients != newConstSumDetails.distributeToRecipients) {
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

        return !this.distributePointsFor.equals(newConstSumDetails.distributePointsFor);
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();
        if (!distributeToRecipients && numOfConstSumOptions < Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_OPTIONS) {
            errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_NOT_ENOUGH_OPTIONS
                       + Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_OPTIONS + ".");
        }

        if (points < Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_POINTS) {
            errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_NOT_ENOUGH_POINTS
                       + Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_POINTS + ".");
        }

        if (!FieldValidator.areElementsUnique(constSumOptions)) {
            errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_DUPLICATE_OPTIONS);
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

    public int getNumOfConstSumOptions() {
        return numOfConstSumOptions;
    }

    public void setNumOfConstSumOptions(int numOfConstSumOptions) {
        this.numOfConstSumOptions = numOfConstSumOptions;
    }

    public List<String> getConstSumOptions() {
        return constSumOptions;
    }

    public void setConstSumOptions(List<String> constSumOptions) {
        this.constSumOptions = constSumOptions;
    }

    public boolean isDistributeToRecipients() {
        return distributeToRecipients;
    }

    public void setDistributeToRecipients(boolean distributeToRecipients) {
        this.distributeToRecipients = distributeToRecipients;
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
}
