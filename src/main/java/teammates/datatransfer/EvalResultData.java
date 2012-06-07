package teammates.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import teammates.api.Common;
import teammates.api.NotImplementedException;

public class EvalResultData {
	
	public SubmissionData own;
	public ArrayList<SubmissionData> incoming = new ArrayList<SubmissionData>();
	public ArrayList<SubmissionData> outgoing = new ArrayList<SubmissionData>();
	
	public int claimedActual = Common.UNINITIALIZED_INT;
	public int claimedToStudent = Common.UNINITIALIZED_INT;
	public int claimedToCoord = Common.UNINITIALIZED_INT;
	public int perceivedToCoord = Common.UNINITIALIZED_INT;
	public int perceivedToStudent = Common.UNINITIALIZED_INT;
	
	public void sortOutgoingByStudentNameAscending(){
		Collections.sort(outgoing, new Comparator<SubmissionData>() {
			public int compare(SubmissionData s1, SubmissionData s2) {
				return s1.revieweeName.compareTo(s2.revieweeName);
			}
		});
	}
	
	public void sortIncomingByStudentNameAscending(){
		Collections.sort(incoming, new Comparator<SubmissionData>() {
			public int compare(SubmissionData s1, SubmissionData s2) {
				return s1.reviewerName.compareTo(s2.reviewerName);
			}
		});
	}
	
	public void sortIncomingByFeedbackAscending(){
		Collections.sort(incoming, new Comparator<SubmissionData>() {
			public int compare(SubmissionData s1, SubmissionData s2) {
				return s1.p2pFeedback.getValue().compareTo(s2.p2pFeedback.getValue());
			}
		});
	}

}
