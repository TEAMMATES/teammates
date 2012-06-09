package teammates.datatransfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;

import teammates.api.APIServlet;
import teammates.api.Common;
import teammates.api.NotImplementedException;

public class EvalResultData {

	/** N/A */
	public static int NA = Common.UNINITIALIZED_INT;
	/** submitted 'Not SUre' */
	public static int NSU = Common.POINTS_NOT_SURE;
	/** did Not SuBmit */
	public static int NSB = Common.POINTS_NOT_SUBMITTED;

	public SubmissionData own;
	public ArrayList<SubmissionData> incoming = new ArrayList<SubmissionData>();
	public ArrayList<SubmissionData> outgoing = new ArrayList<SubmissionData>();

	public int claimedActual = Common.UNINITIALIZED_INT;
	public int claimedToStudent = Common.UNINITIALIZED_INT;
	public int claimedToCoord = Common.UNINITIALIZED_INT;
	public int perceivedToCoord = Common.UNINITIALIZED_INT;
	public int perceivedToStudent = Common.UNINITIALIZED_INT;

	private static final Logger log = Logger.getLogger(EvalResultData.class
			.getName());

	public void sortOutgoingByStudentNameAscending() {
		Collections.sort(outgoing, new Comparator<SubmissionData>() {
			public int compare(SubmissionData s1, SubmissionData s2) {
				return s1.revieweeName.compareTo(s2.revieweeName);
			}
		});
	}

	public void sortIncomingByStudentNameAscending() {
		Collections.sort(incoming, new Comparator<SubmissionData>() {
			public int compare(SubmissionData s1, SubmissionData s2) {
				return s1.reviewerName.compareTo(s2.reviewerName);
			}
		});
	}

	public void sortIncomingByFeedbackAscending() {
		Collections.sort(incoming, new Comparator<SubmissionData>() {
			public int compare(SubmissionData s1, SubmissionData s2) {
				return s1.p2pFeedback.getValue().compareTo(
						s2.p2pFeedback.getValue());
			}
		});
	}

	public static int[][] calculatePoints(int[][] input) {
		log.info("==================\n" + "starting result calculation for\n"
				+ pointsToString(input));
		int teamSize = input.length;
		int containerSize = teamSize * 2 + 1;
		int[][] output = new int[containerSize][teamSize];

		// create the three sub containers
		int[][] actualInputNormalized = new int[teamSize][teamSize];
		double[] perceivedForCoord = new double[teamSize];
		int[][] perceivedForStudent = new int[teamSize][teamSize];

		// fill first sub-container
		for (int i = 0; i < teamSize; i++) {
			actualInputNormalized[i] = doubleToInt(normalizeValues(input[i]));
		}

		// fill second sub-container
		int[][] actualInputSanitized = new int[teamSize][teamSize];
		for (int i = 0; i < teamSize; i++) {
			for (int j = 0; j < teamSize; j++) {
				int points = input[i][j];
				boolean pointsNotGiven = (points == Common.POINTS_NOT_SUBMITTED)
						|| (points == Common.POINTS_NOT_SURE);
				actualInputSanitized[i][j] = pointsNotGiven ? NA : points;
			}

		}
		log.info("actual values sanitized :\n"
				+ pointsToString(actualInputSanitized));
		
		double[][] sanitizedAndNormalizedInput = new double[teamSize][teamSize];
		for (int i = 0; i < teamSize; i++) {
			sanitizedAndNormalizedInput[i] = normalizeValues(actualInputSanitized[i]);
		}

		perceivedForCoord = calculatePerceivedForCoord(sanitizedAndNormalizedInput);

		// fill third sub-container
		for (int k = 0; k < teamSize; k++) {
			perceivedForStudent[k] = calculatePerceivedForStudent(
					actualInputSanitized[k], perceivedForCoord);
		}
		log.info("perceived to student :\n"
				+ pointsToString(perceivedForStudent));

		// transfer values to output container
		int i = 0;
		for (; i < teamSize; i++) {
			output[i] = actualInputNormalized[i];
		}
		output[i] = doubleToInt(perceivedForCoord);
		i++;
		for (int k = 0; k < teamSize; k++) {
			output[i] = perceivedForStudent[k];
			i++;
		}
		log.info("==================");

		return output;
	}

	private static double[] calculatePerceivedForCoord(double[][] sanitizedAndNormalizedInput) {
		int teamSize = sanitizedAndNormalizedInput.length;
		double[] perceivedForCoord;
		
		log.info("actual values sanitiezed and normalized :\n"
				+ pointsToString(sanitizedAndNormalizedInput));

		double[][] selfRatingsRemoved = excludeSelfRatings(sanitizedAndNormalizedInput);
		log.info("self ratings removed :\n"
				+ pointsToString(selfRatingsRemoved));

		double[][] selfRatingRemovedAndNormalized = new double[teamSize][teamSize];
		for (int i = 0; i < teamSize; i++) {
			selfRatingRemovedAndNormalized[i] = normalizeValues(selfRatingsRemoved[i]);
		}
		log.info("self ratings removed and normalized :\n"
				+ pointsToString(selfRatingRemovedAndNormalized));

		perceivedForCoord = normalizeValues(averageColumns(selfRatingRemovedAndNormalized));
		log.info("perceived to coord :\n"
				+ replaceMagicNumbers(Arrays.toString(perceivedForCoord)));
		return perceivedForCoord;
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

	@Deprecated
	public static double[] extractPerceivedValuesWithCorrespondingInputValues(
			int[] filterArray, double[] valueArray) {
		double[] returnValue = new double[filterArray.length];
		for (int i = 0; i < filterArray.length; i++) {
			int filterValue = filterArray[i];
			boolean isSpecialValue = !isSanitized(filterValue)
					|| filterValue == NA;
			returnValue[i] = (isSpecialValue ? NA : valueArray[i]);
		}
		return returnValue;
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
				if (i == j) {
					output[i][j] = NA;
				} else {
					output[i][j] = input[i][j];
				}
			}
		}
		return output;
	}

	// TODO: make this private and use reflection to test
	public static double[] normalizeValues(double[] input) {
		int sum = 0;
		int count = 0;
		double[] output = new double[input.length];

		// calcuate factor
		for (int j = 0; j < input.length; j++) {
			double value = input[j];
			int valueAsInt = (int) value;
			if (isSpecialValue(valueAsInt)) {
				continue;
			}
			sum += value;
			count++;
		}

		double factor = (sum == 0 ? 0 : count * 100.0 / sum);

		// normalize values
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

	private static boolean isSpecialValue(int value) {
		return (value == NA) || (value == NSU) || (value == NSB);
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

	// TODO: make this private and use reflection to test
	public static double[] averageColumns(double[][] input) {
		double[] output = new double[input.length];

		for (int i = 0; i < input.length; i++) {
			int sum = 0;
			int count = 0;
			for (int j = 0; j < input.length; j++) {
				double value = input[j][i];
				if (value == NA) {
					continue;
				} else {
					sum += value;
					count++;
				}
			}
			output[i] = (count < 2 ? NA : Math.round((double) sum / count));
		}
		return output;
	}

	public static String pointsToString(int[][] array) {
		return pointsToString(intToDouble(array)).replace(".0", "");
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

	private static String replaceMagicNumbers(String returnValue) {
		returnValue = returnValue.replace(NA + ".0", " NA");
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

}
