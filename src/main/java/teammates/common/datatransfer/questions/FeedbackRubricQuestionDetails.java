package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;

public class FeedbackRubricQuestionDetails extends FeedbackQuestionDetails {

    private boolean hasAssignedWeights;
    private List<List<Double>> rubricWeightsForEachCell;
    private int numOfRubricChoices;
    private List<String> rubricChoices;
    private int numOfRubricSubQuestions;
    private List<String> rubricSubQuestions;
    private List<List<String>> rubricDescriptions;

    public FeedbackRubricQuestionDetails() {
        super(FeedbackQuestionType.RUBRIC);

        this.hasAssignedWeights = false;
        this.numOfRubricChoices = 0;
        this.rubricChoices = new ArrayList<>();
        this.numOfRubricSubQuestions = 0;
        this.rubricSubQuestions = new ArrayList<>();
        this.rubricDescriptions = new ArrayList<>();
        this.rubricWeightsForEachCell = new ArrayList<>();
    }

    /**
     * Checks if the dimensions of rubricDescription is valid according
     * to numOfRubricSubQuestions and numOfRubricChoices.
     */
    private boolean isValidDescriptionSize() {
        if (rubricDescriptions.size() != numOfRubricSubQuestions) {
            return false;
        }
        for (List<String> rubricDescription : rubricDescriptions) {
            if (rubricDescription.size() != numOfRubricChoices) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the dimensions of rubricWeightsForEachCell is valid according
     * to numOfRubricSubQuestions and numOfRubricChoices.
     */
    private boolean isValidWeightSize() {
        if (rubricWeightsForEachCell.size() != numOfRubricSubQuestions) {
            return false;
        }
        return rubricWeightsForEachCell.stream().allMatch(x -> x.size() == numOfRubricChoices);
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackRubricQuestionDetails newRubricDetails = (FeedbackRubricQuestionDetails) newDetails;
        // TODO: need to check for exact match.

        // Responses require deletion if choices change
        if (!this.rubricChoices.equals(newRubricDetails.rubricChoices)) {
            return true;
        }

        // Responses require deletion if sub-questions change
        return this.numOfRubricSubQuestions != newRubricDetails.numOfRubricSubQuestions
            || !this.rubricSubQuestions.containsAll(newRubricDetails.rubricSubQuestions)
            || !newRubricDetails.rubricSubQuestions.containsAll(this.rubricSubQuestions);
    }

    @Override
    public List<String> validateQuestionDetails() {
        // For rubric questions,
        // 1) Description size should be valid
        // 2) At least 2 choices
        // 3) At least 1 sub-question
        // 4) Choices and sub-questions should not be empty
        // 5) Weights must be assigned to all cells if weights are assigned, which means
        //    weight size should be equal to (numOfRubricChoices * numOfRubricSubQuestions).

        List<String> errors = new ArrayList<>();

        if (!isValidDescriptionSize()) {
            // This should not happen.
            // Set descriptions to empty if the sizes are invalid when extracting question details.
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_DESC_INVALID_SIZE);
        }

        if (numOfRubricChoices < Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_CHOICES) {
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_NOT_ENOUGH_CHOICES
                       + Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_CHOICES);
        }

        if (this.numOfRubricSubQuestions < Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_SUB_QUESTIONS) {
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_NOT_ENOUGH_SUB_QUESTIONS
                       + Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_SUB_QUESTIONS);
        }

        //Rubric choices are now allowed to be empty.
        /*
        for (String choice : this.rubricChoices) {
            if (choice.trim().isEmpty()) {
                errors.add(ERROR_RUBRIC_EMPTY_CHOICE);
                break;
            }
        }
        */

        for (String subQn : rubricSubQuestions) {
            if (subQn.trim().isEmpty()) {
                errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_EMPTY_SUB_QUESTION);
                break;
            }
        }

        if (hasAssignedWeights && !isValidWeightSize()) {
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_INVALID_WEIGHT);
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

    /**
     * Returns a list of rubric weights if the weights are assigned,
     * otherwise returns an empty list.
     */
    public List<List<Double>> getRubricWeights() {
        if (hasAssignedWeights) {
            return rubricWeightsForEachCell;
        }

        return new ArrayList<>();
    }

    public boolean hasAssignedWeights() {
        return hasAssignedWeights;
    }

    public void setHasAssignedWeights(boolean hasAssignedWeights) {
        this.hasAssignedWeights = hasAssignedWeights;
    }

    public List<List<Double>> getRubricWeightsForEachCell() {
        return rubricWeightsForEachCell;
    }

    public void setRubricWeightsForEachCell(List<List<Double>> rubricWeightsForEachCell) {
        this.rubricWeightsForEachCell = rubricWeightsForEachCell;
    }

    public int getNumOfRubricChoices() {
        return numOfRubricChoices;
    }

    public void setNumOfRubricChoices(int numOfRubricChoices) {
        this.numOfRubricChoices = numOfRubricChoices;
    }

    public List<String> getRubricChoices() {
        return rubricChoices;
    }

    public void setRubricChoices(List<String> rubricChoices) {
        this.rubricChoices = rubricChoices;
    }

    public int getNumOfRubricSubQuestions() {
        return numOfRubricSubQuestions;
    }

    public void setNumOfRubricSubQuestions(int numOfRubricSubQuestions) {
        this.numOfRubricSubQuestions = numOfRubricSubQuestions;
    }

    public List<String> getRubricSubQuestions() {
        return rubricSubQuestions;
    }

    public void setRubricSubQuestions(List<String> rubricSubQuestions) {
        this.rubricSubQuestions = rubricSubQuestions;
    }

    public List<List<String>> getRubricDescriptions() {
        return rubricDescriptions;
    }

    public void setRubricDescriptions(List<List<String>> rubricDescriptions) {
        this.rubricDescriptions = rubricDescriptions;
    }
}
