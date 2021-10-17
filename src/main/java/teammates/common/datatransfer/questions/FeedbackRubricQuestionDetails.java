package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;

/**
 * Contains specific structure and processing logic for rubric feedback questions.
 */
public class FeedbackRubricQuestionDetails extends FeedbackQuestionDetails {

    static final String QUESTION_TYPE_NAME = "Rubric question";
    static final int RUBRIC_ANSWER_NOT_CHOSEN = -1;
    static final int RUBRIC_MIN_NUM_OF_CHOICES = 2;
    static final String RUBRIC_ERROR_NOT_ENOUGH_CHOICES =
            "Too little choices for " + QUESTION_TYPE_NAME + ". Minimum number of options is: ";
    static final int RUBRIC_MIN_NUM_OF_SUB_QUESTIONS = 1;
    static final String RUBRIC_ERROR_NOT_ENOUGH_SUB_QUESTIONS =
            "Too little sub-questions for " + QUESTION_TYPE_NAME + ". " + "Minimum number of sub-questions is: ";
    static final String RUBRIC_ERROR_DESC_INVALID_SIZE =
            "Invalid number of descriptions for " + QUESTION_TYPE_NAME;
    static final String RUBRIC_ERROR_EMPTY_SUB_QUESTION =
            "Sub-questions for " + QUESTION_TYPE_NAME + " cannot be empty.";
    static final String RUBRIC_ERROR_INVALID_WEIGHT =
            "The weights for the choices of each sub-question of a " + QUESTION_TYPE_NAME
                    + " must be valid numbers with precision up to 2 decimal places.";
    static final String RUBRIC_EMPTY_ANSWER = "Empty answer.";
    static final String RUBRIC_INVALID_ANSWER = "The answer for the rubric question is not valid.";

    private boolean hasAssignedWeights;
    private List<List<Double>> rubricWeightsForEachCell;
    private List<String> rubricChoices;
    private List<String> rubricSubQuestions;
    private List<List<String>> rubricDescriptions;

    public FeedbackRubricQuestionDetails() {
        this(null);
    }

    public FeedbackRubricQuestionDetails(String questionText) {
        super(FeedbackQuestionType.RUBRIC, questionText);
        this.hasAssignedWeights = false;
        this.rubricChoices = new ArrayList<>();
        this.rubricSubQuestions = new ArrayList<>();
        this.rubricDescriptions = new ArrayList<>();
        this.rubricWeightsForEachCell = new ArrayList<>();
    }

    /**
     * Checks if the dimensions of rubricDescription is valid according
     * to size of rubricChoices and size of rubricSubQuestions .
     */
    private boolean isValidDescriptionSize() {
        if (rubricDescriptions.size() != rubricSubQuestions.size()) {
            return false;
        }
        for (List<String> rubricDescription : rubricDescriptions) {
            if (rubricDescription.size() != rubricChoices.size()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the dimensions of rubricWeightsForEachCell is valid according
     * to size of rubricChoices and size of rubricSubQuestions .
     */
    private boolean isValidWeightSize() {
        if (rubricWeightsForEachCell.size() != rubricSubQuestions.size()) {
            return false;
        }
        return rubricWeightsForEachCell.stream().allMatch(x -> x.size() == rubricChoices.size());
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
        return this.rubricSubQuestions.size() != newRubricDetails.rubricSubQuestions.size()
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
        //    weight size should be equal to (size of rubricSubQuestions  * size of rubricChoices).

        List<String> errors = new ArrayList<>();

        if (!isValidDescriptionSize()) {
            // This should not happen.
            // Set descriptions to empty if the sizes are invalid when extracting question details.
            errors.add(RUBRIC_ERROR_DESC_INVALID_SIZE);
        }

        if (rubricChoices.size() < RUBRIC_MIN_NUM_OF_CHOICES) {
            errors.add(RUBRIC_ERROR_NOT_ENOUGH_CHOICES
                       + RUBRIC_MIN_NUM_OF_CHOICES);
        }

        if (this.rubricSubQuestions.size() < RUBRIC_MIN_NUM_OF_SUB_QUESTIONS) {
            errors.add(RUBRIC_ERROR_NOT_ENOUGH_SUB_QUESTIONS
                       + RUBRIC_MIN_NUM_OF_SUB_QUESTIONS);
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
                errors.add(RUBRIC_ERROR_EMPTY_SUB_QUESTION);
                break;
            }
        }

        if (hasAssignedWeights && !isValidWeightSize()) {
            errors.add(RUBRIC_ERROR_INVALID_WEIGHT);
        }

        return errors;
    }

    @Override
    public List<String> validateResponsesDetails(List<FeedbackResponseDetails> responses, int numRecipients) {
        List<String> errors = new ArrayList<>();

        for (FeedbackResponseDetails response : responses) {
            FeedbackRubricResponseDetails details = (FeedbackRubricResponseDetails) response;
            if (details.getAnswer().isEmpty()) {
                errors.add(RUBRIC_EMPTY_ANSWER);
            }

            if (details.getAnswer().size() != rubricSubQuestions.size()) {
                errors.add(RUBRIC_INVALID_ANSWER);
            }

            if (details.getAnswer().stream().anyMatch(choice ->
                    choice != RUBRIC_ANSWER_NOT_CHOSEN
                            && (choice < 0 || choice >= rubricChoices.size()))) {
                errors.add(RUBRIC_INVALID_ANSWER);
            }

            if (details.getAnswer().stream().allMatch(choice -> choice == RUBRIC_ANSWER_NOT_CHOSEN)) {
                errors.add(RUBRIC_INVALID_ANSWER);
            }
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

    public boolean isHasAssignedWeights() {
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

    public List<String> getRubricChoices() {
        return rubricChoices;
    }

    public void setRubricChoices(List<String> rubricChoices) {
        this.rubricChoices = rubricChoices;
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
