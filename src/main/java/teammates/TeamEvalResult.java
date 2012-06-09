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

	public int[][] submissionValues;
	public int[][] submissionValuesNormalized;
	public int[] perceivedForCoord;
	public int[][] perceivedForStudent;

	public TeamEvalResult(int[][] submissionValues) {
		int teamSize = submissionValues.length;
		this.submissionValues = submissionValues;
		
		init(submissionValues);
	}
	
	public void init(int[][] input) {
		log .info("==================\n" + "starting result calculation for\n"
				+ pointsToString(input));
		
		int teamSize = input.length;

		double[][] submissionValuesNormalizedAsDouble = fillSubmissionValuesNormalized(submissionValues);

		submissionValuesNormalized = doubleToInt(submissionValuesNormalizedAsDouble);
		

		// fill second sub-container
		int[][] actualInputSanitized = sanitizeInput(input);
		log.info("actual values sanitized :\n"
				+ pointsToString(actualInputSanitized));
		
		double[][] sanitizedAndNormalizedInput = normalizeValues(
				intToDouble(actualInputSanitized));
//		for (int i = 0; i < teamSize; i++) {
//			sanitizedAndNormalizedInput[i] = normalizeValues(actualInputSanitized[i]);
//		}

		double[] perceivedForCoordAsDouble = calculatePerceivedForCoord(sanitizedAndNormalizedInput);

		// fill third sub-container
		perceivedForStudent = new int[teamSize][teamSize];
		for (int k = 0; k < teamSize; k++) {
			perceivedForStudent[k] = calculatePerceivedForStudent(
					actualInputSanitized[k], perceivedForCoordAsDouble);
		}
		log.info("perceived to student :\n"
				+ pointsToString(perceivedForStudent));

		perceivedForCoord = doubleToInt(perceivedForCoordAsDouble);
		
		log.info("==================");

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

	private double[][] fillSubmissionValuesNormalized(int[][] input) {
		int  teamSize = input.length;
		double[][] output = new double[teamSize][teamSize];
		for (int i = 0; i < teamSize; i++) {
			output[i] = normalizeValues(input[i]);
		}
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
	
	private static double[][] normalizeValues(double[][] input){
		double[][] output = new double[input.length][input.length];
		for (int i = 0; i < input.length; i++) {
			output[i] = normalizeValues(input[i]);
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
	
	private static int[][] doubleToInt(double[][] input){
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
			//omit calculation if fewer than two data points
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

	public static String replaceMagicNumbers(String returnValue) {
		returnValue = returnValue.replace(NA + ".0", " NA");
		returnValue = returnValue.replace(NA+"" , " NA");
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
