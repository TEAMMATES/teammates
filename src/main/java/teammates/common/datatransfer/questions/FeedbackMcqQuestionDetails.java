package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;

/**
 * Contains specific structure and processing logic for MCQ feedback questions.
 */
public class FeedbackMcqQuestionDetails extends FeedbackQuestionDetails {

    static final String QUESTION_TYPE_NAME = "Multiple-choice (single answer) question";
    static final int MCQ_MIN_NUM_OF_CHOICES = 2;
    static final String MCQ_ERROR_NOT_ENOUGH_CHOICES =
            "Too little choices for " + QUESTION_TYPE_NAME + ". Minimum number of options is: ";
    static final String MCQ_ERROR_INVALID_OPTION =
            " is not a valid option for the " + QUESTION_TYPE_NAME + ".";
    static final String MCQ_ERROR_INVALID_WEIGHT =
            "The weights for the choices of a " + QUESTION_TYPE_NAME
                    + " must be valid non-negative numbers with precision up to 2 decimal places.";
    static final String MCQ_ERROR_EMPTY_MCQ_OPTION = "The MCQ options cannot be empty";
    static final String MCQ_ERROR_OTHER_CONTENT_NOT_PROVIDED = "No text provided for other option";
    static final String MCQ_ERROR_DUPLICATE_MCQ_OPTION = "The MCQ options cannot be duplicate";

    private boolean hasAssignedWeights;
    private List<Double> mcqWeights;
    private double mcqOtherWeight;
    private int numOfMcqChoices;
    private List<String> mcqChoices;
    private boolean otherEnabled;
    private FeedbackParticipantType generateOptionsFor;

    public FeedbackMcqQuestionDetails() {
        this(null);
    }

    public FeedbackMcqQuestionDetails(String questionText) {
        super(FeedbackQuestionType.MCQ, questionText);
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

            if (numOfMcqChoices < MCQ_MIN_NUM_OF_CHOICES) {
                errors.add(MCQ_ERROR_NOT_ENOUGH_CHOICES
                        + MCQ_MIN_NUM_OF_CHOICES + ".");
            }

            // If there are Empty Mcq options entered trigger this error
            boolean isEmptyMcqOptionEntered = mcqChoices.stream().anyMatch(mcqText -> mcqText.trim().equals(""));
            if (isEmptyMcqOptionEntered) {
                errors.add(MCQ_ERROR_EMPTY_MCQ_OPTION);
            }

            // If weights are enabled, number of choices and weights should be same.
            // If user enters an invalid weight for a valid choice,
            // the mcqChoices.size() will be greater than mcqWeights.size(),
            // in that case, trigger this error.
            if (hasAssignedWeights && mcqChoices.size() != mcqWeights.size()) {
                errors.add(MCQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are not enabled, but weight list is not empty or otherWeight is not 0
            // In that case, trigger this error.
            if (!hasAssignedWeights && (!mcqWeights.isEmpty() || mcqOtherWeight != 0)) {
                errors.add(MCQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are enabled, but other option is disabled, and mcqOtherWeight is not 0
            // In that case, trigger this error.
            if (hasAssignedWeights && !otherEnabled && mcqOtherWeight != 0) {
                errors.add(MCQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are enabled, and any of the weights have negative value,
            // trigger this error.
            if (hasAssignedWeights && !mcqWeights.isEmpty()) {
                mcqWeights.stream()
                        .filter(weight -> weight < 0)
                        .forEach(weight -> errors.add(MCQ_ERROR_INVALID_WEIGHT));
            }

            // If 'Other' option is enabled, and other weight has negative value,
            // trigger this error.
            if (hasAssignedWeights && otherEnabled && mcqOtherWeight < 0) {
                errors.add(MCQ_ERROR_INVALID_WEIGHT);
            }

            //If there are duplicate mcq options trigger this error
            boolean isDuplicateOptionsEntered = mcqChoices.stream().map(String::trim).distinct().count()
                                                != mcqChoices.size();
            if (isDuplicateOptionsEntered) {
                errors.add(MCQ_ERROR_DUPLICATE_MCQ_OPTION);
            }
        }

        return errors;
    }

    @Override
    public List<String> validateResponsesDetails(List<FeedbackResponseDetails> responses, int numRecipients) {
        List<String> errors = new ArrayList<>();

        for (FeedbackResponseDetails response : responses) {
            FeedbackMcqResponseDetails details = (FeedbackMcqResponseDetails) response;

            // if other option is not selected and selected answer is not part of Mcq option list trigger this error.
            if (!details.isOther() && !mcqChoices.contains(details.getAnswerString())) {
                errors.add(details.getAnswerString() + " " + MCQ_ERROR_INVALID_OPTION);
            }

            // if other option is selected but not text is provided trigger this error
            if (details.isOther() && "".equals(details.getAnswerString().trim())) {
                errors.add(MCQ_ERROR_OTHER_CONTENT_NOT_PROVIDED);
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

    public boolean isHasAssignedWeights() {
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
