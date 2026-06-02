package teammates.common.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.common.datatransfer.questions.FeedbackConstantSumDistributePointsType;

/**
 * Shared validation helpers for constant sum question details.
 */
public final class FeedbackConstantSumQuestionDetailsHelper {

    public static final String QUESTION_TYPE_NAME_OPTION = "Distribute points (among options) question";
    public static final String QUESTION_TYPE_NAME_RECIPIENT = "Distribute points (among recipients) question";
    public static final int CONST_SUM_MIN_NUM_OF_OPTIONS = 2;
    public static final int CONST_SUM_MIN_NUM_OF_POINTS = 1;
    public static final String CONST_SUM_ERROR_NOT_ENOUGH_OPTIONS =
            "Too little options for " + QUESTION_TYPE_NAME_OPTION + ". Minimum number of options is: ";
    public static final String CONST_SUM_ERROR_DUPLICATE_OPTIONS = "Duplicate options are not allowed.";
    public static final String CONST_SUM_ERROR_NOT_ENOUGH_POINTS =
            "Too little points for " + QUESTION_TYPE_NAME_RECIPIENT + ". Minimum number of points is: ";
    public static final String CONST_SUM_ERROR_MISMATCH =
            "Please distribute all the points for distribution questions. "
                    + "To skip a distribution question, leave the boxes blank.";
    public static final String CONST_SUM_ERROR_NEGATIVE = "Points cannot be negative.";
    public static final String CONST_SUM_ERROR_UNIQUE = "Every option must be given a different number of points.";
    public static final String CONST_SUM_ERROR_SOME_UNIQUE =
            "At least some options must be given a different number of points.";
    public static final String CONST_SUM_ANSWER_OPTIONS_NOT_MATCH = "The answers are inconsistent with the options";
    public static final String CONST_SUM_ANSWER_RECIPIENT_NOT_MATCH = "The answer is inconsistent with the recipient";
    public static final String CONST_SUM_TEMPLATE_NEGATIVE = "%s cannot be negative.";
    public static final String CONST_SUM_TEMPLATE_EXCEEDS_POINTS =
            "%s cannot be greater than the total points distributed: %s.";
    public static final String CONST_SUM_ERROR_MAX_POINT_BELOW_LOWER_BOUND =
            "To ensure total distribution of points, the maximum number of points cannot be smaller than: ";
    public static final String CONST_SUM_ERROR_MIN_POINT_ABOVE_UPPER_BOUND =
            "To ensure total distribution of points, the minimum number of points cannot be larger than: ";
    public static final String CONST_SUM_ERROR_MIN_GREATER_THAN_MAX =
            "Minimum number of points cannot be greater than the maximum number of points";
    public static final String CONST_SUM_ANSWER_BELOW_MIN =
            "An answer cannot be smaller than the minimum number of points: ";
    public static final String CONST_SUM_ANSWER_ABOVE_MAX =
            "An answer cannot be greater than the maximum number of points: ";
    public static final String MAX_POINT_STRING = "Maximum number of points";
    public static final String MIN_POINT_STRING = "Minimum number of points";

    private FeedbackConstantSumQuestionDetailsHelper() {
        // utility class
    }

    /**
     * Validates details for constant sum options questions.
     */
    public static List<String> validateOptionsQuestionDetails(
            List<String> constSumOptions, int points, Integer minPoint, Integer maxPoint) {
        List<String> errors = new ArrayList<>();
        if (constSumOptions.size() < CONST_SUM_MIN_NUM_OF_OPTIONS) {
            errors.add(CONST_SUM_ERROR_NOT_ENOUGH_OPTIONS + CONST_SUM_MIN_NUM_OF_OPTIONS + ".");
        }

        validateMinimumPoints(errors, points);

        if (!FieldValidator.areElementsUnique(constSumOptions)) {
            errors.add(CONST_SUM_ERROR_DUPLICATE_OPTIONS);
        }

        int totalPoints = points;
        double evenPointDistribution = 1.0d * totalPoints / constSumOptions.size();

        if (minPoint != null) {
            commonBoundaryValidation(errors, minPoint, totalPoints, MIN_POINT_STRING);

            int upperBound = (int) Math.floor(evenPointDistribution);
            if (minPoint > upperBound) {
                errors.add(CONST_SUM_ERROR_MIN_POINT_ABOVE_UPPER_BOUND + upperBound);
            }
        }

        if (maxPoint != null) {
            commonBoundaryValidation(errors, maxPoint, totalPoints, MAX_POINT_STRING);

            int lowerBound = (int) Math.ceil(evenPointDistribution);
            if (maxPoint < lowerBound) {
                errors.add(CONST_SUM_ERROR_MAX_POINT_BELOW_LOWER_BOUND + lowerBound);
            }
        }

        if (maxPoint != null && minPoint != null && minPoint > maxPoint) {
            errors.add(CONST_SUM_ERROR_MIN_GREATER_THAN_MAX);
        }

        return errors;
    }

    /**
     * Validates details for constant sum recipients questions.
     */
    public static List<String> validateRecipientsQuestionDetails(int points) {
        List<String> errors = new ArrayList<>();
        validateMinimumPoints(errors, points);
        return errors;
    }

    /**
     * Returns errors for answers outside the configured min/max point boundaries.
     */
    public static List<String> getMinMaxPointErrors(List<Integer> answers, Integer minPoint, Integer maxPoint) {
        List<String> errors = new ArrayList<>();

        for (int answer : answers) {
            if (minPoint != null && answer < minPoint) {
                errors.add(CONST_SUM_ANSWER_BELOW_MIN + minPoint);
            } else if (maxPoint != null && answer > maxPoint) {
                errors.add(CONST_SUM_ANSWER_ABOVE_MAX + maxPoint);
            }
        }
        return errors;
    }

    /**
     * Returns errors for the common constant sum response point constraints.
     */
    public static List<String> getErrors(
            List<Integer> givenPoints, int totalPoints, boolean forceUnevenDistribution, String distributePointsFor) {
        List<String> errors = new ArrayList<>();

        int sum = 0;
        for (int point : givenPoints) {
            if (point < 0) {
                errors.add(CONST_SUM_ERROR_NEGATIVE);
                return errors;
            }

            sum += point;
        }

        if (sum != totalPoints) {
            errors.add(CONST_SUM_ERROR_MISMATCH);
            return errors;
        }

        Set<Integer> answerSet = new HashSet<>();
        if (distributePointsFor.equals(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY.getDisplayedOption())) {
            boolean hasDifferentPoints = false;
            for (int point : givenPoints) {
                if (!answerSet.isEmpty() && !answerSet.contains(point)) {
                    hasDifferentPoints = true;
                    break;
                }
                answerSet.add(point);
            }

            if (!hasDifferentPoints) {
                errors.add(CONST_SUM_ERROR_SOME_UNIQUE);
                return errors;
            }
        } else if (forceUnevenDistribution || distributePointsFor.equals(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption())) {
            for (int point : givenPoints) {
                if (answerSet.contains(point)) {
                    errors.add(CONST_SUM_ERROR_UNIQUE);
                    return errors;
                }
                answerSet.add(point);
            }
        }

        return errors;
    }

    private static void validateMinimumPoints(List<String> errors, int points) {
        if (points < CONST_SUM_MIN_NUM_OF_POINTS) {
            errors.add(CONST_SUM_ERROR_NOT_ENOUGH_POINTS + CONST_SUM_MIN_NUM_OF_POINTS + ".");
        }
    }

    private static void commonBoundaryValidation(List<String> errors, int points, int totalPoints, String boundaryType) {
        if (points < 0) {
            errors.add(String.format(CONST_SUM_TEMPLATE_NEGATIVE, boundaryType));
        }

        if (points > totalPoints) {
            errors.add(String.format(CONST_SUM_TEMPLATE_EXCEEDS_POINTS, boundaryType, totalPoints));
        }
    }
}
