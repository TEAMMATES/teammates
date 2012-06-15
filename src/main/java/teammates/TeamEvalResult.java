package teammates;

import java.util.Arrays;
import java.util.logging.Logger;

import teammates.api.Common;

public class TeamEvalResult {
	/** N/A */
	public static int NA = Common.UNINITIALIZED_INT;
	/** submitted 'Not SUre' */
	public static int NSU = Common.POINTS_NOT_SURE;
	/** did Not SuBmit */
	public static int NSB = Common.POINTS_NOT_SUBMITTED;
	private static Logger log = Common.getLogger();

	/** submission values originally from students of the team */
	public int[][] claimedToStudents;
	/** submission values to be shown to coordinator (after normalization) */
	public int[][] claimedToCoord;
	/** average perception of team shown to coord. Excludes self evaluations) */
	public int[] perceivedToCoord;
	/** team perception shown to students. normalized based on their own claims */
	public int[][] perceivedToStudents;
	
	/** ratings after removing bias for self ratings*/
	public int[][] unbiased;

	public TeamEvalResult(int[][] submissionValues) {
		claimedToStudents = submissionValues;

		log.fine("==================\n" + "starting result calculation for\n"
				+ pointsToString(submissionValues));

		claimedToCoord = doubleToInt(normalizeValues(intToDouble(claimedToStudents)));

		int[][] claimedSanitized = sanitizeInput(submissionValues);
		log.fine("submission values sanitized :\n"
				+ pointsToString(claimedSanitized));

		double[][] claimedSanitizedNormalized = normalizeValues(intToDouble(claimedSanitized));

		log.fine("submission values sanitized and normalized :\n"
				+ pointsToString(claimedSanitizedNormalized));
		
		double[][] unbiasedAsDouble = calculateUnbiased(claimedSanitizedNormalized); 
		log.fine("unbiased (i.e.self ratings removed and normalized) :\n"
				+ pointsToString(unbiasedAsDouble));
		
		unbiased = doubleToInt(unbiasedAsDouble);
		log.fine("unbiased as int :\n"
				+ pointsToString(unbiased));
		
		double[] perceivedForCoordAsDouble = calculatePerceivedForCoord(unbiasedAsDouble);

		log.fine("perceived to coord as double:\n"
				+ replaceMagicNumbers(Arrays
						.toString(perceivedForCoordAsDouble)));

		perceivedToStudents = calculatePerceivedForStudents(claimedSanitized,
				perceivedForCoordAsDouble);
		log.fine("perceived to students :\n"
				+ pointsToString(perceivedToStudents));

		perceivedToCoord = doubleToInt(perceivedForCoordAsDouble);

		log.fine("==================");
	}

	private int[][] sanitizeInput(int[][] input) {
		int teamSize = input.length;
		int[][] output = new int[teamSize][teamSize];
		for (int i = 0; i < teamSize; i++) {
			for (int j = 0; j < teamSize; j++) {
				int points = input[i][j];
				boolean pointsNotGiven = (points == Common.POINTS_NOT_SUBMITTED)
						|| (points == Common.POINTS_NOT_SURE);
				output[i][j] = pointsNotGiven ? NA : points;
			}
		}
		return output;
	}

	private static double[][] calculateUnbiased(
			double[][] sanitizedAndNormalizedInput) {
		int teamSize = sanitizedAndNormalizedInput.length;

		double[][] selfRatingsRemoved = excludeSelfRatings(sanitizedAndNormalizedInput);
		log.fine("self ratings removed :\n"
				+ pointsToString(selfRatingsRemoved));

		double[][] selfRatingRemovedAndNormalized = new double[teamSize][teamSize];
		for (int i = 0; i < teamSize; i++) {
			selfRatingRemovedAndNormalized[i] = normalizeValues(selfRatingsRemoved[i]);
		}
		return selfRatingRemovedAndNormalized;
	}
	
	private static double[] calculatePerceivedForCoord(
			double[][] unbiased) {
		double[] perceivedForCoord;
		perceivedForCoord = normalizeValues(averageColumns(unbiased));
		return perceivedForCoord;
	}

	private int[][] calculatePerceivedForStudents(int[][] actualInputSanitized,
			double[] perceivedForCoordAsDouble) {
		int teamSize = actualInputSanitized.length;
		int[][] output = new int[teamSize][teamSize];
		for (int k = 0; k < teamSize; k++) {
			output[k] = calculatePerceivedForStudent(actualInputSanitized[k],
					perceivedForCoordAsDouble);
		}
		return output;
	}

	public static int[] calculatePerceivedForStudent(
			int[] sanitizedActualInput, double[] perceivedForCoord) {
		verify("Unsanitized value received ", isSanitized(sanitizedActualInput));

		int[] perceivedForStudent = new int[sanitizedActualInput.length];

		// remove from each array values matching special values in the other
		double[] filteredPerceivedForCoord = purgeValuesCorrespondingToSpecialValuesInFilter(
				intToDouble(sanitizedActualInput), perceivedForCoord);
		int[] filteredSanitizedActual = doubleToInt(purgeValuesCorrespondingToSpecialValuesInFilter(
				perceivedForCoord, intToDouble(sanitizedActualInput)));

		double sumOfperceivedForCoord = sum(filteredPerceivedForCoord);
		double sumOfActual = sum(filteredSanitizedActual);

		if (sumOfActual == NA) {
			sumOfActual = sumOfperceivedForCoord;
		}

		double factor = sumOfActual / sumOfperceivedForCoord;

		for (int i = 0; i < perceivedForStudent.length; i++) {
			double perceived = perceivedForCoord[i];
			if (perceived == NA) {
				perceivedForStudent[i] = NA;
			} else {
				perceivedForStudent[i] = (int) Math.round(perceived * factor);
			}
		}
		return perceivedForStudent;
	}

	public static double[] purgeValuesCorrespondingToSpecialValuesInFilter(
			double[] filterArray, double[] valueArray) {
		double[] returnValue = new double[filterArray.length];
		for (int i = 0; i < filterArray.length; i++) {
			int filterValue = (int) filterArray[i];
			boolean isSpecialValue = !isSanitized(filterValue)
					|| filterValue == NA;
			returnValue[i] = (isSpecialValue ? NA : valueArray[i]);
		}
		return returnValue;
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
		if (i == NSU) {
			return false;
		}
		if (i == NSB) {
			return false;
		}
		return true;
	}

	private static boolean isSpecialValue(int value) {
		return (value == NA) || (value == NSU) || (value == NSB);
	}

	public static double sum(double[] input) {
		double sum = NA;
		if (input.length == 0) {
			return 0;
		}

		verify("Unsanitized value in " + Arrays.toString(input),
				isSanitized(doubleToInt(input)));

		for (int i = 0; i < input.length; i++) {

			double value = input[i];
			if (value != NA) {
				sum = (sum == NA ? value : sum + value);
			}
		}
		return sum;
	}

	public static int sum(int[] input) {
		return (int) sum(intToDouble(input));
	}

	// TODO: make this private and use reflection to test
	public static double[][] excludeSelfRatings(double[][] input) {
		double[][] output = new double[input.length][input.length];
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].length; j++) {
				output[i][j] = ((i == j) ? NA : input[i][j]);
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
		return adjustByFactor(input, factor);
	}

	private static double[] adjustByFactor(double[] input, double factor) {
		double[] output = new double[input.length];
		for (int j = 0; j < input.length; j++) {
			double value = input[j];
			int valueAsInt = (int) value;
			if (valueAsInt == NSU) {
				output[j] = NSU;
			} else if (valueAsInt == NSB) {
				output[j] = NSB;
			} else if (valueAsInt == NA) {
				output[j] = NA;
			} else {
				output[j] = value * factor;
			}
		}
		return output;
	}

	private static double calculateFactor(double[] input) {
		int sum = 0;
		int count = 0;
		for (int j = 0; j < input.length; j++) {
			double value = input[j];
			int valueAsInt = (int) value;
			if (isSpecialValue(valueAsInt)) {
				continue;
			}
			sum += value;
			count++;
		}
		return (sum == 0 ? 0 : count * 100.0 / sum);
	}

	public static double[] normalizeValues(int[] input) {
		return normalizeValues(intToDouble(input));
	}

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
			converted[i] = (int) (Math.round(input[i]));
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
		return output;
	}

	private static double averageColumn(double[][] array, int columnIndex) {
		int sum = 0;
		int count = 0;
		for (int j = 0; j < array.length; j++) {
			double value = array[j][columnIndex];

			if (value == NA) {
				continue;
			} else {
				sum += value;
				count++;
			}
		}
		// omit calculation if fewer than two data points
		return (count < 2 ? NA : Math.round((double) sum / count));
	}

	public static String pointsToString(int[][] array) {
		return pointsToString(intToDouble(array)).replace(".0", "");
	}

	private String pointsToString(int[] input) {
		return replaceMagicNumbers(Arrays.toString(input)) + Common.EOL;
	}

	public static String pointsToString(double[][] array) {
		String returnValue = "";
		boolean isSquareArray = (array.length == array[0].length);
		int firstDividerLocation = (array.length - 1) / 2 - 1;
		int secondDividerLocation = firstDividerLocation + 1;
		for (int i = 0; i < array.length; i++) {
			returnValue = returnValue + Arrays.toString(array[i]) + Common.EOL;
			if (isSquareArray) {
				continue;
			}
			if ((i == firstDividerLocation) || (i == secondDividerLocation)) {
				returnValue = returnValue + "======================="
						+ Common.EOL;
			}
		}
		returnValue = replaceMagicNumbers(returnValue);
		return returnValue;
	}

	public static String replaceMagicNumbers(String returnValue) {
		returnValue = returnValue.replace(NA + ".0", " NA");
		returnValue = returnValue.replace(NA + "", " NA");
		returnValue = returnValue.replace(NSB + ".0", "NSB");
		returnValue = returnValue.replace(NSU + ".0", "NSU");
		return returnValue;
	}

	private static void verify(String message, boolean condition) {
		// TODO: replace with assert?
		if (!condition) {
			throw new RuntimeException("Internal assertion failuer : "
					+ message);
		}
	}

	public String toString() {
		return toString(0);
	}

	public String toString(int indent) {
		String indentString = Common.getIndent(indent);
		String divider = "====================" + Common.EOL;
		StringBuilder sb = new StringBuilder();
		sb.append("claimed from student:");
		String filler = "                     ";
		sb.append(indentString
				+ pointsToString((claimedToStudents)).replace(Common.EOL,
						Common.EOL + indentString + filler));
		sb.append(divider);
		sb.append("    claimed to coord:");
		sb.append(indentString
				+ pointsToString((claimedToCoord)).replace(Common.EOL,
						Common.EOL + indentString+ filler));
		sb.append(divider);
		sb.append("  perceived to coord:");
		sb.append(indentString
				+ pointsToString(perceivedToCoord).replace(Common.EOL,
						Common.EOL + indentString+ filler));
		sb.append(divider);
		sb.append("            unbiased:");
		sb.append(indentString
				+ pointsToString(unbiased).replace(Common.EOL,
						Common.EOL + indentString+ filler));
		sb.append(divider);
		sb.append("perceived to student:");
		sb.append(indentString
				+ pointsToString((perceivedToStudents)).replace(Common.EOL,
						Common.EOL + indentString+ filler));
		sb.append(divider);
		return sb.toString();
	}

}
