package teammates.common.datatransfer;

import java.util.Arrays;
import java.util.List;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;

/**
 * This class represents an feedback contribution question result for a given team.
 * It exposes the result via some public variables.
 */
public class TeamEvalResult {

    /** submitted value is uninitialized. */
    public static final int NA = Const.INT_UNINITIALIZED;
    /** submitted 'Not SUre'. */
    public static final int NSU = Const.POINTS_NOT_SURE;
    /** did Not SuBmit. */
    public static final int NSB = Const.POINTS_NOT_SUBMITTED;
    private static final Logger log = Logger.getLogger();

    /** submission values originally from students of the team. */
    public int[][] claimed;
    /** submission values to be shown to instructor (after normalization). */
    public int[][] normalizedClaimed;
    /** average perception of team shown to instructor. Excludes self evaluations */
    public int[] normalizedAveragePerceived;
    /** team perception shown to students. denormalized to match their own claims */
    public int[][] denormalizedAveragePerceived;

    /** the values that were used to calculate normalizedAveragePerceived values. */
    public int[][] normalizedPeerContributionRatio;

    // List of student email's.
    // The index of the student in the list is used as the index for the int arrays.
    // The 2d int arrays are of the format [giverIndex][recipientIndex]
    public List<String> studentEmails;

    public TeamEvalResult(int[][] submissionValues) {
        /*This is the only method that should be public. However, many of the
         * other methods are set as public for the ease of testing.
         */

        log.fine("==================\n" + "starting result calculation for\n"
                + pointsToString(submissionValues));

        claimed = submissionValues;

        normalizedClaimed = normalizeValues(claimed);

        int[][] claimedSanitized = sanitizeInput(submissionValues);
        log.fine("claimed values sanitized :\n"
                + pointsToString(claimedSanitized));

        double[][] claimedSanitizedNormalized = normalizeValues(intToDouble(claimedSanitized));
        log.fine("claimed values sanitized and normalized :\n"
                + pointsToString(claimedSanitizedNormalized));

        double[][] peerContributionRatioAsDouble = calculatePeerContributionRatio(claimedSanitizedNormalized);
        log.fine("peerContributionRatio as double :\n"
                + pointsToString(peerContributionRatioAsDouble));

        double[] averagePerceivedAsDouble = averageColumns(peerContributionRatioAsDouble);
        log.fine("averagePerceived as double:\n"
                + replaceMagicNumbers(Arrays.toString(averagePerceivedAsDouble)));

        double[] normalizedAveragePerceivedAsDouble = normalizeValues(averagePerceivedAsDouble);
        log.fine("normalizedAveragePerceivedAsDouble as double:\n"
                + replaceMagicNumbers(Arrays
                        .toString(normalizedAveragePerceivedAsDouble)));

        double[][] normalizedPeerContributionRatioAsDouble =
                adjustPeerContributionRatioToTallyNormalizedAveragePerceived(peerContributionRatioAsDouble);
        log.fine("normalizedPeerContributionRatio as double :\n"
                + pointsToString(peerContributionRatioAsDouble));

        normalizedPeerContributionRatio = doubleToInt(normalizedPeerContributionRatioAsDouble);
        log.fine("normalizedUnbiasedClaimed as int :\n"
                + pointsToString(normalizedPeerContributionRatio));

        denormalizedAveragePerceived = calculatePerceivedForStudents(
                claimedSanitized, normalizedAveragePerceivedAsDouble);
        log.fine("perceived to students :\n"
                + pointsToString(denormalizedAveragePerceived));

        normalizedAveragePerceived = doubleToInt(normalizedAveragePerceivedAsDouble);

        log.fine("Final result:\n" + this.toString());

        log.fine("==================");
    }

    /**
     * Replaces all missing points ('not sure' with NSU and 'did not submit' with NA).
     */
    private int[][] sanitizeInput(int[][] input) {
        int teamSize = input.length;
        int[][] output = new int[teamSize][teamSize];
        for (int i = 0; i < teamSize; i++) {
            for (int j = 0; j < teamSize; j++) {
                int points = input[i][j];
                boolean pointsNotGiven = points == Const.POINTS_NOT_SUBMITTED;
                output[i][j] = pointsNotGiven ? NA : points;
            }
        }
        return output;
    }

    private static double[][] calculatePeerContributionRatio(double[][] input) {

        int teamSize = input.length;

        double[][] selfRatingsRemoved = removeSelfRatings(input);

        double[][] selfRatingRemovedAndNormalized = new double[teamSize][teamSize];
        for (int i = 0; i < teamSize; i++) {
            selfRatingRemovedAndNormalized[i] = normalizeValues(selfRatingsRemoved[i]);
        }

        return selfRatingRemovedAndNormalized;
    }

    private static double[][] adjustPeerContributionRatioToTallyNormalizedAveragePerceived(
            double[][] peerContributionRatio) {
        double[] columnsAveraged = averageColumns(peerContributionRatio);
        double factor = calculateFactor(columnsAveraged);
        return multiplyByFactor(factor, peerContributionRatio);
    }

    private int[][] calculatePerceivedForStudents(int[][] claimedSanitized,
            double[] normalizedAveragePerceivedAsDouble) {
        int teamSize = claimedSanitized.length;
        int[][] output = new int[teamSize][teamSize];
        for (int k = 0; k < teamSize; k++) {
            output[k] = calculatePerceivedForStudent(claimedSanitized[k],
                    normalizedAveragePerceivedAsDouble);
        }
        return output;
    }

    public static int[] calculatePerceivedForStudent(int[] claimedSanitizedRow,
            double[] normalizedAveragePerceivedAsDouble) {

        verify("Unsanitized value received ", isSanitized(claimedSanitizedRow));

        // remove from each array values matching special values in the other
        double[] filteredPerceived = purgeValuesCorrespondingToSpecialValuesInFilter(
                intToDouble(claimedSanitizedRow),
                normalizedAveragePerceivedAsDouble);
        int[] filteredSanitizedActual = doubleToInt(purgeValuesCorrespondingToSpecialValuesInFilter(
                normalizedAveragePerceivedAsDouble,
                intToDouble(claimedSanitizedRow)));

        double sumOfPerceived = sum(filteredPerceived);
        double sumOfActual = sum(filteredSanitizedActual);

        // if the student did not submit
        if (sumOfActual == NA) {
            sumOfActual = sumOfPerceived;
        }

        double factor = sumOfActual / sumOfPerceived;

        return doubleToInt(multiplyByFactor(factor,
                normalizedAveragePerceivedAsDouble));
    }

    public static boolean isSanitized(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (!isSanitized(array[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSanitized(int i) {
        return i != NSB;
    }

    private static boolean isSpecialValue(int value) {
        return value == NA || value == NSU || value == NSB;
    }

    private static boolean isValidSpecialValue(double value) {
        return value == NA || value == NSU;
    }

    private static double[][] multiplyByFactor(double factor, double[][] input) {
        int teamSize = input.length;
        double[][] output = new double[teamSize][teamSize];
        for (int i = 0; i < teamSize; i++) {
            output[i] = multiplyByFactor(factor, input[i]);
        }
        return output;
    }

    private static double[] multiplyByFactor(double factor, double[] input) {
        int teamSize = input.length;
        double[] output = new double[teamSize];
        for (int j = 0; j < teamSize; j++) {
            double value = input[j];
            if (isSpecialValue((int) value)) {
                output[j] = value;
            } else {
                output[j] = factor == 0 ? value : value * factor;
            }
        }
        return output;
    }

    public static double[] purgeValuesCorrespondingToSpecialValuesInFilter(
            double[] filterArray, double[] valueArray) {
        double[] returnValue = new double[filterArray.length];
        for (int i = 0; i < filterArray.length; i++) {
            int filterValue = (int) filterArray[i];
            if (filterValue == NA || filterValue == NSU || !isSanitized(filterValue)) {
                returnValue[i] = filterValue == NSU ? NSU : NA;
            } else {
                returnValue[i] = valueArray[i];
            }
        }
        return returnValue;
    }

    public static double sum(double[] input) {
        if (input.length == 0) {
            return 0;
        }

        verify("Unsanitized value in " + Arrays.toString(input),
                isSanitized(doubleToInt(input)));

        double sum = NA;
        for (double value : input) {

            if (!isValidSpecialValue(value)) {
                sum = sum == NA ? value : sum + value;
            }
        }
        return sum;
    }

    public static int sum(int[] input) {
        return (int) sum(intToDouble(input));
    }

    // TODO: methods like these private and use reflection to test
    public static double[][] removeSelfRatings(double[][] input) {
        double[][] output = new double[input.length][input.length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                output[i][j] = i == j ? NA : input[i][j];
            }
        }
        return output;
    }

    private static double[][] normalizeValues(double[][] input) {
        double[][] output = new double[input.length][input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = normalizeValues(input[i]);
        }
        return output;
    }

    // TODO: make this private and use reflection to test
    public static double[] normalizeValues(double[] input) {
        double factor = calculateFactor(input);
        return multiplyByFactor(factor, input);
    }

    public static double[] normalizeValues(int[] input) {
        return normalizeValues(intToDouble(input));
    }

    public static int[][] normalizeValues(int[][] input) {
        return doubleToInt(normalizeValues(intToDouble(input)));
    }

    private static double calculateFactor(double[] input) {
        double actualSum = 0;
        int count = 0;
        for (double value : input) {
            int valueAsInt = (int) value;
            if (isSpecialValue(valueAsInt)) {
                continue;
            }
            actualSum += value;
            count++;
        }

        double idealSum = count * 100.0;
        double factor = actualSum == 0 ? 0 : idealSum / actualSum;
        log.fine("Factor = " + idealSum + "/" + actualSum + " = " + factor);
        return factor;
    }

    @SuppressWarnings("PMD.AvoidArrayLoops") // the arrays are of different types
    private static double[] intToDouble(int[] input) {
        double[] converted = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            converted[i] = input[i];
        }
        return converted;
    }

    private static double[][] intToDouble(int[][] input) {
        double[][] converted = new double[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            converted[i] = intToDouble(input[i]);
        }
        return converted;
    }

    private static int[] doubleToInt(double[] input) {
        int[] converted = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            converted[i] = (int) Math.round(input[i]);
        }
        return converted;
    }

    private static int[][] doubleToInt(double[][] input) {
        int[][] output = new int[input.length][input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = doubleToInt(input[i]);
        }
        return output;
    }

    // TODO: make this private and use reflection to test
    public static double[] averageColumns(double[][] input) {
        double[] output = new double[input.length];

        for (int i = 0; i < input.length; i++) {
            verify("Unsanitized value in " + Arrays.toString(input[i]),
                    isSanitized(doubleToInt(input[i])));
            output[i] = averageColumn(input, i);
        }
        log.fine("Column averages: "
                + replaceMagicNumbers(Arrays.toString(output)));
        return output;
    }

    private static double averageColumn(double[][] arrayOfArrays, int columnIndex) {
        double sum = 0;
        int count = 0;
        StringBuilder values = new StringBuilder();
        for (double[] array : arrayOfArrays) {
            double value = array[columnIndex];

            values.append(value).append(' ');
            if (isValidSpecialValue(value)) {
                continue;
            }
            sum += value;
            count++;
        }
        // omit calculation if no data points
        double average = count == 0 ? NA : (double) (sum / count);

        String logMessage = "Average(" + values.toString().trim() + ") = " + average;
        log.fine(replaceMagicNumbers(logMessage));

        return average;
    }

    public static String pointsToString(int[][] array) {
        return pointsToString(intToDouble(array)).replace(".0", "");
    }

    private String pointsToString(int[] input) {
        return replaceMagicNumbers(Arrays.toString(input)) + System.lineSeparator();
    }

    public static String pointsToString(double[][] array) {
        StringBuilder returnValue = new StringBuilder();
        boolean isSquareArray = array.length == array[0].length;
        int teamSize = (array.length - 1) / 3;
        int firstDividerLocation = teamSize - 1;
        int secondDividerLocation = teamSize * 2 - 1;
        int thirdDividerLocation = secondDividerLocation + 1;
        for (int i = 0; i < array.length; i++) {
            returnValue.append(Arrays.toString(array[i])).append(System.lineSeparator());
            if (isSquareArray) {
                continue;
            }
            if (i == firstDividerLocation || i == secondDividerLocation || i == thirdDividerLocation) {
                returnValue.append("=======================")
                           .append(System.lineSeparator());
            }
        }
        return replaceMagicNumbers(returnValue.toString());
    }

    /** replaces 999 etc. with NA, NSB, NSU etc.
     */
    public static String replaceMagicNumbers(String returnValue) {
        return returnValue.replace(NA + ".0", " NA")
                          .replace(Integer.toString(NA), " NA")
                          .replace(NSB + ".0", "NSB")
                          .replace(NSU + ".0", "NSU");
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int indent) {
        String indentString = StringHelper.getIndent(indent);
        String divider = "======================" + System.lineSeparator();
        StringBuilder sb = new StringBuilder(200);
        sb.append("           claimed from student:");
        String filler = "                                ";
        sb.append(indentString)
          .append(pointsToString(claimed).replace(System.lineSeparator(),
                        System.lineSeparator() + indentString + filler))
          .append(divider)
          .append("              normalizedClaimed:")
          .append(indentString)
          .append(pointsToString(normalizedClaimed).replace(System.lineSeparator(),
                        System.lineSeparator() + indentString + filler))
          .append(divider)
          .append("normalizedPeerContributionRatio:")
          .append(indentString)
          .append(pointsToString(normalizedPeerContributionRatio).replace(
                        System.lineSeparator(), System.lineSeparator() + indentString + filler))
          .append(divider)
          .append("     normalizedAveragePerceived:")
          .append(indentString)
          .append(pointsToString(normalizedAveragePerceived).replace(
                        System.lineSeparator(), System.lineSeparator() + indentString + filler))
          .append(divider)

          .append("   denormalizedAveragePerceived:")
          .append(indentString)
          .append(pointsToString(denormalizedAveragePerceived).replace(
                        System.lineSeparator(), System.lineSeparator() + indentString + filler))
            .append(divider);
        return sb.toString();
    }

    private static void verify(String message, boolean condition) {
        Assumption.assertTrue("Internal assertion failure : " + message, condition);
    }
}
