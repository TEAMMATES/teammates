package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;

public class FeedbackMsqQuestionDetails extends FeedbackQuestionDetails {
    private List<String> msqChoices;
    private boolean otherEnabled;
    private boolean hasAssignedWeights;
    private List<Double> msqWeights;
    private double msqOtherWeight;
    private FeedbackParticipantType generateOptionsFor;
    private int maxSelectableChoices;
    private int minSelectableChoices;
    private transient int numOfGeneratedMsqChoices;

    public FeedbackMsqQuestionDetails() {
        super(FeedbackQuestionType.MSQ);

        this.msqChoices = new ArrayList<>();
        this.otherEnabled = false;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
        this.maxSelectableChoices = Integer.MIN_VALUE;
        this.minSelectableChoices = Integer.MIN_VALUE;
        this.hasAssignedWeights = false;
        this.msqWeights = new ArrayList<>();
        this.msqOtherWeight = 0;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackMsqQuestionDetails newMsqDetails = (FeedbackMsqQuestionDetails) newDetails;

        if (this.msqChoices.size() != newMsqDetails.msqChoices.size()
                || !this.msqChoices.containsAll(newMsqDetails.msqChoices)
                || !newMsqDetails.msqChoices.containsAll(this.msqChoices)) {
            return true;
        }

        if (this.generateOptionsFor != newMsqDetails.generateOptionsFor) {
            return true;
        }

        if (this.maxSelectableChoices == Integer.MIN_VALUE && newMsqDetails.maxSelectableChoices != Integer.MIN_VALUE) {
            // Delete responses if max selectable restriction is newly added
            return true;
        }

        if (this.minSelectableChoices == Integer.MIN_VALUE && newMsqDetails.minSelectableChoices != Integer.MIN_VALUE) {
            // Delete responses if min selectable restriction is newly added
            return true;
        }

        if (this.minSelectableChoices != Integer.MIN_VALUE && newMsqDetails.minSelectableChoices != Integer.MIN_VALUE
                && this.minSelectableChoices < newMsqDetails.minSelectableChoices) {
            // A more strict min selectable choices restriction is placed
            return true;
        }

        if (this.maxSelectableChoices != Integer.MIN_VALUE && newMsqDetails.maxSelectableChoices != Integer.MIN_VALUE
                && this.maxSelectableChoices > newMsqDetails.maxSelectableChoices) {
            // A more strict max selectable choices restriction is placed
            return true;
        }

        return this.otherEnabled != newMsqDetails.otherEnabled;
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();
        if (generateOptionsFor == FeedbackParticipantType.NONE) {

            if (msqChoices.size() < Const.FeedbackQuestion.MSQ_MIN_NUM_OF_CHOICES) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_NOT_ENOUGH_CHOICES
                           + Const.FeedbackQuestion.MSQ_MIN_NUM_OF_CHOICES + ".");
            }

            // If there are Empty Msq options entered trigger this error
            boolean isEmptyMsqOptionEntered = msqChoices.stream().anyMatch(msqText -> msqText.trim().equals(""));
            if (isEmptyMsqOptionEntered) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_EMPTY_MSQ_OPTION);
            }

            // If weights are enabled, number of choices and weights should be same.
            // If a user enters an invalid weight for a valid choice,
            // the msqChoices.size() will be greater than msqWeights.size(), in that case
            // trigger this error.
            if (hasAssignedWeights && msqChoices.size() != msqWeights.size()) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are not enabled, but weight list is not empty or otherWeight is not 0
            // In that case, trigger this error.
            if (!hasAssignedWeights && (!msqWeights.isEmpty() || msqOtherWeight != 0)) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT);
            }

            // If weight is enabled, but other option is disabled, and msqOtherWeight is not 0
            // In that case, trigger this error.
            if (hasAssignedWeights && !otherEnabled && msqOtherWeight != 0) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are negative, trigger this error.
            if (hasAssignedWeights && !msqWeights.isEmpty()) {
                msqWeights.stream()
                        .filter(weight -> weight < 0)
                        .forEach(weight -> errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT));
            }

            // If 'Other' option is enabled, and other weight has negative value,
            // trigger this error.
            if (hasAssignedWeights && otherEnabled && msqOtherWeight < 0) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT);
            }

            //If there are duplicate mcq options trigger this error
            boolean isDuplicateOptionsEntered = msqChoices.stream().map(String::trim).distinct().count()
                                                != msqChoices.size();
            if (isDuplicateOptionsEntered) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_DUPLICATE_MSQ_OPTION);
            }
        }

        boolean isMaxSelectableChoicesEnabled = maxSelectableChoices != Integer.MIN_VALUE;
        boolean isMinSelectableChoicesEnabled = minSelectableChoices != Integer.MIN_VALUE;

        int numOfMsqChoices = numOfGeneratedMsqChoices;
        if (generateOptionsFor == FeedbackParticipantType.NONE) {
            numOfMsqChoices = msqChoices.size() + (otherEnabled ? 1 : 0);
        }
        if (isMaxSelectableChoicesEnabled) {
            if (numOfMsqChoices < maxSelectableChoices) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_MAX_SELECTABLE_EXCEEDED_TOTAL);
            } else if (maxSelectableChoices < 2) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_MIN_FOR_MAX_SELECTABLE_CHOICES);
            }
        }

        if (isMinSelectableChoicesEnabled) {
            if (minSelectableChoices < 1) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_MIN_FOR_MIN_SELECTABLE_CHOICES);
            }
            if (minSelectableChoices > numOfMsqChoices) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_MIN_SELECTABLE_MORE_THAN_NUM_CHOICES);
            }
        }

        if (isMaxSelectableChoicesEnabled && isMinSelectableChoicesEnabled
                && minSelectableChoices > maxSelectableChoices) {
            errors.add(Const.FeedbackQuestion.MSQ_ERROR_MIN_SELECTABLE_EXCEEDED_MAX_SELECTABLE);
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

    public List<String> getMsqChoices() {
        return msqChoices;
    }

    public void setMsqChoices(List<String> msqChoices) {
        this.msqChoices = msqChoices;
    }

    public boolean isOtherEnabled() {
        return otherEnabled;
    }

    public void setOtherEnabled(boolean otherEnabled) {
        this.otherEnabled = otherEnabled;
    }

    public boolean hasAssignedWeights() {
        return hasAssignedWeights;
    }

    public void setHasAssignedWeights(boolean hasAssignedWeights) {
        this.hasAssignedWeights = hasAssignedWeights;
    }

    public List<Double> getMsqWeights() {
        return msqWeights;
    }

    public void setMsqWeights(List<Double> msqWeights) {
        this.msqWeights = msqWeights;
    }

    public double getMsqOtherWeight() {
        return msqOtherWeight;
    }

    public void setMsqOtherWeight(double msqOtherWeight) {
        this.msqOtherWeight = msqOtherWeight;
    }

    public FeedbackParticipantType getGenerateOptionsFor() {
        return generateOptionsFor;
    }

    public void setGenerateOptionsFor(FeedbackParticipantType generateOptionsFor) {
        this.generateOptionsFor = generateOptionsFor;
    }

    public int getMaxSelectableChoices() {
        return maxSelectableChoices;
    }

    public void setMaxSelectableChoices(int maxSelectableChoices) {
        this.maxSelectableChoices = maxSelectableChoices;
    }

    public int getMinSelectableChoices() {
        return minSelectableChoices;
    }

    public void setMinSelectableChoices(int minSelectableChoices) {
        this.minSelectableChoices = minSelectableChoices;
    }

    public int getNumOfGeneratedMsqChoices() {
        return numOfGeneratedMsqChoices;
    }

    public void setNumOfGeneratedMsqChoices(int numOfGeneratedMsqChoices) {
        this.numOfGeneratedMsqChoices = numOfGeneratedMsqChoices;
    }
}
