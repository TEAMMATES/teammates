package teammates.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import teammates.api.Common;
import teammates.api.NotImplementedException;

public class EvalResultData {

	private static int NA = Common.UNINITIALIZED_INT;

	public SubmissionData own;
	public ArrayList<SubmissionData> incoming = new ArrayList<SubmissionData>();
	public ArrayList<SubmissionData> outgoing = new ArrayList<SubmissionData>();

	public int claimedActual = Common.UNINITIALIZED_INT;
	public int claimedToStudent = Common.UNINITIALIZED_INT;
	public int claimedToCoord = Common.UNINITIALIZED_INT;
	public int perceivedToCoord = Common.UNINITIALIZED_INT;
	public int perceivedToStudent = Common.UNINITIALIZED_INT;

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
		int teamSize = input.length;
		int containerSize = teamSize * 2 + 1;
		int[][] output = new int[containerSize][teamSize];

		// create the three sub containers
		int[][] actualInput = new int[teamSize][teamSize];
		int[] perceivedForCoord = new int[teamSize];
		int[][] perceivedForStudent =  new int[teamSize][teamSize];
		
		//fill first sub-container
		for (int i = 0; i < teamSize; i++) {
			actualInput[i] = input[i];
		}
		
		//fill second sub-container
		int[][] normalizedInput = new int[teamSize][teamSize];
		for (int i = 0; i < teamSize; i++) {
			normalizedInput[i] = normalizeValues(input[i]);
		}
		int[][] selfRatingsRemoved = excludeSelfRatings(normalizedInput);

		int[][] selfRatingRemovedAndNormalized = new int[teamSize][teamSize];
		for (int i = 0; i < teamSize; i++) {
			selfRatingRemovedAndNormalized[i] = normalizeValues(selfRatingsRemoved[i]);
		}
		perceivedForCoord = normalizeValues(averageColumns(selfRatingRemovedAndNormalized));
		
		//fill third sub-container
		for (int k = 0; k < teamSize; k++) {
			perceivedForStudent[k] = calculatePerceivedForStudent(actualInput[k],
					perceivedForCoord);
		}
		
		//transfer values to output container
		int i=0;
		for (; i < teamSize; i++) {
			output[i] = normalizedInput[i];
		}
		output[i] = perceivedForCoord;
		i++;
		for (int k = 0; k < teamSize; k++) {
			output[i] = perceivedForStudent[k];
			i++;
		}
		
		return output;
	}

	public static int[] calculatePerceivedForStudent(int[] actualInput,
			int[] perceivedForCoord) {
		int[] perceivedForStudent = new int[actualInput.length];
		for (int i = 0; i < perceivedForStudent.length; i++) {
			int sumOfActual = sum(actualInput);
			int sumOfperceivedForCoord = sum(perceivedForCoord);
			if(sumOfActual==NA){
				sumOfActual = sumOfperceivedForCoord;
			}
			double factor = ((double)sumOfActual)/ sumOfperceivedForCoord;
			perceivedForStudent[i] = (int)(perceivedForCoord[i] * factor);
		}
		return perceivedForStudent;
	}

	public static int sum(int[] input) {
		int sum = NA;
		if (input.length == 0) {
			return 0;
		}
		for (int i = 0; i < input.length; i++) {
			if (input[i] != NA) {
				sum = (sum == NA ? input[i] : sum + input[i]);
			}
		}
		return sum;
	}

	// TODO: make this private and use reflection to test
	public static int[][] excludeSelfRatings(int[][] input) {
		int[][] output = new int[input.length][input.length];
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].length; j++) {
				if (i == j) {
					output[i][j] = Common.UNINITIALIZED_INT;
				} else {
					output[i][j] = input[i][j];
				}
			}
		}
		return output;
	}

	// TODO: make this private and use reflection to test
	public static int[] normalizeValues(int[] input) {
		int sum = 0;
		int count = 0;
		int[] output = new int[input.length];

		for (int j = 0; j < input.length; j++) {
			if (input[j] == NA) {
				continue;
			}
			sum += input[j];
			count++;
		}

		double factor = (sum == 0 ? 0 : count * 100.0 / sum);

		for (int j = 0; j < input.length; j++) {
			output[j] = (int) (input[j] == NA ? NA : Math.round(input[j]
					* factor));
		}
		return output;
	}

	// TODO: make this private and use reflection to test
	public static int[] averageColumns(int[][] input) {
		int[] output = new int[input.length];

		for (int i = 0; i < input.length; i++) {
			int sum = 0;
			int count = 0;
			for (int j = 0; j < input.length; j++) {
				int value = input[j][i];
				if (value == NA) {
					continue;
				} else {
					sum += value;
					count++;
				}
			}
			output[i] = (count == 0 ? NA : Math.round(sum / count));
		}
		return output;
	}

}
