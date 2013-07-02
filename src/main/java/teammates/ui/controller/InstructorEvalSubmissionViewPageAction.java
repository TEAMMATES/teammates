package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.logic.GateKeeper;

public class InstructorEvalSubmissionViewPageAction extends Action {
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParam(Config.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String evalName = getRequestParam(Config.PARAM_EVALUATION_NAME);
		Assumption.assertNotNull(evalName);
		
		String studentEmail = getRequestParam(Config.PARAM_STUDENT_EMAIL); 
		//Note: in InstructorEvalSubmissionEditPageData we use Common.PARAM_FROM_EMAIL instead
		Assumption.assertNotNull(studentEmail);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getEvaluation(courseId, evalName));
		
		InstructorEvalSubmissionViewPageData data = new InstructorEvalSubmissionViewPageData(account);
		
		try {
			
			data.student = logic.getStudentForEmail(courseId, studentEmail);
			data.evaluation = logic.getEvaluation(courseId, evalName);
			data.studentResult = logic.getEvaluationResultForStudent(courseId, evalName, studentEmail);
			
			statusToAdmin = "instructorEvalSubmissionView Page Load<br>" + 
					"Viewing <span class=\"bold\">" + studentEmail + "'s</span> submission " +
					"for Evaluation <span class=\"bold\">" + evalName + "</span> " +
					"for Course <span class=\"bold\">[" + courseId + "]</span>";
			
			return createShowPageResult(Config.JSP_INSTRUCTOR_EVAL_SUBMISSION_VIEW, data);
			
		} catch (InvalidParametersException e) {
			//TODO: some parts of this block can be extracted into a method and pulled up
			isError = true;
			statusToUser.add(e.getMessage());
			statusToAdmin = Config.LOG_SERVLET_ACTION_FAILURE + " : " + e.getMessage(); 
			
			return createShowPageResult(Config.JSP_STATUS_MESSAGE, data);
		}
		
	}
	
	
	
}
