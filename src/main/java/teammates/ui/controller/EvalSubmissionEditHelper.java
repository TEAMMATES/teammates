package teammates.ui.controller;

import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;

public class EvalSubmissionEditHelper extends Helper {
	public EvaluationData eval;
	public StudentData student;
	public List<SubmissionData> submissions;
	
	/**
	 * Returns the p2p comments depending whether it is for self or others, and
	 * if the comments is empty
	 * @param sub
	 * @return
	 */
	public String getP2PComments(SubmissionData sub) {
		String commentsString = EvalSubmissionEditHelper.escapeForHTML(sub.p2pFeedback.getValue());
		if (commentsString.trim().equals("")){
			if(sub.reviewee.equals(sub.reviewer)) {
				return "";
			} else {
				return "What I appreciate about you as a team member:\n\nAreas you can improve further:\n\nOther comments";
			}
		} else {
			return commentsString;
		}
	 		
	}
	/**
	 * Returns the section title of evaluation depending on whether it is
	 * an evaluation submission for self or for others.
	 * @param sub
	 * @return
	 */
	public String getEvaluationSectionTitle(SubmissionData sub){
		if(sub.reviewee.equals(sub.reviewer)){
			return "Self evaluation in Team [" + student.team + "]";
		} else {
			return "Evaluation for " + sub.revieweeName;
		}
	}
	
	/**
	 * Returns the justification instruction depending on whether it is
	 * an evaluation submission for self or for others.
	 * @param sub
	 * @return
	 */
	public String getJustificationInstr(SubmissionData sub){
		if(sub.reviewee.equals(sub.reviewer)){
			return "Comments about your contribution:<br />" +
					"(this will be shown to other teammates)";
		} else {
			return "Confidential comments about this teammate:<br />" + 
					"(not shown to the teammate)";
		}
	}
	
	/**
	 * Returns the comments (p2pfeedback) instructions depending on whether
	 * it is an evaluation submission for self or for others.
	 * @param sub
	 * @return
	 */
	public String getCommentsInstr(SubmissionData sub){
		if(sub.reviewee.equals(sub.reviewer)){
			return "Comments about team dynamics:<br />" +
					"(confidential)";
		} else {
			return "Your feedback to this teammate:<br />" + 
					"(shown anonymously to the teammate)";
		}
	}
	
	/**
	 * Returns the options for contribution share in a team.
	 * This will also select the one which is already selected as shown in the
	 * given SubmissionData. If the submission data is a new data, then by
	 * default "Not Sure" is chosen.
	 * @param sub
	 * @return
	 */
	public String getEvaluationOptions(SubmissionData sub){
		String result = "";
		if(sub.points==Common.POINTS_NOT_SUBMITTED ||
				sub.points==Common.UNINITIALIZED_INT){
			sub.points=Common.POINTS_NOT_SURE;
		}
		for(int i=200; i>=0; i-=10){
			result += "<option value=\"" + i + "\"" +
						(i==sub.points
						? "selected=\"selected\""
						: "") +
						">" + format(i) +
						"</option>\r\n";
		}
		result+="<option value=\"" + Common.POINTS_NOT_SURE + "\""
				+ (sub.points==-101 ? " selected=\"selected\"" : "") + ">" +
				"Not Sure</option>";
		return result;
	}
	
	private String format(int i) {
		if (i > 100)
			return "Equal share + " + (i - 100) + "%"; // Do more
		else if (i == 100)
			return "Equal share"; // Do same
		else if (i > 0)
			return "Equal share - " + (100 - i) + "%"; // Do less
		else
			return "0%"; // Do none
	}
}