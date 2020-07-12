package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;

public class FeedbackMcqQuestionDetails extends FeedbackQuestionDetails {

    private boolean hasAssignedWeights;
    private List<Double> mcqWeights;
    private double mcqOtherWeight;
    private int numOfMcqChoices;
    private List<String> mcqChoices;
    private boolean otherEnabled;
    private FeedbackParticipantType generateOptionsFor;

    public FeedbackMcqQuestionDetails() {
        super(FeedbackQuestionType.MCQ);

        this.hasAssignedWeights = false;
        this.mcqWeights = new ArrayList<>();
        this.numOfMcqChoices = 0;
        this.mcqChoices = new ArrayList<>();
        this.otherEnabled = false;
        this.mcqOtherWeight = 0;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackMcqQuestionDetails newMcqDetails = (FeedbackMcqQuestionDetails) newDetails;

        if (this.numOfMcqChoices != newMcqDetails.numOfMcqChoices
                || !this.mcqChoices.containsAll(newMcqDetails.mcqChoices)
                || !newMcqDetails.mcqChoices.containsAll(this.mcqChoices)) {
            return true;
        }

        if (this.generateOptionsFor != newMcqDetails.generateOptionsFor) {
            return true;
        }

        return this.otherEnabled != newMcqDetails.otherEnabled;
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();
        if (generateOptionsFor == FeedbackParticipantType.NONE) {

            if (numOfMcqChoices < Const.FeedbackQuestion.MCQ_MIN_NUM_OF_CHOICES) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_NOT_ENOUGH_CHOICES
                        + Const.FeedbackQuestion.MCQ_MIN_NUM_OF_CHOICES + ".");
            }

            // If there are Empty Mcq options entered trigger this error
            boolean isEmptyMcqOptionEntered = mcqChoices.stream().anyMatch(mcqText -> mcqText.trim().equals(""));
            if (isEmptyMcqOptionEntered) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_EMPTY_MCQ_OPTION);
            }

            // If weights are enabled, number of choices and weights should be same.
            // If user enters an invalid weight for a valid choice,
            // the mcqChoices.size() will be greater than mcqWeights.size(),
            // in that case, trigger this error.
            if (hasAssignedWeights && mcqChoices.size() != mcqWeights.size()) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are not enabled, but weight list is not empty or otherWeight is not 0
            // In that case, trigger this error.
            if (!hasAssignedWeights && (!mcqWeights.isEmpty() || mcqOtherWeight != 0)) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are enabled, but other option is disabled, and mcqOtherWeight is not 0
            // In that case, trigger this error.
            if (hasAssignedWeights && !otherEnabled && mcqOtherWeight != 0) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are enabled, and any of the weights have negative value,
            // trigger this error.
            if (hasAssignedWeights && !mcqWeights.isEmpty()) {
                mcqWeights.stream()
                        .filter(weight -> weight < 0)
                        .forEach(weight -> errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT));
            }

            // If 'Other' option is enabled, and other weight has negative value,
            // trigger this error.
            if (hasAssignedWeights && otherEnabled && mcqOtherWeight < 0) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT);
            }

            //If there are duplicate mcq options trigger this error
            boolean isDuplicateOptionsEntered = mcqChoices.stream().map(String::trim).distinct().count()
                                                != mcqChoices.size();
            if (isDuplicateOptionsEntered) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_DUPLICATE_MCQ_OPTION);
            }
        }

        return errors;
    }

    @Override
    public boolean isFeedbackParticipantCommentsOnResponsesAllowed() {
        return true;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

    public boolean hasAssignedWeights() {
        return hasAssignedWeights;
    }

    public void setHasAssignedWeights(boolean hasAssignedWeights) {
        this.hasAssignedWeights = hasAssignedWeights;
    }

    public List<Double> getMcqWeights() {
        return mcqWeights;
    }

    public void setMcqWeights(List<Double> mcqWeights) {
        this.mcqWeights = mcqWeights;
    }

    public double getMcqOtherWeight() {
        return mcqOtherWeight;
    }

    public void setMcqOtherWeight(double mcqOtherWeight) {
        this.mcqOtherWeight = mcqOtherWeight;
    }

    public int getNumOfMcqChoices() {
        return numOfMcqChoices;
    }

    public void setNumOfMcqChoices(int numOfMcqChoices) {
        this.numOfMcqChoices = numOfMcqChoices;
    }

    public List<String> getMcqChoices() {
        return mcqChoices;
    }

    public void setMcqChoices(List<String> mcqChoices) {
        this.mcqChoices = mcqChoices;
    }

    public boolean isOtherEnabled() {
        return otherEnabled;
    }

    public void setOtherEnabled(boolean otherEnabled) {
        this.otherEnabled = otherEnabled;
    }

    public FeedbackParticipantType getGenerateOptionsFor() {
        return generateOptionsFor;
    }

    public void setGenerateOptionsFor(FeedbackParticipantType generateOptionsFor) {
        this.generateOptionsFor = generateOptionsFor;
    }
}
