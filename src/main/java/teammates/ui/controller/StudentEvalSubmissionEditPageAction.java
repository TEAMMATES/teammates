package teammates.ui.controller;

import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

public class StudentEvalSubmissionEditPageAction extends Action {
	
	private StudentEvalSubmissionEditPageData data;
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
		Assumption.assertNotNull(evalName);
		
		if(notYetJoinedCourse(courseId, account.googleId)){
			return createPleaseJoinCourseResponse(courseId);
		}
		//No need to call GateKeeper because of the above redirect
		
		data = new StudentEvalSubmissionEditPageData(account);
		data.student = logic.getStudentForGoogleId(courseId, account.googleId);
		data.eval = logic.getEvaluation(courseId, evalName);
		Assumption.assertNotNull(data.eval);
		
		try{
			data.submissions = logic.getSubmissionsForEvaluationFromStudent(courseId, evalName, data.student.email);
		} catch (InvalidParametersException e) {
			Assumption.fail("Invalid parameters not expected at this stage");
		}

		SubmissionAttributes.sortByReviewee(data.submissions);
		SubmissionAttributes.putSelfSubmissionFirst(data.submissions);
		
		if(data.eval.getStatus() != EvalStatus.OPEN){
			statusToUser.add(Const.StatusMessages.EVALUATION_NOT_OPEN);
			data.disableAttribute = "disabled=\"disabled\"";
		}
		
		statusToAdmin = "studentEvalEdit Page Load<br>"+ 
				"Editing <span class=\"bold\">" + data.student.email + "'s</span> Submission " +
				"<span class=\"bold\">("+ data.eval.name +")</span> " +
				"for Course <span class=\"bold\">[" + courseId + "]</span>";
		
		ShowPageResult response = createShowPageResult(Const.ViewURIs.STUDENT_EVAL_SUBMISSION_EDIT, data);
		return response;

	}
	
}
