package teammates.ui.controller;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.EvaluationResultsBundle;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

public class InstructorEvalSubmissionViewPageAction extends Action {
	
	private InstructorEvalSubmissionEditPageData data;
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String evalName = getRequestParam(Common.PARAM_EVALUATION_NAME);
		Assumption.assertNotNull(evalName);
		
		String studentEmail = getRequestParam(Common.PARAM_STUDENT_EMAIL); 
		//Note: in InstructorEvalSubmissionEditPageData we use Common.PARAM_FROM_EMAIL instead
		Assumption.assertNotNull(studentEmail);
		
		new GateKeeper().verifyCourseInstructorOrAbove(courseId);
		
		InstructorEvalSubmissionViewPageData data = new InstructorEvalSubmissionViewPageData(account);
		
		try {
			
			data.student = logic.getStudentForEmail(courseId, studentEmail);
			data.evaluation = logic.getEvaluation(courseId, evalName);
			data.studentResult = logic.getEvaluationResultForStudent(courseId, evalName, studentEmail);
			
			statusToAdmin = "instructorEvalSubmissionView Page Load<br>" + 
					"Viewing <span class=\"bold\">" + studentEmail + "'s</span> submission " +
					"for Evaluation <span class=\"bold\">" + evalName + "</span> " +
					"for Course <span class=\"bold\">[" + courseId + "]</span>";
			
			return createShowPageResult(Common.JSP_INSTRUCTOR_EVAL_SUBMISSION_VIEW, data);
			
		} catch (InvalidParametersException e) {
			//TODO: some parts of this block can be extracted into a method and pulled up
			isError = true;
			statusToUser.add(e.getMessage());
			statusToAdmin = Common.LOG_SERVLET_ACTION_FAILURE + " : " + e.getMessage(); 
			
			return createShowPageResult(Common.JSP_STATUS_MESSAGE, data);
		}
		
	}
	
	
	
}
