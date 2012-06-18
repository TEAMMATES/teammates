package teammates.jsp;

import teammates.datatransfer.SubmissionData;

public class CoordEvalSubmissionEditHelper extends EvalSubmissionEditHelper{

	public String getEvaluationSectionTitle(SubmissionData sub){
		if(sub.reviewee.equals(sub.reviewer)){
			return escapeHTML(student.name) + "'s evaluation submission";
		} else {
			return "Evaluation for " + sub.revieweeName;
		}
	}
}
