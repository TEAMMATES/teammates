package teammates.ui.controller;

import teammates.common.datatransfer.SubmissionAttributes;

public class InstructorEvalSubmissionEditHelper extends EvalSubmissionEditHelper{

	public String getEvaluationSectionTitle(SubmissionAttributes sub){
		if(sub.reviewee.equals(sub.reviewer)){
			return escapeForHTML(student.name) + "'s evaluation submission";
		} else {
			return "Evaluation for " + sub.revieweeName;
		}
	}
}
