package teammates.ui.controller;

import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorEvalSubmissionEditPageAction extends Action {
	
	private InstructorEvalSubmissionEditPageData data;
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
		Assumption.assertNotNull(evalName);
		
		String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL); 
		//Note: in InstructorEvalSubmissionEditPageData we use Common.Params.FROM_EMAIL instead
		Assumption.assertNotNull(studentEmail);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getEvaluation(courseId, evalName));
		
		data = new InstructorEvalSubmissionEditPageData(account);
		data.student = logic.getStudentForEmail(courseId, studentEmail);
		Assumption.assertNotNull(data.student);
		
		data.eval = logic.getEvaluation(courseId, evalName);
		
		try{
			data.submissions = logic.getSubmissionsForEvaluationFromStudent(courseId, evalName, data.student.email);
			
		} catch (InvalidParametersException e) {
			Assumption.fail("Invalid parameters not expected at this stage");
		}

		SubmissionAttributes.sortByReviewee(data.submissions);
		SubmissionAttributes.putSelfSubmissionFirst(data.submissions);
		
		statusToAdmin = "instructorEvalSubmissionEdit Page Load<br>"+ 
				"Editing <span class=\"bold\">" + studentEmail + "'s</span> Submission " +
				"<span class=\"bold\">("+ data.eval.name +")</span> " +
				"for Course <span class=\"bold\">[" + courseId + "]</span>";
		
		
		ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_EVAL_SUBMISSION_EDIT, data);
		return response;

	}
	
	
	
}
