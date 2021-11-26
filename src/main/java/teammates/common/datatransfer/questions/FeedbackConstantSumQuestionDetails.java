package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.FieldValidator;

/**
 * Contains specific structure and processing logic for constant sum feedback questions.
 */
public class FeedbackConstantSumQuestionDetails extends FeedbackQuestionDetails {

    static final String QUESTION_TYPE_NAME_OPTION = "Distribute points (among options) question";
    static final String QUESTION_TYPE_NAME_RECIPIENT = "Distribute points (among recipients) question";
    static final int CONST_SUM_MIN_NUM_OF_OPTIONS = 2;
    static final int CONST_SUM_MIN_NUM_OF_POINTS = 1;
    static final String CONST_SUM_ERROR_NOT_ENOUGH_OPTIONS =
            "Too little options for " + QUESTION_TYPE_NAME_OPTION + ". Minimum number of options is: ";
    static final String CONST_SUM_ERROR_DUPLICATE_OPTIONS = "Duplicate options are not allowed.";
    static final String CONST_SUM_ERROR_NOT_ENOUGH_POINTS =
            "Too little points for " + QUESTION_TYPE_NAME_RECIPIENT + ". Minimum number of points is: ";
    static final String CONST_SUM_ERROR_MISMATCH =
            "Please distribute all the points for distribution questions. "
                    + "To skip a distribution question, leave the boxes blank.";
    static final String CONST_SUM_ERROR_NEGATIVE = "Points given must be 0 or more.";
    static final String CONST_SUM_ERROR_UNIQUE = "Every option must be given a different number of points.";
    static final String CONST_SUM_ERROR_SOME_UNIQUE =
            "At least some options must be given a different number of points.";
    static final String CONST_SUM_ANSWER_OPTIONS_NOT_MATCH = "The answers are inconsistent with the options";
    static final String CONST_SUM_ANSWER_RECIPIENT_NOT_MATCH = "The answer is inconsistent with the recipient";

    private int numOfConstSumOptions;
    private List<String> constSumOptions;
    private boolean distributeToRecipients;
    private boolean pointsPerOption;
    private boolean forceUnevenDistribution;
    private String distributePointsFor;
    private int points;

    public FeedbackConstantSumQuestionDetails() {
        this(null);
    }

    public FeedbackConstantSumQuestionDetails(String questionText) {
        super(FeedbackQuestionType.CONSTSUM, questionText);
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

        if (!this.constSumOptions.containsAll(newConstSumDetails.constSumOptions)
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
        if (!distributeToRecipients && constSumOptions.size() < CONST_SUM_MIN_NUM_OF_OPTIONS) {
            errors.add(CONST_SUM_ERROR_NOT_ENOUGH_OPTIONS
                       + CONST_SUM_MIN_NUM_OF_OPTIONS + ".");
        }

        if (points < CONST_SUM_MIN_NUM_OF_POINTS) {
            errors.add(CONST_SUM_ERROR_NOT_ENOUGH_POINTS
                       + CONST_SUM_MIN_NUM_OF_POINTS + ".");
        }

        if (!FieldValidator.areElementsUnique(constSumOptions)) {
            errors.add(CONST_SUM_ERROR_DUPLICATE_OPTIONS);
        }

        return errors;
    }

    @Override
    public List<String> validateResponsesDetails(List<FeedbackResponseDetails> responses, int numRecipients) {
        List<String> errors;

        int numOptions = distributeToRecipients ? numRecipients : constSumOptions.size();
        int totalPoints = pointsPerOption ? points * numOptions : points;

        if (distributeToRecipients) {
            errors = getErrorsForConstSumRecipients(responses, totalPoints);
        } else {
            errors = getErrorsForConstSumOptions(responses, totalPoints);
        }

        return errors;
    }

    private List<String> getErrorsForConstSumOptions(List<FeedbackResponseDetails> responses, int totalPoints) {
        for (FeedbackResponseDetails response : responses) {
            List<String> errors = new ArrayList<>();

            FeedbackConstantSumResponseDetails details = (FeedbackConstantSumResponseDetails) response;

            if (details.getAnswers().size() != constSumOptions.size()) {
                errors.add(CONST_SUM_ANSWER_OPTIONS_NOT_MATCH);
                return errors;
            }

            List<Integer> givenPoints = details.getAnswers();
            errors = getErrors(givenPoints, totalPoints);

            // Return an error if any response is erroneous
            if (!errors.isEmpty()) {
                return errors;
            }
        }
        return new ArrayList<>();
    }

    private List<String> getErrorsForConstSumRecipients(List<FeedbackResponseDetails> responses, int totalPoints) {
        List<Integer> givenPoints = new ArrayList<>();

        for (FeedbackResponseDetails response : responses) {
            FeedbackConstantSumResponseDetails details = (FeedbackConstantSumResponseDetails) response;

            List<String> errors = new ArrayList<>();

            if (details.getAnswers().size() != 1) {
                // Distribute to recipient must have array size one
                errors.add(CONST_SUM_ANSWER_RECIPIENT_NOT_MATCH);
            }

            // Return an error if any response is erroneous
            if (!errors.isEmpty()) {
                return errors;
            }

            int givenPoint = details.getAnswers().get(0);
            givenPoints.add(givenPoint);
        }

        return getErrors(givenPoints, totalPoints);
    }

    private List<String> getErrors(List<Integer> givenPoints, int totalPoints) {
        List<String> errors = new ArrayList<>();

        // Check that all points are >= 0
        int sum = 0;
        for (int i : givenPoints) {
            if (i < 0) {
                errors.add(CONST_SUM_ERROR_NEGATIVE);
                return errors;
            }

            sum += i;
        }

        // Check that points sum up properly
        if (sum != totalPoints) {
            errors.add(CONST_SUM_ERROR_MISMATCH);
            return errors;
        }

        // Check that points are given unevenly for all/at least some options as per the question settings
        Set<Integer> answerSet = new HashSet<>();
        if (distributePointsFor.equals(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY.getDisplayedOption())) {
            boolean hasDifferentPoints = false;
            for (int i : givenPoints) {
                if (!answerSet.isEmpty() && !answerSet.contains(i)) {
                    hasDifferentPoints = true;
                    break;
                }
                answerSet.add(i);
            }

            if (!hasDifferentPoints) {
                errors.add(CONST_SUM_ERROR_SOME_UNIQUE);
                return errors;
            }
        } else if (forceUnevenDistribution || distributePointsFor.equals(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption())) {
            for (int i : givenPoints) {
                if (answerSet.contains(i)) {
                    errors.add(CONST_SUM_ERROR_UNIQUE);
                    return errors;
                }
                answerSet.add(i);
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
