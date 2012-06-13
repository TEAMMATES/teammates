package teammates.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;

import teammates.api.Common;

public class EvalResultData {



	public ArrayList<SubmissionData> incoming = new ArrayList<SubmissionData>();
	public ArrayList<SubmissionData> outgoing = new ArrayList<SubmissionData>();
	public ArrayList<SubmissionData> selfEvaluations = new ArrayList<SubmissionData>();

	public int claimedFromStudent = Common.UNINITIALIZED_INT;
	public int claimedToCoord = Common.UNINITIALIZED_INT;
	public int perceivedToCoord = Common.UNINITIALIZED_INT;
	public int perceivedToStudent = Common.UNINITIALIZED_INT;

	private static final Logger log = Common.getLogger();
	
	/** returns the self-evaluation selected from outgoing submissions*/
	public SubmissionData getSelfEvaluation(){
		for(SubmissionData s: outgoing){
			if(s.reviewee.equals(s.reviewer)){
				return s;
			}
		}
		return null;
	}

	public void sortOutgoingByStudentNameAscending() {
		Collections.sort(outgoing, new Comparator<SubmissionData>() {
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

	public String getOwnerEmail() {
		for(SubmissionData sb: outgoing){
			if(sb.reviewee.equals(sb.reviewer)){
				return sb.reviewer;
			}
		}
		return null;
	}
	
}
