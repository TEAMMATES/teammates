package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

public class InstructorEvalResultsPageAction extends Action {
	Logger log = Common.getLogger();

	@Override
	protected ActionResult execute() 
			throws EntityDoesNotExistException,	InvalidParametersException {
		
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		String evalName = getRequestParam(Common.PARAM_EVALUATION_NAME);
		Assumption.assertNotNull(evalName);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getEvaluation(courseId, evalName));
		
		InstructorEvalResultsPageData data = new InstructorEvalResultsPageData(account);
		
		data.evaluationResults = logic.getEvaluationResult(courseId, evalName);
		data.evaluationResults.sortForReportToInstructor();
				
		statusToUser.add(Common.MESSAGE_LOADING);
		statusToAdmin = "instructorEvalResults Page Load<br>" + "Viewing Results " +
				"for Evaluation <span class=\"bold\">" + evalName + "</span> " +
				"in Course <span class=\"bold\">[" + courseId + "]</span>";
			
		
		return createShowPageResult(Common.JSP_INSTRUCTOR_EVAL_RESULTS, data);

	}

}