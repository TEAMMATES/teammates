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



	public SubmissionData own;
	public ArrayList<SubmissionData> incoming = new ArrayList<SubmissionData>();
	public ArrayList<SubmissionData> outgoingOriginal = new ArrayList<SubmissionData>();
	public ArrayList<SubmissionData> outgoingNormalized = new ArrayList<SubmissionData>();

	public int claimedFromStudent = Common.UNINITIALIZED_INT;
	public int claimedToCoord = Common.UNINITIALIZED_INT;
	public int perceivedToCoord = Common.UNINITIALIZED_INT;
	public int perceivedToStudent = Common.UNINITIALIZED_INT;

	private static final Logger log = Common.getLogger();

	public void sortOutgoingByStudentNameAscending() {
		sortOutgoingSubmissionsByName(outgoingOriginal);
		sortOutgoingSubmissionsByName(outgoingNormalized);
	}

	private void sortOutgoingSubmissionsByName(ArrayList<SubmissionData> submissions) {
		Collections.sort(submissions, new Comparator<SubmissionData>() {
			public int compare(SubmissionData s1, SubmissionData s2) {
				//email is prefixed to avoid mix ups due to two students with
				//same name.
				return (s1.revieweeName+s1.reviewee)
						.compareTo(s2.revieweeName+s2.reviewee);
			}
		});
	}

	public void sortIncomingByStudentNameAscending() {
		Collections.sort(incoming, new Comparator<SubmissionData>() {
			public int compare(SubmissionData s1, SubmissionData s2) {
				//email is prefixed to avoid mix ups due to two students with
				//same name.
				return (s1.reviewerName+s1.reviewer)
						.compareTo(s2.reviewerName+s2.reviewer);
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

	

}
