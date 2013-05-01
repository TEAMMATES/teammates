package teammates.common.datatransfer;

import static teammates.common.Common.EOL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;

import teammates.common.Common;

public class StudentEvalResultData {

	public ArrayList<SubmissionData> incoming = new ArrayList<SubmissionData>();
	public ArrayList<SubmissionData> outgoing = new ArrayList<SubmissionData>();
	public ArrayList<SubmissionData> selfEvaluations = new ArrayList<SubmissionData>();

	public int claimedFromStudent = Common.UNINITIALIZED_INT;
	public int claimedToInstructor = Common.UNINITIALIZED_INT;
	public int perceivedToInstructor = Common.UNINITIALIZED_INT;
	public int perceivedToStudent = Common.UNINITIALIZED_INT;

	@SuppressWarnings("unused")
	private static final Logger log = Common.getLogger();

	/** returns the self-evaluation selected from outgoing submissions */
	public SubmissionData getSelfEvaluation() {
		for (SubmissionData s : outgoing) {
			if (s.reviewee.equals(s.reviewer)) {
				return s;
			}
		}
		return null;
	}

	public void sortOutgoingByStudentNameAscending() {
		Collections.sort(outgoing, new Comparator<SubmissionData>() {
			public int compare(SubmissionData s1, SubmissionData s2) {
				// email is appended to avoid mix ups due to two students with
				// same name.
				return (s1.revieweeName + s1.reviewee)
						.compareTo(s2.revieweeName + s2.reviewee);
			}
		});
	}

	public void sortIncomingByStudentNameAscending() {
		Collections.sort(incoming, new Comparator<SubmissionData>() {
			public int compare(SubmissionData s1, SubmissionData s2) {
				// email is appended to avoid mix ups due to two students with
				// same name.
				return (s1.reviewerName + s1.reviewer)
						.compareTo(s2.reviewerName + s2.reviewer);
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

	public String getOwnerEmail() {
		for (SubmissionData sb : outgoing) {
			if (sb.reviewee.equals(sb.reviewer)) {
				return sb.reviewer;
			}
		}
		return null;
	}
	
	public String toString(){
		return toString(0);
	}

	public String toString(int indent) {
		String indentString = Common.getIndent(indent);
		StringBuilder sb = new StringBuilder();
		sb.append(indentString + "claimedFromStudent:" + claimedFromStudent
				+ EOL);
		sb.append(indentString + "claimedToInstructor:" + claimedToInstructor + EOL);
		sb.append(indentString + "perceivedToStudent:" + perceivedToStudent
				+ EOL);
		sb.append(indentString + "perceivedToInstructor:" + perceivedToInstructor + EOL);

		sb.append(indentString + "outgoing:" + EOL);
		for (SubmissionData submission : outgoing) {
			sb.append(submission.toString(indent + 2) + EOL);
		}

		sb.append(indentString + "incoming:" + EOL);
		for (SubmissionData submission : incoming) {
			sb.append(submission.toString(indent + 2) + EOL);
		}
		
		sb.append(indentString + "self evaluations:" + EOL);
		for (SubmissionData submission : selfEvaluations) {
			sb.append(submission.toString(indent + 2) + EOL);
		}
		
		return replaceMagicNumbers(sb.toString());
	}
	
	private String replaceMagicNumbers(String input){
		return input.replace(Common.UNINITIALIZED_INT + ".0", " NA")
				.replace(Common.UNINITIALIZED_INT + "", " NA")
				.replace(Common.POINTS_NOT_SUBMITTED + "", "NSB")
				.replace(Common.POINTS_NOT_SURE + "", "NSU");
	}

}
